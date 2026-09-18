package com.naturaltaste.recommend.application.usecase.auth;

public record AuthResponse(Long userId, String accessToken, String tokenType) {

    public static AuthResponse bearer(Long userId, String accessToken) {
        return new AuthResponse(userId, accessToken, "Bearer");
    }
}
