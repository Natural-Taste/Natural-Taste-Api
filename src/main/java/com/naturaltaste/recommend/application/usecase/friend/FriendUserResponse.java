package com.naturaltaste.recommend.application.usecase.friend;

import com.naturaltaste.recommend.domain.user.User;

public record FriendUserResponse(
        Long id,
        String email,
        String name,
        FriendRelationshipStatus relationshipStatus
) {

    public static FriendUserResponse from(User user) {
        return from(user, FriendRelationshipStatus.NONE);
    }

    public static FriendUserResponse from(User user, FriendRelationshipStatus relationshipStatus) {
        return new FriendUserResponse(user.getId(), user.getEmail(), user.getName(), relationshipStatus);
    }
}
