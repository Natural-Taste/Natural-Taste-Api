package com.naturaltaste.recommend.infrastructure.database.jpa.restaurant;

import com.naturaltaste.recommend.domain.restaurant.SavedRestaurant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedRestaurantJpaRepository extends JpaRepository<SavedRestaurant, Long> {

    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

    Optional<SavedRestaurant> findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    List<SavedRestaurant> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
