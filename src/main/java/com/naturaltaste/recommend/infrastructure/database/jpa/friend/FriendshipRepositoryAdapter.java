package com.naturaltaste.recommend.infrastructure.database.jpa.friend;

import com.naturaltaste.recommend.domain.friend.Friendship;
import com.naturaltaste.recommend.domain.friend.FriendshipRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendshipRepositoryAdapter implements FriendshipRepository {

    private final FriendshipJpaRepository friendshipJpaRepository;

    @Override
    public Friendship save(Friendship friendship) {
        return friendshipJpaRepository.save(friendship);
    }

    @Override
    public boolean existsByUserIdAndFriendId(Long userId, Long friendId) {
        return friendshipJpaRepository.existsByUserIdAndFriendId(userId, friendId);
    }

    @Override
    public List<Friendship> findAllByUserId(Long userId) {
        return friendshipJpaRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
