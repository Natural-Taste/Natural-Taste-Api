package com.naturaltaste.recommend.application.usecase.restaurant;

import java.math.BigDecimal;
import java.util.List;

public interface RestaurantSearchUseCase {

    List<RestaurantResponse> search(String query, BigDecimal longitude, BigDecimal latitude);
}
