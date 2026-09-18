package com.naturaltaste.recommend.application.usecase.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SaveRestaurantRequest(
        @NotBlank String provider,
        @NotBlank String providerPlaceId,
        @NotBlank String name,
        @NotBlank String address,
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude,
        String category,
        String phone,
        String placeUrl
) {
}
