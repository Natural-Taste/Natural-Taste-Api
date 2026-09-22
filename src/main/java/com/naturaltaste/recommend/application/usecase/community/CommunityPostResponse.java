package com.naturaltaste.recommend.application.usecase.community;

import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import com.naturaltaste.recommend.domain.community.CommunityPost;
import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import java.time.LocalDateTime;

public record CommunityPostResponse(
        Long id,
        Long authorId,
        String authorName,
        String title,
        String content,
        String imageUrl,
        RestaurantResponse restaurant,
        long commentCount,
        long recommendationCount,
        boolean recommended,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CommunityPostResponse from(
            CommunityPost post,
            String authorName,
            Restaurant restaurant,
            long commentCount,
            long recommendationCount,
            boolean recommended
    ) {
        return new CommunityPostResponse(
                post.getId(),
                post.getAuthorId(),
                authorName,
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                RestaurantResponse.from(restaurant),
                commentCount,
                recommendationCount,
                recommended,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
