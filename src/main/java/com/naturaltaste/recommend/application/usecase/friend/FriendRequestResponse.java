package com.naturaltaste.recommend.application.usecase.friend;

import com.naturaltaste.recommend.domain.friend.FriendRequest;
import com.naturaltaste.recommend.domain.user.User;
import java.time.LocalDateTime;

public record FriendRequestResponse(
        Long id,
        FriendUserResponse requester,
        LocalDateTime createdAt
) {

    public static FriendRequestResponse from(FriendRequest request, User requester) {
        return new FriendRequestResponse(
                request.getId(),
                FriendUserResponse.from(requester),
                request.getCreatedAt()
        );
    }
}
