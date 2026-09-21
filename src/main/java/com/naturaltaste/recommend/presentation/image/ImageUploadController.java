package com.naturaltaste.recommend.presentation.image;

import com.naturaltaste.recommend.application.usecase.image.ImageUploadResponse;
import com.naturaltaste.recommend.application.usecase.image.ImageUploadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadUseCase imageUploadUseCase;

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ImageUploadResponse upload(@RequestParam MultipartFile file) {
        return imageUploadUseCase.upload(file);
    }
}
