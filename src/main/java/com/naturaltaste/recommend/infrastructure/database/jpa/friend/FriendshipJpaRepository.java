package com.naturaltaste.recommend.infrastructure.database.jpa.friend;

import com.naturaltaste.recommend.domain.friend.Friendship;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipJpaRepository extends JpaRepository<Friendship, Long> {

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    List<Friendship> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByUserIdAndFriendId(Long userId, Long friendId);
}
