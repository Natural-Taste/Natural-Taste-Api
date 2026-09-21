package com.naturaltaste.recommend.application.port;

import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantSearchResult;
import java.math.BigDecimal;
import java.util.List;

public interface RestaurantSearchPort {

    List<RestaurantSearchResult> search(String query, BigDecimal longitude, BigDecimal latitude);
}
