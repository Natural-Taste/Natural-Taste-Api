package com.naturaltaste.recommend.application.usecase.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.naturaltaste.recommend.application.port.RestaurantSearchPort;
import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import com.naturaltaste.recommend.domain.restaurant.RestaurantRepository;
import com.naturaltaste.recommend.domain.restaurant.SavedRestaurant;
import com.naturaltaste.recommend.domain.restaurant.SavedRestaurantRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantSearchPort restaurantSearchPort;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private SavedRestaurantRepository savedRestaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    void searchReturnsRestaurantResponses() {
        given(restaurantSearchPort.search("초밥")).willReturn(List.of(new RestaurantSearchResult(
                "KAKAO",
                "1",
                "초밥집",
                "서울시 강남구",
                new BigDecimal("37.1234567"),
                new BigDecimal("127.1234567"),
                "음식점 > 일식",
                "02-000-0000",
                "https://place.map.kakao.com/1"
        )));

        List<RestaurantResponse> responses = restaurantService.search("초밥");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("초밥집");
    }

    @Test
    void saveCreatesRestaurantAndSavedRestaurant() {
        SaveRestaurantRequest request = request();
        Restaurant restaurant = Restaurant.builder()
                .id(10L)
                .provider(request.provider())
                .providerPlaceId(request.providerPlaceId())
                .name(request.name())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .category(request.category())
                .phone(request.phone())
                .placeUrl(request.placeUrl())
                .build();
        given(restaurantRepository.findByProviderAndProviderPlaceId(
                request.provider(),
                request.providerPlaceId()
        )).willReturn(Optional.empty());
        given(restaurantRepository.save(org.mockito.ArgumentMatchers.any(Restaurant.class))).willReturn(restaurant);
        given(savedRestaurantRepository.findByUserIdAndRestaurantId(1L, restaurant.getId())).willReturn(Optional.empty());
        given(savedRestaurantRepository.save(org.mockito.ArgumentMatchers.any(SavedRestaurant.class)))
                .willReturn(SavedRestaurant.create(1L, restaurant.getId()));

        RestaurantResponse response = restaurantService.save(1L, request);

        assertThat(response.id()).isEqualTo(restaurant.getId());
        assertThat(response.saved()).isTrue();
        assertThat(response.memo()).isNull();
        verify(savedRestaurantRepository).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateSavedRestaurantMemoUpdatesMemo() {
        Restaurant restaurant = Restaurant.builder()
                .id(10L)
                .provider("KAKAO")
                .providerPlaceId("1")
                .name("초밥집")
                .address("서울시 강남구")
                .latitude(new BigDecimal("37.1234567"))
                .longitude(new BigDecimal("127.1234567"))
                .category("음식점 > 일식")
                .phone("02-000-0000")
                .placeUrl("https://place.map.kakao.com/1")
                .build();
        SavedRestaurant savedRestaurant = SavedRestaurant.create(1L, restaurant.getId());
        given(savedRestaurantRepository.findByUserIdAndRestaurantId(1L, restaurant.getId()))
                .willReturn(Optional.of(savedRestaurant));
        given(restaurantRepository.findById(restaurant.getId())).willReturn(Optional.of(restaurant));
        given(savedRestaurantRepository.save(savedRestaurant)).willReturn(savedRestaurant);

        RestaurantResponse response = restaurantService.updateSavedRestaurantMemo(
                1L,
                restaurant.getId(),
                new UpdateSavedRestaurantMemoRequest("다음에는 런치로")
        );

        assertThat(response.id()).isEqualTo(restaurant.getId());
        assertThat(response.saved()).isTrue();
        assertThat(response.memo()).isEqualTo("다음에는 런치로");
    }

    private SaveRestaurantRequest request() {
        return new SaveRestaurantRequest(
                "KAKAO",
                "1",
                "초밥집",
                "서울시 강남구",
                new BigDecimal("37.1234567"),
                new BigDecimal("127.1234567"),
                "음식점 > 일식",
                "02-000-0000",
                "https://place.map.kakao.com/1"
        );
    }
}
