package com.naturaltaste.recommend.application.usecase.community;

import com.naturaltaste.recommend.domain.community.CommunityComment;
import java.time.LocalDateTime;

public record CommunityCommentResponse(
        Long id,
        Long postId,
        Long authorId,
        String authorName,
        String content,
        LocalDateTime createdAt
) {

    public static CommunityCommentResponse from(CommunityComment comment, String authorName) {
        return new CommunityCommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getAuthorId(),
                authorName,
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
