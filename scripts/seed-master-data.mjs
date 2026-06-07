/**
 * 主数据测试数据灌入脚本（通过 REST API 写入开发库 ai_store_manage）。
 * 覆盖：供应商 / 仓库 / 库区 / 托盘类型（ISO 6780 大中小三规格）。
 *
 * 用法：后端在 8080 启动后执行  node scripts/seed-master-data.mjs
 * 幂等：编码重复（409）视为已存在，自动跳过并复用已有记录。
 */
const BASE = process.env.API_BASE || 'http://localhost:8080';

async function post(path, body) {
  const res = await fetch(BASE + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (res.status === 201) return res.json();
  if (res.status === 409) return { __duplicate: true };
  const text = await res.text();
  throw new Error(`POST ${path} -> ${res.status}: ${text}`);
}

async function findByCode(path, code) {
  const res = await fetch(`${BASE}${path}?keyword=${encodeURIComponent(code)}&pageSize=100`);
  const data = await res.json();
  return (data.items || []).find((it) => it.code === code) || null;
}

/** 创建或复用（按 code 唯一）。返回带 id 的记录。 */
async function ensure(path, body, label) {
  const created = await post(path, body);
  if (!created.__duplicate) {
    console.log(`  + ${label}：${body.code} ${body.name}`);
    return created;
  }
  const existing = await findByCode(path, body.code);
  console.log(`  = ${label}：${body.code}（已存在，复用 id=${existing?.id}）`);
  return existing;
}

async function main() {
  console.log('▶ 供应商');
  const suppliers = [
    { code: 'SUP-001', name: '中石化华东树脂供应商', address: '上海市金山区石化大道 1 号', contact: '王经理', phone: '021-58880001', email: 'sales@sinopec-east.com', remark: 'LLDPE 拉伸膜专用树脂' },
    { code: 'SUP-002', name: '江苏华美包装材料有限公司', address: '江苏省苏州市工业园区包装路 18 号', contact: '李主管', phone: '0512-66660002', email: 'huamei@pack.com', remark: '纸箱 / 纸管供应' },
    { code: 'SUP-003', name: '宁波塑源添加剂有限公司', address: '浙江省宁波市北仑区化工园 9 号', contact: '张工', phone: '0574-99990003', email: 'add@suyuan.com', remark: '色母 / 增粘剂' },
  ];
  for (const s of suppliers) await ensure('/api/suppliers', s, '供应商');

  console.log('▶ 仓库');
  const warehouses = [
    { code: 'WH-RAW', name: '原料仓库', type: 'RAW', remark: '存放 LLDPE 树脂等原材料' },
    { code: 'WH-SEMI', name: '半成品仓库', type: 'SEMI', remark: '存放母卷 / 半成品' },
    { code: 'WH-FIN', name: '成品仓库', type: 'FINISHED', remark: '存放成品缠绕膜，待发货' },
  ];
  const whMap = {};
  for (const w of warehouses) {
    const rec = await ensure('/api/warehouses', w, '仓库');
    whMap[w.code] = rec.id;
  }

  console.log('▶ 库区（关联仓库，编码在仓库内唯一）');
  const zones = [
    { wh: 'WH-RAW', code: 'Z-A', name: '收货暂存区', type: '收货' },
    { wh: 'WH-RAW', code: 'Z-B', name: '树脂存储区', type: '存储' },
    { wh: 'WH-SEMI', code: 'Z-A', name: '母卷存储区', type: '存储' },
    { wh: 'WH-FIN', code: 'Z-A', name: '成品拣货区', type: '拣货' },
    { wh: 'WH-FIN', code: 'Z-B', name: '发货月台区', type: '发货' },
  ];
  for (const z of zones) {
    const warehouseId = whMap[z.wh];
    const created = await post('/api/zones', { warehouseId, code: z.code, name: z.name, type: z.type });
    console.log(`  ${created.__duplicate ? '=' : '+'} 库区：${z.wh}/${z.code} ${z.name}`);
  }

  console.log('▶ 托盘类型（ISO 6780 国际标准 大/中/小）');
  const pallets = [
    { code: 'PLT-L', name: '大托盘 1200×1000', length: 1200, width: 1000, tareWeight: 25, maxLoad: 1500, maxStack: 5, remark: 'ISO6780 标准（北美 / 通用）' },
    { code: 'PLT-M', name: '欧标托盘 1200×800', length: 1200, width: 800, tareWeight: 25, maxLoad: 1500, maxStack: 5, remark: 'ISO6780 / 欧标 EUR1（EPAL）' },
    { code: 'PLT-S', name: '小托盘 800×600', length: 800, width: 600, tareWeight: 12, maxLoad: 500, maxStack: 4, remark: 'ISO6780 半欧托（EUR6）' },
  ];
  for (const p of pallets) await ensure('/api/pallet-types', p, '托盘');

  console.log('\n✓ 主数据灌入完成');
}

main().catch((e) => {
  console.error('✗ 灌入失败：', e.message);
  process.exit(1);
});
