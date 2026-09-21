package com.naturaltaste.recommend.application.usecase.community;

import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import java.util.List;

public interface CommunityPostUseCase {

    CommunityPostResponse create(Long authorId, CreateCommunityPostRequest request);

    List<CommunityPostResponse> findAll(Long userId);

    CommunityPostResponse findById(Long userId, Long postId);

    CommunityPostResponse update(Long userId, Long postId, UpdateCommunityPostRequest request);

    RestaurantResponse saveRestaurant(Long userId, Long postId);

    CommunityPostResponse toggleRecommendation(Long userId, Long postId);

    CommunityCommentResponse createComment(Long userId, Long postId, CreateCommunityCommentRequest request);

    List<CommunityCommentResponse> findComments(Long postId);

    CommunityCommentResponse updateComment(Long userId, Long postId, Long commentId, UpdateCommunityCommentRequest request);

    void delete(Long userId, Long postId);

    void deleteComment(Long userId, Long postId, Long commentId);
}
