package com.naturaltaste.recommend.infrastructure.database.jpa.restaurant;

import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import com.naturaltaste.recommend.domain.restaurant.RestaurantRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryAdapter implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;

    @Override
    public Restaurant save(Restaurant restaurant) {
        return restaurantJpaRepository.save(restaurant);
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        return restaurantJpaRepository.findById(id);
    }

    @Override
    public Optional<Restaurant> findByProviderAndProviderPlaceId(String provider, String providerPlaceId) {
        return restaurantJpaRepository.findByProviderAndProviderPlaceId(provider, providerPlaceId);
    }

    @Override
    public List<Restaurant> findAllByIds(List<Long> ids) {
        return restaurantJpaRepository.findAllById(ids);
    }
}
