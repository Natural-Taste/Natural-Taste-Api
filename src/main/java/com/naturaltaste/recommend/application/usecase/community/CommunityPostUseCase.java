package com.naturaltaste.recommend.application.usecase.community;

import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import java.util.List;

public interface CommunityPostUseCase {

    CommunityPostResponse create(Long authorId, CreateCommunityPostRequest request);

    List<CommunityPostResponse> findAll(Long userId);

    CommunityPostResponse findById(Long userId, Long postId);

    RestaurantResponse saveRestaurant(Long userId, Long postId);

    CommunityPostResponse toggleRecommendation(Long userId, Long postId);

    CommunityCommentResponse createComment(Long userId, Long postId, CreateCommunityCommentRequest request);

    List<CommunityCommentResponse> findComments(Long postId);
}
