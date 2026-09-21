package com.naturaltaste.recommend.application.usecase.friend;

import com.naturaltaste.recommend.domain.friend.FriendRequest;
import com.naturaltaste.recommend.domain.user.User;
import java.time.LocalDateTime;

public record FriendRequestResponse(
        Long id,
        FriendUserResponse requester,
        FriendUserResponse receiver,
        LocalDateTime createdAt
) {

    public static FriendRequestResponse from(FriendRequest request, User requester) {
        return from(request, requester, null);
    }

    public static FriendRequestResponse from(FriendRequest request, User requester, User receiver) {
        return new FriendRequestResponse(
                request.getId(),
                FriendUserResponse.from(requester),
                receiver == null ? null : FriendUserResponse.from(receiver),
                request.getCreatedAt()
        );
    }
}
