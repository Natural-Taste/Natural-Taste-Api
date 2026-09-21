package com.naturaltaste.recommend.application.usecase.restaurant;

import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import com.naturaltaste.recommend.domain.restaurant.SavedRestaurant;
import java.math.BigDecimal;

public record RestaurantResponse(
        Long id,
        String provider,
        String providerPlaceId,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String category,
        String phone,
        String placeUrl,
        boolean saved,
        String memo
) {

    public static RestaurantResponse from(Restaurant restaurant) {
        return fromRestaurant(restaurant, false);
    }

    public static RestaurantResponse fromRestaurant(Restaurant restaurant, boolean saved) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getProvider(),
                restaurant.getProviderPlaceId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getCategory(),
                restaurant.getPhone(),
                restaurant.getPlaceUrl(),
                saved,
                null
        );
    }

    public static RestaurantResponse fromSavedRestaurant(Restaurant restaurant, SavedRestaurant savedRestaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getProvider(),
                restaurant.getProviderPlaceId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getCategory(),
                restaurant.getPhone(),
                restaurant.getPlaceUrl(),
                true,
                savedRestaurant.getMemo()
        );
    }

    public static RestaurantResponse fromSearchResult(RestaurantSearchResult searchResult) {
        return new RestaurantResponse(
                null,
                searchResult.provider(),
                searchResult.providerPlaceId(),
                searchResult.name(),
                searchResult.address(),
                searchResult.latitude(),
                searchResult.longitude(),
                searchResult.category(),
                searchResult.phone(),
                searchResult.placeUrl(),
                false,
                null
        );
    }
}
