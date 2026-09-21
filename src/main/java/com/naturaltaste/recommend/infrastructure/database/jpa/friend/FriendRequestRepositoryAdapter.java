package com.naturaltaste.recommend.infrastructure.database.jpa.friend;

import com.naturaltaste.recommend.domain.friend.FriendRequest;
import com.naturaltaste.recommend.domain.friend.FriendRequestRepository;
import com.naturaltaste.recommend.domain.friend.FriendRequestStatus;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendRequestRepositoryAdapter implements FriendRequestRepository {

    private final FriendRequestJpaRepository friendRequestJpaRepository;

    @Override
    public FriendRequest save(FriendRequest friendRequest) {
        return friendRequestJpaRepository.save(friendRequest);
    }

    @Override
    public Optional<FriendRequest> findById(Long id) {
        return friendRequestJpaRepository.findById(id);
    }

    @Override
    public boolean existsPendingBetween(Long requesterId, Long receiverId) {
        return friendRequestJpaRepository.existsPendingBetween(
                requesterId,
                receiverId,
                FriendRequestStatus.PENDING
        );
    }

    @Override
    public List<FriendRequest> findReceivedPendingRequests(Long receiverId) {
        return friendRequestJpaRepository.findAllByReceiverIdAndStatusOrderByCreatedAtDesc(
                receiverId,
                FriendRequestStatus.PENDING
        );
    }

    @Override
    public List<FriendRequest> findSentPendingRequests(Long requesterId) {
        return friendRequestJpaRepository.findAllByRequesterIdAndStatusOrderByCreatedAtDesc(
                requesterId,
                FriendRequestStatus.PENDING
        );
    }

    @Override
    public Optional<FriendRequest> findPendingByRequesterIdAndReceiverId(Long requesterId, Long receiverId) {
        return friendRequestJpaRepository.findByRequesterIdAndReceiverIdAndStatus(
                requesterId,
                receiverId,
                FriendRequestStatus.PENDING
        );
    }
}
