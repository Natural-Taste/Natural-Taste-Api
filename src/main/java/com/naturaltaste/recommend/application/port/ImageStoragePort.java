package com.naturaltaste.recommend.application.port;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStoragePort {

    String store(MultipartFile file);
}
