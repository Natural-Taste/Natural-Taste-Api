package com.naturaltaste.recommend.application.usecase.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadUseCase {

    ImageUploadResponse upload(MultipartFile file);
}
