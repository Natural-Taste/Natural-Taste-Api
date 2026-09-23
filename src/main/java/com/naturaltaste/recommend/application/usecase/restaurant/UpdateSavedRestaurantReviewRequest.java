package com.naturaltaste.recommend.application.usecase.restaurant;

public record UpdateSavedRestaurantReviewRequest(
        Integer rating,
        String tags,
        Boolean revisit
) {
}
