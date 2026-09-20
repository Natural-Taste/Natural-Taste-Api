package com.naturaltaste.recommend.application.usecase.community;

import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import com.naturaltaste.recommend.domain.community.CommunityPost;
import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import java.time.LocalDateTime;

public record CommunityPostResponse(
        Long id,
        Long authorId,
        String title,
        String content,
        RestaurantResponse restaurant,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CommunityPostResponse from(CommunityPost post, Restaurant restaurant) {
        return new CommunityPostResponse(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getContent(),
                RestaurantResponse.from(restaurant),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
