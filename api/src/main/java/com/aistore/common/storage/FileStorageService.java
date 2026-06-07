package com.aistore.common.storage;

import com.aistore.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * 本地磁盘文件存储服务。
 * 将上传图片保存到 {app.upload.dir}/yyyy/MM/ 下（UUID 重命名），返回可访问 URL（{base-url}/yyyy/MM/xxx.ext）。
 * 仅接受图片类型并限制大小；后续如需对象存储（S3/OSS），替换本类实现即可。
 */
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");
    private static final long MAX_SIZE = 10L * 1024 * 1024; // 10MB

    private final String uploadDir;
    private final String baseUrl;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir,
                              @Value("${app.upload.base-url:/api/files}") String baseUrl) {
        this.uploadDir = uploadDir;
        this.baseUrl = baseUrl;
    }

    /** 保存上传文件并返回访问 URL。文件为空 / 超限 / 非图片时抛 BusinessException（→ 400）。 */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("FILE_EMPTY", "上传文件为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("FILE_TOO_LARGE", "文件超出大小限制（最大 10MB）");
        }
        String ext = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("FILE_TYPE_NOT_ALLOWED", "仅支持图片文件（jpg/png/gif/webp/bmp）");
        }

        LocalDate today = LocalDate.now();
        String subDir = String.format("%04d/%02d", today.getYear(), today.getMonthValue());
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        try {
            Path dir = Paths.get(uploadDir, subDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new BusinessException("FILE_STORE_FAILED", "文件保存失败");
        }

        return baseUrl + "/" + subDir + "/" + filename;
    }

    private static String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "";
    }
}
