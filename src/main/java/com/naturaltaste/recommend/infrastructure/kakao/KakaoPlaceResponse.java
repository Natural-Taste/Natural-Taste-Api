package com.naturaltaste.recommend.infrastructure.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoPlaceResponse(
        String id,
        @JsonProperty("place_name") String placeName,
        @JsonProperty("address_name") String addressName,
        @JsonProperty("road_address_name") String roadAddressName,
        String x,
        String y,
        @JsonProperty("category_name") String categoryName,
        String phone,
        @JsonProperty("place_url") String placeUrl
) {
}
