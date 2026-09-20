package com.naturaltaste.recommend.application.usecase.friend;

import com.naturaltaste.recommend.domain.user.User;

public record FriendUserResponse(Long id, String email, String name) {

    public static FriendUserResponse from(User user) {
        return new FriendUserResponse(user.getId(), user.getEmail(), user.getName());
    }
}
