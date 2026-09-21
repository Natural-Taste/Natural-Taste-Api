package com.naturaltaste.recommend.presentation.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("local")
public class LocalImageResourceConfig implements WebMvcConfigurer {

    private final Path uploadPath;

    public LocalImageResourceConfig(@Value("${app.image.local.upload-dir:uploads/images}") String uploadDir) {
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations(resourceLocation());
    }

    private String resourceLocation() {
        String location = uploadPath.toUri().toString();
        if (location.endsWith("/")) {
            return location;
        }
        return location + "/";
    }
}
