package com.naturaltaste.recommend.application.port;

public interface TokenPort {

    String createAccessToken(Long userId);

    Long parseUserId(String token);
}
