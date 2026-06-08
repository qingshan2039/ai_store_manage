# VPS 部署指南（Ubuntu + Docker Compose + Nginx + Certbot）

> 目标：把 `ai_store_manage` 部署到一台空白的 Ubuntu 22.04 / 24.04 VPS 上，使用 Docker Compose 编排 PostgreSQL + 后端 jar + 前端静态站点 + 反向代理，并通过 Nginx + Certbot 启用 HTTPS。
>
> 适用范围：单机 MVP / 内测环境。生产高可用、读写分离、多副本另作方案。
>
> 数据库为 **PostgreSQL 16**，表结构由后端 **Flyway**（迁移脚本 `V1…V8`）启动时自动建立与演进——**无需手写 init SQL**。上传图片落本地磁盘，需挂持久化卷（见下文 uploads 卷）。

---

## 目录

1. [前置准备](#1-前置准备)
2. [VPS 系统初始化](#2-vps-系统初始化)
3. [安装 Docker 与 Compose](#3-安装-docker-与-compose)
4. [项目部署目录布局](#4-项目部署目录布局)
5. [新增构建文件](#5-新增构建文件)
   - 5.1 后端 Dockerfile
   - 5.2 前端 Dockerfile
   - 5.3 .dockerignore
   - 5.4 Nginx 反代配置
   - 5.5 docker-compose.yml
   - 5.6 .env 环境变量
   - 5.7 数据库初始化（Flyway 自动迁移）
6. [上传代码到 VPS](#6-上传代码到-vps)
7. [首次启动（HTTP 模式）](#7-首次启动http-模式)
8. [配置 HTTPS（Nginx + Certbot）](#8-配置-https-nginx--certbot)
9. [验证部署](#9-验证部署)
10. [日常运维](#10-日常运维)
11. [故障排查](#11-故障排查)
12. [安全加固清单](#12-安全加固清单)

---

## 部署架构

```
                    互联网
                       │
                  80 │ 443
                       ▼
   ┌───────────────────────────────────────┐
   │  nginx 容器（反代 + HTTPS 终结）        │
   │  + /etc/letsencrypt + /var/www/certbot │
   └────────┬──────────────────┬───────────┘
            │ /api/*           │ /*
            ▼                  ▼
       ┌─────────┐         ┌─────────┐
       │  api    │         │  web    │  ← React 静态产物
       │  :8080  │         │  :80    │     由内层 nginx 服务
       └──┬───┬──┘         └─────────┘
          │   │ 上传图片落盘 → data/uploads（卷），经 /api/files/** 提供
          │   └──────────────► /app/uploads
          │ JDBC
          ▼
       ┌──────────┐
       │ postgres │  ← 持久化到 data/postgres；表由后端 Flyway 自动迁移
       │  :5432   │
       └──────────┘
   只有 nginx 容器对外暴露 80/443；其余只在 docker 内部网络互通
```

---

## 1. 前置准备

| 项 | 最低要求 | 建议 |
|---|---|---|
| **VPS 规格** | 2 vCPU / 2 GB RAM / 20 GB SSD | 2 vCPU / 4 GB RAM / 40 GB SSD |
| **操作系统** | Ubuntu 22.04 LTS | Ubuntu 24.04 LTS |
| **网络** | 公网 IPv4，开放 22/80/443 | 同左 + 关闭其它端口 |
| **域名** | 已注册并能改 DNS | 已把 A 记录指向 VPS IP |
| **本机工具** | SSH 客户端、git | scp / rsync |

**域名解析检查**（替换 `yourdomain.com` 与 `1.2.3.4`）：

```bash
# 在本机执行，确认域名已经解析到 VPS
dig +short yourdomain.com         # 应返回 1.2.3.4
dig +short www.yourdomain.com     # 如果需要 www 子域，同上
```

> ⚠️ Certbot 申请证书要求域名 **已经** 解析到本机 IP，否则验证会失败。先把 DNS 改好再继续后面的步骤，TTL 默认 600 秒，等几分钟。

---

## 2. VPS 系统初始化

以下命令在 VPS 上以 `root` 或具备 sudo 的普通用户执行。

```bash
# 1) 更新系统
sudo apt update && sudo apt upgrade -y

# 2) 时区设为上海（应用时区也是 Asia/Shanghai）
sudo timedatectl set-timezone Asia/Shanghai

# 3) 创建非 root 部署用户（如果当前是 root）
sudo adduser deploy
sudo usermod -aG sudo deploy
# 把本机 SSH 公钥写入 /home/deploy/.ssh/authorized_keys 后切换登录
# 后续操作建议都用 deploy 用户

# 4) 防火墙：只放行 22 / 80 / 443
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow OpenSSH
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
sudo ufw status

# 5) 禁用 root SSH 登录、禁用密码登录（确保已配好 deploy 用户的 key 再做）
sudo sed -i 's/^#\?PermitRootLogin.*/PermitRootLogin no/' /etc/ssh/sshd_config
sudo sed -i 's/^#\?PasswordAuthentication.*/PasswordAuthentication no/' /etc/ssh/sshd_config
sudo systemctl restart ssh
```

---

## 3. 安装 Docker 与 Compose

按 Docker 官方源安装（apt 自带版本太旧）：

```bash
# 卸载可能存在的旧版本
sudo apt remove -y docker docker-engine docker.io containerd runc 2>/dev/null || true

# 安装依赖
sudo apt install -y ca-certificates curl gnupg

# 添加 Docker 官方 GPG key
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
  sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# 添加仓库
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 安装 docker-ce + compose 插件
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 把 deploy 加到 docker 组（免 sudo 用 docker）
sudo usermod -aG docker $USER
# 重新登录或执行 newgrp docker 让组生效
newgrp docker

# 验证
docker --version            # Docker version 27.x
docker compose version      # Docker Compose version v2.x
docker run --rm hello-world
```

---

## 4. 项目部署目录布局

在 VPS 上规划一份持久化目录结构，**不要直接在 git checkout 出来的目录里挂卷**：

```
/opt/ai-store-manage/
├── app/                     # git clone 的项目代码
│   ├── api/
│   ├── web/
│   ├── api-contract/
│   └── deploy/              # ← 仓库内已含的部署相关文件
│       ├── Dockerfile.api
│       ├── Dockerfile.web
│       ├── docker-compose.yml
│       └── nginx/
│           ├── default.conf
│           └── web.conf
├── data/                    # 持久化数据（**绝不进 git**）
│   ├── postgres/            # PostgreSQL 数据目录
│   ├── uploads/             # 上传图片（后端落盘，经 /api/files/** 访问）
│   ├── certbot/conf/        # Let's Encrypt 证书
│   └── certbot/www/         # ACME 验证 webroot
└── logs/                    # 容器日志输出
```

> 数据库不再需要 `init.sql`——表结构由后端 Flyway 自动迁移，PostgreSQL 容器仅凭 `POSTGRES_DB` 建好空库即可。

```bash
sudo mkdir -p /opt/ai-store-manage/{app,data/postgres,data/uploads,data/certbot/conf,data/certbot/www,logs}
sudo chown -R $USER:$USER /opt/ai-store-manage
```

---

## 5. 新增构建文件

仓库 `deploy/` 目录下已包含以下构建文件（Dockerfile 因构建上下文需放仓库根，由 `.dockerignore` 配合）；下面逐一说明，按需核对/修改即可。**唯一需要你手工创建的是 `deploy/.env`**（含密码，不进 git）。

### 5.1 后端 Dockerfile

放在项目根：`app/Dockerfile.api`

```dockerfile
# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# 缓存依赖：先只 COPY pom.xml 拉依赖
COPY api/pom.xml .
RUN mvn -B dependency:go-offline

# 再 COPY 源码做编译
COPY api/src ./src
RUN mvn -B clean package -DskipTests

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 创建非 root 用户
RUN useradd -r -u 1001 -g root appuser

# 拷贝 fat jar
COPY --from=build /build/target/*.jar app.jar

# 健康检查 endpoint 已经在 actuator 暴露
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75", \
  "-Duser.timezone=Asia/Shanghai", \
  "-jar", "app.jar"]
```

### 5.2 前端 Dockerfile

放在项目根：`app/Dockerfile.web`

```dockerfile
# ===== Build stage =====
FROM node:22-alpine AS build
WORKDIR /build

# 缓存 node_modules
COPY web/package.json web/package-lock.json* ./
# 没有 lockfile 时退化为 npm install
RUN npm ci 2>/dev/null || npm install

# 构建
COPY web/. .
RUN npm run build

# ===== Runtime stage =====
FROM nginx:1.27-alpine
# 替换默认 nginx 配置，使其支持 SPA history fallback
COPY deploy/nginx/web.conf /etc/nginx/conf.d/default.conf
COPY --from=build /build/dist /usr/share/nginx/html
EXPOSE 80
```

### 5.3 .dockerignore

放在项目根：`app/.dockerignore`

```
.git
.github
.idea
.vscode
.claude
.codegraph
.agents
.m2-local
**/target
**/node_modules
**/dist
**/.env.local
**/*.log
docs
api-contract
data
logs
```

### 5.4 Nginx 反代配置

#### 内层 web 容器自己的 nginx（SPA history fallback）

`app/deploy/nginx/web.conf`

```nginx
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # SPA 路由：所有未命中的 path 都回到 index.html
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源长缓存
    location ~* \.(?:js|css|woff2?|ttf|eot|svg|png|jpg|jpeg|gif|webp|ico)$ {
        expires 7d;
        add_header Cache-Control "public, immutable";
    }
}
```

#### 外层反向代理（HTTP 阶段）

`app/deploy/nginx/default.conf`

```nginx
# 上游
upstream api_backend {
    server api:8080;
}
upstream web_backend {
    server web:80;
}

# HTTP 服务：先只承载 ACME 验证和 80 → 443 跳转
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;

    # ACME http-01 challenge
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    # 在拿到证书前，先用 HTTP 代理跑通流程（拿到证书后改成 301 跳转）
    location /api/ {
        proxy_pass         http://api_backend;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
    }

    location / {
        proxy_pass         http://web_backend;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
    }
}
```

> 第 8 节会用 `default.https.conf` 替换它，开启 HTTPS。

### 5.5 docker-compose.yml

`app/deploy/docker-compose.yml`

```yaml
name: ai-store-manage

services:
  postgres:
    image: postgres:16-alpine
    container_name: asm-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      TZ: Asia/Shanghai
    volumes:
      - /opt/ai-store-manage/data/postgres:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $${POSTGRES_USER} -d $${POSTGRES_DB}"]
      interval: 10s
      timeout: 5s
      retries: 10
    networks:
      - asm-net
    # 不暴露端口到宿主机，只在容器网络内可达。表结构由后端 Flyway（V1…V8）启动时自动迁移，无需 init.sql。

  api:
    build:
      context: ..
      dockerfile: Dockerfile.api
    image: ai-store-manage/api:latest
    container_name: asm-api
    restart: unless-stopped
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      # Spring Boot relaxed binding：覆盖 application.yml 中的 datasource（容器内连 postgres:5432）
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      SPRING_DATASOURCE_DRIVER_CLASS_NAME: org.postgresql.Driver
      # 兼容 application.yml 已有的占位
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      # 上传图片落盘目录（与下方卷一致；应用据此存储并经 /api/files/** 提供访问）
      APP_UPLOAD_DIR: /app/uploads
      JAVA_TOOL_OPTIONS: "-Xms256m -Xmx1024m"
    volumes:
      - /opt/ai-store-manage/logs:/app/logs
      - /opt/ai-store-manage/data/uploads:/app/uploads
    networks:
      - asm-net

  web:
    build:
      context: ..
      dockerfile: Dockerfile.web
    image: ai-store-manage/web:latest
    container_name: asm-web
    restart: unless-stopped
    networks:
      - asm-net

  nginx:
    image: nginx:1.27-alpine
    container_name: asm-nginx
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/default.conf:/etc/nginx/conf.d/default.conf:ro
      - /opt/ai-store-manage/data/certbot/conf:/etc/letsencrypt:ro
      - /opt/ai-store-manage/data/certbot/www:/var/www/certbot:ro
    depends_on:
      - api
      - web
    networks:
      - asm-net

  certbot:
    image: certbot/certbot:latest
    container_name: asm-certbot
    restart: unless-stopped
    volumes:
      - /opt/ai-store-manage/data/certbot/conf:/etc/letsencrypt
      - /opt/ai-store-manage/data/certbot/www:/var/www/certbot
    # 启动后每 12 小时尝试一次 renew；首次签发用一次性命令（见第 8 节）
    entrypoint: "/bin/sh -c 'trap exit TERM; while :; do certbot renew --quiet; sleep 12h & wait $${!}; done;'"
    networks:
      - asm-net

networks:
  asm-net:
    driver: bridge
```

### 5.6 .env 环境变量

`app/deploy/.env`（**不要进 git**，自己创建）：

```dotenv
# === 数据库（PostgreSQL）===
POSTGRES_DB=ai_store_manage
POSTGRES_USER=ai_store
POSTGRES_PASSWORD=请改成强随机字符串_不少于20位

# === 域名与证书 ===
DOMAIN=yourdomain.com
CERTBOT_EMAIL=you@example.com
```

把 `.env` 加进 `.gitignore`（项目根已有 .gitignore，往里追加 `deploy/.env` 即可）。

### 5.7 数据库初始化（Flyway 自动迁移，无需手写 SQL）

本项目用 **Flyway** 管理表结构：后端启动时自动执行 `api/src/main/resources/db/migration/` 下的版本化脚本（`V1…V8`），建表并灌入种子数据——**所以不需要 init.sql，也不要手写 DDL**。PostgreSQL 容器只要用 `POSTGRES_DB` 建好空库即可，其余交给应用。

- `V1` 建 `sys_user` / `sys_department` 并灌部门种子；`V2`/`V3` 顾客与送货地址；`V4` 供应商/仓库/库区/托盘类型；`V5` 物料目录（品类/SPU/SKU）；`V6` 包装与条码；`V7` 库存与托盘（库位/LPN/库存）；`V8` 运输（车辆/打油/打卡，含 4 车 / 4 司机 / 4 跟车员 / 近 7 天打卡种子）。
- **演进规则**：已发布的迁移文件不可改；新 schema 变更一律新增 `V9__xxx.sql`，启动时自动应用。
- 验证迁移：`docker compose logs api` 中会出现 `Successfully applied N migration(s)` 与 `now at version v8`。

> ⚠️ **登录**：当前登录为前端 mock，后端 `SecurityConfig` 全放行、Auth（`/api/auth/login` + JWT）尚未闭环，**没有内置管理员账号**。公网暴露前务必加访问控制（见第 12 节）。

---

## 6. 上传代码到 VPS

两种方式任选其一。

**方式 A：git clone（推荐）**

```bash
cd /opt/ai-store-manage
git clone <your-repo-url> app
cd app
# 切到要部署的分支
git checkout main
```

**方式 B：本地 rsync**

```bash
# 在本机执行
rsync -avz --exclude='.git' --exclude='node_modules' --exclude='target' \
  ./ deploy@<vps-ip>:/opt/ai-store-manage/app/
```

把本指南 §5 列的 7 个文件放到对应路径（如果直接 git 仓库里已包含就跳过）：

```
app/Dockerfile.api
app/Dockerfile.web
app/.dockerignore
app/deploy/nginx/web.conf
app/deploy/nginx/default.conf
app/deploy/docker-compose.yml
app/deploy/.env                 ← 手工创建，不要进 git
```

> 数据库无需 init.sql：表结构由后端 Flyway 启动时自动迁移（见 §5.7）。

把 `default.conf` 里的 `yourdomain.com` 替换成你的真实域名：

```bash
cd /opt/ai-store-manage/app/deploy
sed -i 's/yourdomain\.com/your-real-domain.com/g' nginx/default.conf
```

---

## 7. 首次启动（HTTP 模式）

```bash
cd /opt/ai-store-manage/app/deploy

# 1) 编辑 .env，填强密码和邮箱
nano .env

# 2) 构建镜像（首次较慢，Maven 拉依赖大约 5-10 分钟）
docker compose build

# 3) 启动 postgres + api + web + nginx，certbot 暂时不要起（还没证书）
docker compose up -d postgres api web nginx

# 4) 看 postgres 是否初始化完成
docker compose logs -f postgres
# 看到 "database system is ready to accept connections" 后 Ctrl+C 退出

# 5) 看 api 是否成功连接 PostgreSQL 并完成 Flyway 迁移
docker compose logs -f api
# 看到 "Successfully applied N migration(s) ... now at version v8"
# 再看到 "Started AiStoreManageApplication" 即可

# 6) 浏览器访问 http://yourdomain.com 应该能看到登录页
# 接口探活
curl http://yourdomain.com/api/users
```

如果到这一步页面能打开、接口能返回（哪怕是空数据或登录页），说明 docker 编排已经跑通。

---

## 8. 配置 HTTPS（Nginx + Certbot）

### 8.1 首次签发证书

```bash
cd /opt/ai-store-manage/app/deploy

# ⚠️ 必须加 --entrypoint certbot：compose 里 certbot 服务的 entrypoint 是「每 12h renew」的死循环，
#    不覆盖它的话，certonly 参数会被当垃圾忽略、容器空跑卡住，证书永远签不出来（日志还是空的）。
# 第一次用 --staging 试一下，避免触发 LE 速率限制
docker compose run --rm --entrypoint certbot certbot certonly \
  --webroot --webroot-path=/var/www/certbot \
  --email "$(grep CERTBOT_EMAIL .env | cut -d= -f2)" \
  --agree-tos --no-eff-email \
  --staging \
  -d yourdomain.com -d www.yourdomain.com

# 成功后删掉 staging 目录，正式签发
sudo rm -rf /opt/ai-store-manage/data/certbot/conf/live/yourdomain.com
sudo rm -rf /opt/ai-store-manage/data/certbot/conf/archive/yourdomain.com
sudo rm -f /opt/ai-store-manage/data/certbot/conf/renewal/yourdomain.com.conf

docker compose run --rm --entrypoint certbot certbot certonly \
  --webroot --webroot-path=/var/www/certbot \
  --email "$(grep CERTBOT_EMAIL .env | cut -d= -f2)" \
  --agree-tos --no-eff-email \
  -d yourdomain.com -d www.yourdomain.com
```

成功后 `/opt/ai-store-manage/data/certbot/conf/live/yourdomain.com/` 下会出现 `fullchain.pem` 与 `privkey.pem`。

### 8.2 切换到 HTTPS 配置

把 `app/deploy/nginx/default.conf` **整体替换**为下面这份（再把 `yourdomain.com` 改成你的域名）：

```nginx
upstream api_backend {
    server api:8080;
}
upstream web_backend {
    server web:80;
}

# 80 → 443 跳转 + ACME challenge
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://$host$request_uri;
    }
}

# HTTPS
server {
    listen 443 ssl;
    http2  on;
    server_name yourdomain.com www.yourdomain.com;

    ssl_certificate     /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options    "nosniff"           always;
    add_header X-Frame-Options           "SAMEORIGIN"        always;
    add_header Referer-Policy            "strict-origin-when-cross-origin" always;

    # 后端 API
    location /api/ {
        proxy_pass         http://api_backend;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
        client_max_body_size 10m;
    }

    # 前端静态
    location / {
        proxy_pass         http://web_backend;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
    }
}
```

重载 nginx（不需要重启容器）：

```bash
docker compose exec nginx nginx -t      # 配置校验
docker compose exec nginx nginx -s reload
```

### 8.3 启动 certbot 自动续期

```bash
docker compose up -d certbot
# 它会每 12 小时尝试 renew 一次，证书到期前 30 天会自动续。
# nginx 容器挂的是 :ro 的同目录，所以续期后下次 nginx reload 自动生效。
```

可选：每周自动 reload 一次 nginx，保证拿到新证书：

```bash
# 在宿主机加 crontab -e
0 4 * * 0 docker compose -f /opt/ai-store-manage/app/deploy/docker-compose.yml exec -T nginx nginx -s reload
```

---

## 9. 验证部署

```bash
# 1) 全部容器健康
docker compose ps
# STATE 列都应是 Up (healthy) 或 Up

# 2) 后端健康检查
curl -fsS https://yourdomain.com/actuator/health
# {"status":"UP",...}

# 3) 用户列表（MVP 阶段无鉴权，能直接访问）
curl -fsS https://yourdomain.com/api/users | head

# 4) 浏览器访问 https://yourdomain.com
#    - 应看到登录页
#    - DevTools Network 看 /api/users 走的是 https + 同源

# 5) 证书等级
curl -vI https://yourdomain.com 2>&1 | grep -i "ssl\|tls\|issuer"
```

---

## 10. 日常运维

### 10.1 启停与日志

```bash
cd /opt/ai-store-manage/app/deploy

docker compose ps                # 状态
docker compose logs -f api       # 跟踪后端日志
docker compose logs --tail=200 nginx
docker compose restart api       # 重启某个服务
docker compose down              # 全停（数据卷不删）
docker compose up -d             # 全起
```

### 10.2 升级（拉新代码 → 重建 → 滚动起来）

```bash
cd /opt/ai-store-manage/app
git pull

cd deploy
docker compose build api web         # 只重建变化的镜像
docker compose up -d api web         # 滚动替换；postgres/nginx 不动
docker image prune -f                # 清理悬挂镜像
```

> schema 变更由后端 **Flyway 自动迁移**：新版本只需在 `api/src/main/resources/db/migration/` 新增 `V9__xxx.sql`（已发布的迁移文件不可改），重建并重启 api 后启动时自动应用，无需手工执行 SQL。迁移结果见 `docker compose logs api` 的 `Successfully applied ... migration(s)`。

### 10.3 数据库备份

> `pg_dump` 在容器内执行，密码经 `PGPASSWORD` 传入，用户/库名取自 `.env`。除数据库外，**上传图片目录 `data/uploads/` 也要一并备份**（落在宿主机磁盘，不在数据库里）。

```bash
# 单次手动备份
docker compose exec -T -e PGPASSWORD="$(grep POSTGRES_PASSWORD .env | cut -d= -f2)" postgres \
  pg_dump -U "$(grep POSTGRES_USER .env | cut -d= -f2)" "$(grep POSTGRES_DB .env | cut -d= -f2)" \
  > /opt/ai-store-manage/backups/db-$(date +%F-%H%M).sql

# 自动每天 03:00 备份 + 保留 14 天（crontab -e）
0 3 * * * cd /opt/ai-store-manage/app/deploy && \
  docker compose exec -T -e PGPASSWORD="$(grep POSTGRES_PASSWORD .env | cut -d= -f2)" postgres \
  pg_dump -U "$(grep POSTGRES_USER .env | cut -d= -f2)" "$(grep POSTGRES_DB .env | cut -d= -f2)" \
  | gzip > /opt/ai-store-manage/backups/db-$(date +\%F).sql.gz && \
  find /opt/ai-store-manage/backups -name "db-*.sql.gz" -mtime +14 -delete

# 上传图片目录备份（与数据库同一时间点）
0 3 * * * tar czf /opt/ai-store-manage/backups/uploads-$(date +\%F).tar.gz \
  -C /opt/ai-store-manage/data uploads
```

### 10.4 进 PostgreSQL CLI（psql）

```bash
docker compose exec -e PGPASSWORD="$(grep POSTGRES_PASSWORD .env | cut -d= -f2)" postgres \
  psql -U "$(grep POSTGRES_USER .env | cut -d= -f2)" "$(grep POSTGRES_DB .env | cut -d= -f2)"
```

---

## 11. 故障排查

| 现象 | 排查动作 |
|---|---|
| `docker compose build` 在 Maven 阶段卡很久 | 首次拉依赖正常需要 5–10 分钟；想加速可在 `Dockerfile.api` 的 `mvn` 命令前加阿里云镜像 `RUN mkdir -p /root/.m2 && echo '<settings>...</settings>' > /root/.m2/settings.xml` |
| api 启动报 `Connection refused` / `FATAL: the database system is starting up` | 大概率 postgres 还没就绪。检查 `docker compose logs postgres`；compose 文件已经用 healthcheck（`pg_isready`）+ depends_on 处理，但首次初始化可能要 10–30s |
| `502 Bad Gateway` | `docker compose ps` 看 api/web 是不是 Up。再 `docker compose logs api` 看堆栈 |
| Certbot 报 `Connection refused` / `unauthorized` | 域名 DNS 还没生效，或者 nginx 的 80 端口没正确转发 `/.well-known/`。先 `curl http://yourdomain.com/.well-known/acme-challenge/test` 看是否返回 nginx 的 404（说明路径通） |
| HTTPS 页面打不开 / 证书无效 | 检查 `data/certbot/conf/live/<domain>/fullchain.pem` 是否存在；`docker compose exec nginx nginx -t` 看配置是否错 |
| 前端能打开但 API 都 404 | nginx 反代 `location /api/` 一定要带尾斜杠（或与 upstream 的 trailing 一致）；后端的所有路径都以 `/api/` 起头，直接 proxy_pass `http://api_backend` 即可 |
| Compose 起不来报 `network already exists` | `docker network prune` 后重启 |
| api 启动报 `relation ... does not exist` / Flyway 校验失败 | 多半是 `data/postgres` 里残留了旧库或迁移历史与代码不一致。内测环境可 `docker compose down` 后 `sudo rm -rf /opt/ai-store-manage/data/postgres` 重新初始化（**会清空数据**）；正式环境改为新增 `V9` 迁移修复 |
| 磁盘满了 | `docker system df` 看占用；`docker image prune -af` 清旧镜像；`data/postgres/` 与 `data/uploads/`（上传图片）会随使用增长属正常 |

---

## 12. 安全加固清单

> 当前后端 [SecurityConfig](api/src/main/java/com/aistore/config/SecurityConfig.java) 是 **全放行的 MVP 配置**。在 Auth 闭环（`/api/auth/login` + JWT）落地之前，**强烈建议不要把这套部署暴露到完全公网**。临时缓解措施可任选：

- [ ] 在 `default.conf` 的 `/api/` 块加 `allow <你的固定 IP>; deny all;`，只让自己访问
- [ ] 或者用 nginx Basic Auth：
  ```nginx
  auth_basic "Restricted";
  auth_basic_user_file /etc/nginx/.htpasswd;
  ```
  生成口令文件：`sudo htpasswd -c /etc/nginx/.htpasswd admin`，把文件挂进 nginx 容器
- [ ] `.env` 的 `POSTGRES_PASSWORD` 用 `openssl rand -base64 32` 生成（postgres 容器不对外暴露端口，但强密码仍是底线）
- [ ] Auth 闭环（`/api/auth/login` + JWT）落地后，移除全放行的 [SecurityConfig](api/src/main/java/com/aistore/config/SecurityConfig.java)；当前无内置管理员账号，登录为前端 mock
- [ ] `.env`、`data/`、`logs/` 都不能进 git
- [ ] 把 `application.yml` 里的 `management.endpoint.health.show-details: always` 在生产改成 `when-authorized`，避免暴露过多内部信息
- [ ] VPS 安装 fail2ban（`sudo apt install fail2ban`）防 SSH 暴力破解
- [ ] 配置 ufw 默认 deny incoming 已在 §2 做过，确认 `ufw status` 只列 22/80/443
- [ ] 定期看 `docker compose logs nginx` 检查异常请求
- [ ] 数据库备份要异地保存（rclone 到对象存储）

---

## 附录：一键速查命令

```bash
# 部署根目录
cd /opt/ai-store-manage/app/deploy

# 启动/停止/重启
docker compose up -d
docker compose down
docker compose restart api

# 日志
docker compose logs -f
docker compose logs -f api

# 进容器
docker compose exec api sh
docker compose exec -e PGPASSWORD="$(grep POSTGRES_PASSWORD .env | cut -d= -f2)" postgres \
  psql -U "$(grep POSTGRES_USER .env | cut -d= -f2)" "$(grep POSTGRES_DB .env | cut -d= -f2)"

# 重新拉证书（手动）
docker compose run --rm certbot renew

# 重载 nginx
docker compose exec nginx nginx -s reload
```
