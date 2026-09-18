package com.naturaltaste.recommend.application.usecase.restaurant;

import java.util.List;

public interface RestaurantSearchUseCase {

    List<RestaurantResponse> search(String query);
}
