package com.naturaltaste.recommend.application.usecase.restaurant;

import java.math.BigDecimal;

public record RestaurantSearchResult(
        String provider,
        String providerPlaceId,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String category,
        String phone,
        String placeUrl
) {
}
