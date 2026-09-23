package com.naturaltaste.recommend.application.usecase.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.port.ImageStoragePort;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ImageUploadServiceTest {

    @Mock
    private ImageStoragePort imageStoragePort;

    @InjectMocks
    private ImageUploadService imageUploadService;

    @Test
    void uploadStoresImageFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "food.jpg",
                "image/jpeg",
                "image".getBytes()
        );
        given(imageStoragePort.store(file)).willReturn("/uploads/images/food.jpg");

        ImageUploadResponse response = imageUploadService.upload(file);

        assertThat(response.imageUrl()).isEqualTo("/uploads/images/food.jpg");
        verify(imageStoragePort).store(file);
    }

    @Test
    void uploadRejectsNonImageFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "memo.txt",
                "text/plain",
                "text".getBytes()
        );

        assertThatThrownBy(() -> imageUploadService.upload(file))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void uploadRejectsImageContentTypeWithUnsupportedExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "food.gif",
                "image/gif",
                "image".getBytes()
        );

        assertThatThrownBy(() -> imageUploadService.upload(file))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_IMAGE_FILE);
    }

    @Test
    void uploadRejectsImageContentTypeWithWrongExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "food.txt",
                "image/png",
                "image".getBytes()
        );

        assertThatThrownBy(() -> imageUploadService.upload(file))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_IMAGE_FILE);
    }

    @Test
    void uploadRejectsTooLargeImageFile() {
        byte[] bytes = new byte[5 * 1024 * 1024 + 1];
        Arrays.fill(bytes, (byte) 1);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "food.jpg",
                "image/jpeg",
                bytes
        );

        assertThatThrownBy(() -> imageUploadService.upload(file))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_IMAGE_FILE);
    }
}
