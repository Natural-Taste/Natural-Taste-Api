package com.naturaltaste.recommend.application.usecase.community;

import com.naturaltaste.recommend.application.usecase.restaurant.SaveRestaurantRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommunityPostRequest(
        @NotBlank String title,
        @NotBlank String content,
        String imageUrl,
        @Valid @NotNull SaveRestaurantRequest restaurant
) {
}
