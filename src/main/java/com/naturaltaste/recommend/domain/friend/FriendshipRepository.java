package com.naturaltaste.recommend.domain.friend;

import java.util.List;

public interface FriendshipRepository {

    Friendship save(Friendship friendship);

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    List<Friendship> findAllByUserId(Long userId);
}
