package com.naturaltaste.recommend.domain.restaurant;

import java.util.List;
import java.util.Optional;

public interface SavedRestaurantRepository {

    SavedRestaurant save(SavedRestaurant savedRestaurant);

    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

    Optional<SavedRestaurant> findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    List<SavedRestaurant> findAllByUserId(Long userId);

    void delete(SavedRestaurant savedRestaurant);
}
