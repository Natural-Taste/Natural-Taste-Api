package com.naturaltaste.recommend.infrastructure.kakao;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.port.RestaurantSearchPort;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantSearchResult;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Primary
public class KakaoLocalSearchPort implements RestaurantSearchPort {

    private static final String PROVIDER = "KAKAO";

    private final RestClient restClient;
    private final String restApiKey;

    public KakaoLocalSearchPort(@Value("${kakao.rest-api-key:}") String restApiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .build();
        this.restApiKey = restApiKey;
    }

    @Override
    public List<RestaurantSearchResult> search(String query) {
        if (restApiKey.isBlank()) {
            return List.of();
        }

        try {
            KakaoKeywordSearchResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .queryParam("category_group_code", "FD6")
                            .build())
                    .header("Authorization", "KakaoAK " + restApiKey)
                    .retrieve()
                    .body(KakaoKeywordSearchResponse.class);

            if (response == null || response.documents() == null) {
                return List.of();
            }

            return response.documents().stream()
                    .map(this::toSearchResult)
                    .toList();
        } catch (RestClientException exception) {
            throw new BusinessException(ErrorCode.KAKAO_SEARCH_FAILED);
        }
    }

    private RestaurantSearchResult toSearchResult(KakaoPlaceResponse place) {
        return new RestaurantSearchResult(
                PROVIDER,
                place.id(),
                place.placeName(),
                selectAddress(place),
                new BigDecimal(place.y()),
                new BigDecimal(place.x()),
                place.categoryName(),
                place.phone(),
                place.placeUrl()
        );
    }

    private String selectAddress(KakaoPlaceResponse place) {
        if (place.roadAddressName() != null && !place.roadAddressName().isBlank()) {
            return place.roadAddressName();
        }

        return place.addressName();
    }
}
