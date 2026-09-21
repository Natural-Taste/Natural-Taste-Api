package com.naturaltaste.recommend.domain.friend;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository {

    FriendRequest save(FriendRequest friendRequest);

    Optional<FriendRequest> findById(Long id);

    boolean existsPendingBetween(Long requesterId, Long receiverId);

    List<FriendRequest> findReceivedPendingRequests(Long receiverId);

    List<FriendRequest> findSentPendingRequests(Long requesterId);

    Optional<FriendRequest> findPendingByRequesterIdAndReceiverId(Long requesterId, Long receiverId);
}
