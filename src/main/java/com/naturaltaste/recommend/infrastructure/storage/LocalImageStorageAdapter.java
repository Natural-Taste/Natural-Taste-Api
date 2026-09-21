package com.naturaltaste.recommend.infrastructure.storage;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.port.ImageStoragePort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Profile("local")
public class LocalImageStorageAdapter implements ImageStoragePort {

    private final Path uploadPath;

    public LocalImageStorageAdapter(@Value("${app.image.local.upload-dir:uploads/images}") String uploadDir) {
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file) {
        try {
            Files.createDirectories(uploadPath);
            String filename = UUID.randomUUID() + extension(file.getOriginalFilename());
            file.transferTo(uploadPath.resolve(filename));
            return "/uploads/images/" + filename;
        } catch (IOException exception) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    private String extension(String filename) {
        if (filename == null) {
            return "";
        }
        int index = filename.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return filename.substring(index).toLowerCase();
    }
}
