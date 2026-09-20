package com.naturaltaste.recommend.application.usecase.community;

import jakarta.validation.constraints.NotBlank;

public record CreateCommunityCommentRequest(
        @NotBlank String content
) {
}
