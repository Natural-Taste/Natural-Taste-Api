package com.naturaltaste.recommend.application.usecase.auth;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String name
) {
}
