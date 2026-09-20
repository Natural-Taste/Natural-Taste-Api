package com.naturaltaste.recommend.application.usecase.friend;

import jakarta.validation.constraints.NotNull;

public record CreateFriendRequest(@NotNull Long receiverId) {
}
