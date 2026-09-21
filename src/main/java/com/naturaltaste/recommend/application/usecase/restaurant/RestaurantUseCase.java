package com.naturaltaste.recommend.application.usecase.restaurant;

import java.math.BigDecimal;
import java.util.List;

public interface RestaurantUseCase {

    List<RestaurantResponse> search(String query, BigDecimal longitude, BigDecimal latitude);

    RestaurantResponse save(Long userId, SaveRestaurantRequest request);

    void cancelSave(Long userId, Long restaurantId);

    List<RestaurantResponse> findSavedRestaurants(Long userId);

    RestaurantResponse updateSavedRestaurantMemo(Long userId, Long restaurantId, UpdateSavedRestaurantMemoRequest request);
}
