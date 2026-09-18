package com.naturaltaste.recommend.infrastructure.database.jpa.restaurant;

import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantJpaRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByProviderAndProviderPlaceId(String provider, String providerPlaceId);
}
