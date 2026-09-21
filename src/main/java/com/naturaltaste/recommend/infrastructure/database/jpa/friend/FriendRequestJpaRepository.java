package com.naturaltaste.recommend.infrastructure.database.jpa.friend;

import com.naturaltaste.recommend.domain.friend.FriendRequest;
import com.naturaltaste.recommend.domain.friend.FriendRequestStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRequestJpaRepository extends JpaRepository<FriendRequest, Long> {

    @Query("""
            select count(fr) > 0
            from FriendRequest fr
            where fr.status = :status
              and ((fr.requesterId = :requesterId and fr.receiverId = :receiverId)
                or (fr.requesterId = :receiverId and fr.receiverId = :requesterId))
            """)
    boolean existsPendingBetween(
            @Param("requesterId") Long requesterId,
            @Param("receiverId") Long receiverId,
            @Param("status") FriendRequestStatus status
    );

    List<FriendRequest> findAllByReceiverIdAndStatusOrderByCreatedAtDesc(
            Long receiverId,
            FriendRequestStatus status
    );

    List<FriendRequest> findAllByRequesterIdAndStatusOrderByCreatedAtDesc(
            Long requesterId,
            FriendRequestStatus status
    );

    Optional<FriendRequest> findByRequesterIdAndReceiverIdAndStatus(
            Long requesterId,
            Long receiverId,
            FriendRequestStatus status
    );
}
