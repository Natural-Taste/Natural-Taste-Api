package com.naturaltaste.recommend.infrastructure.kakao;

import java.util.List;

public record KakaoKeywordSearchResponse(List<KakaoPlaceResponse> documents) {
}
