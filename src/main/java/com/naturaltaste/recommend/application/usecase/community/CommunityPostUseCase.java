package com.naturaltaste.recommend.application.usecase.community;

import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import java.util.List;

public interface CommunityPostUseCase {

    CommunityPostResponse create(Long authorId, CreateCommunityPostRequest request);

    List<CommunityPostResponse> findAll();

    CommunityPostResponse findById(Long postId);

    RestaurantResponse saveRestaurant(Long userId, Long postId);
}
