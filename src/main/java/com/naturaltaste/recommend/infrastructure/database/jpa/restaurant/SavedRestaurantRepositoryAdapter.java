package com.naturaltaste.recommend.infrastructure.database.jpa.restaurant;

import com.naturaltaste.recommend.domain.restaurant.SavedRestaurant;
import com.naturaltaste.recommend.domain.restaurant.SavedRestaurantRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SavedRestaurantRepositoryAdapter implements SavedRestaurantRepository {

    private final SavedRestaurantJpaRepository savedRestaurantJpaRepository;

    @Override
    public SavedRestaurant save(SavedRestaurant savedRestaurant) {
        return savedRestaurantJpaRepository.save(savedRestaurant);
    }

    @Override
    public boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId) {
        return savedRestaurantJpaRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    public Optional<SavedRestaurant> findByUserIdAndRestaurantId(Long userId, Long restaurantId) {
        return savedRestaurantJpaRepository.findByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    public List<SavedRestaurant> findAllByUserId(Long userId) {
        return savedRestaurantJpaRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void delete(SavedRestaurant savedRestaurant) {
        savedRestaurantJpaRepository.delete(savedRestaurant);
    }
}
