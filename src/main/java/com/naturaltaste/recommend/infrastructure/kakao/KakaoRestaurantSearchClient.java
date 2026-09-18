package com.naturaltaste.recommend.infrastructure.kakao;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.port.RestaurantSearchPort;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantSearchResult;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class KakaoRestaurantSearchClient implements RestaurantSearchPort {

    private static final String PROVIDER = "KAKAO";
    private static final String FOOD_CATEGORY_GROUP_CODE = "FD6";

    private final RestClient restClient;
    private final String restApiKey;

    public KakaoRestaurantSearchClient(@Value("${kakao.rest-api-key:}") String restApiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .build();
        this.restApiKey = restApiKey;
    }

    @Override
    public List<RestaurantSearchResult> search(String query) {
        if (restApiKey.isBlank()) {
            throw new BusinessException(ErrorCode.KAKAO_SEARCH_FAILED);
        }

        try {
            KakaoKeywordSearchResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .queryParam("category_group_code", FOOD_CATEGORY_GROUP_CODE)
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

    private RestaurantSearchResult toSearchResult(KakaoPlaceDocument document) {
        return new RestaurantSearchResult(
                PROVIDER,
                document.id(),
                document.place_name(),
                address(document),
                toBigDecimal(document.y()),
                toBigDecimal(document.x()),
                document.category_name(),
                document.phone(),
                document.place_url()
        );
    }

    private String address(KakaoPlaceDocument document) {
        if (document.road_address_name() != null && !document.road_address_name().isBlank()) {
            return document.road_address_name();
        }
        return document.address_name();
    }

    private BigDecimal toBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }

    private record KakaoKeywordSearchResponse(List<KakaoPlaceDocument> documents) {
    }

    private record KakaoPlaceDocument(
            String id,
            String place_name,
            String road_address_name,
            String address_name,
            String x,
            String y,
            String category_name,
            String phone,
            String place_url
    ) {
    }
}
