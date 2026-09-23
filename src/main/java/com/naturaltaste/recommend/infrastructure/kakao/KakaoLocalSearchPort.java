package com.naturaltaste.recommend.infrastructure.kakao;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.port.RestaurantSearchPort;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantSearchResult;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@Primary
@Slf4j
public class KakaoLocalSearchPort implements RestaurantSearchPort {

    private static final String PROVIDER = "KAKAO";
    private static final String FOOD_CATEGORY_GROUP_CODE = "FD6";
    private static final String AUTHORIZATION_PREFIX = "KakaoAK ";

    private final RestClient restClient;
    private final String restApiKey;

    @Autowired
    public KakaoLocalSearchPort(@Value("${kakao.rest-api-key:}") String restApiKey) {
        this(RestClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .build(), restApiKey);
    }

    KakaoLocalSearchPort(RestClient restClient, String restApiKey) {
        this.restClient = restClient;
        this.restApiKey = restApiKey;
    }

    @Override
    public List<RestaurantSearchResult> search(String query, BigDecimal longitude, BigDecimal latitude) {
        if (hasInvalidRestApiKey()) {
            log.warn("Kakao Local API REST key is missing or invalid. Check KAKAO_REST_API_KEY.");
            throw new BusinessException(ErrorCode.KAKAO_SEARCH_FAILED);
        }

        try {
            KakaoKeywordSearchResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .queryParam("category_group_code", FOOD_CATEGORY_GROUP_CODE)
                            .queryParamIfPresent("x", java.util.Optional.ofNullable(longitude))
                            .queryParamIfPresent("y", java.util.Optional.ofNullable(latitude))
                            .build())
                    .header("Authorization", AUTHORIZATION_PREFIX + restApiKey)
                    .retrieve()
                    .body(KakaoKeywordSearchResponse.class);

            if (response == null || response.documents() == null) {
                return List.of();
            }

            return response.documents().stream()
                    .map(this::toSearchResult)
                    .toList();
        } catch (RestClientResponseException exception) {
            log.warn(
                    "Kakao Local API request failed. status={}, body={}",
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString()
            );
            throw new BusinessException(ErrorCode.KAKAO_SEARCH_FAILED);
        } catch (RestClientException exception) {
            log.warn("Kakao Local API request failed.", exception);
            throw new BusinessException(ErrorCode.KAKAO_SEARCH_FAILED);
        }
    }

    private boolean hasInvalidRestApiKey() {
        return restApiKey == null
                || restApiKey.isBlank()
                || restApiKey.startsWith(AUTHORIZATION_PREFIX.trim())
                || restApiKey.chars().anyMatch(Character::isWhitespace);
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
