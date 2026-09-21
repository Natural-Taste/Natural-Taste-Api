package com.naturaltaste.recommend.application.usecase.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.port.RestaurantSearchPort;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RestaurantSearchServiceTest {

    @Test
    void searchRestaurantsByKeyword() {
        RestaurantSearchPort port = (query, longitude, latitude) -> List.of(new RestaurantSearchResult(
                "KAKAO",
                "12345",
                "테스트 맛집",
                "서울시 강남구",
                new BigDecimal("37.499"),
                new BigDecimal("127.027"),
                "음식점 > 한식",
                "02-123-4567",
                "https://place.map.kakao.com/12345"
        ));
        RestaurantSearchService service = new RestaurantSearchService(port);

        List<RestaurantResponse> responses = service.search(" 테스트 ", null, null);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().provider()).isEqualTo("KAKAO");
        assertThat(responses.getFirst().providerPlaceId()).isEqualTo("12345");
        assertThat(responses.getFirst().name()).isEqualTo("테스트 맛집");
        assertThat(responses.getFirst().saved()).isFalse();
    }

    @Test
    void throwExceptionWhenKeywordIsBlank() {
        RestaurantSearchService service = new RestaurantSearchService((query, longitude, latitude) -> List.of());

        assertThatThrownBy(() -> service.search(" ", null, null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_RESTAURANT_SEARCH_KEYWORD);
    }

    @Test
    void searchRestaurantsWithLocation() {
        RestaurantSearchPort port = Mockito.mock(RestaurantSearchPort.class);
        RestaurantSearchService service = new RestaurantSearchService(port);
        BigDecimal longitude = new BigDecimal("127.027");
        BigDecimal latitude = new BigDecimal("37.499");
        given(port.search("초밥", longitude, latitude)).willReturn(List.of());

        service.search(" 초밥 ", longitude, latitude);

        verify(port).search("초밥", longitude, latitude);
    }
}
