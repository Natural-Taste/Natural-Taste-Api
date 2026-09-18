package com.naturaltaste.recommend.domain.restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long id);

    Optional<Restaurant> findByProviderAndProviderPlaceId(String provider, String providerPlaceId);

    List<Restaurant> findAllByIds(List<Long> ids);
}
