package com.naturaltaste.recommend.domain.user;

import java.util.Optional;
import java.util.List;

public interface UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> searchActiveUsers(Long currentUserId, String keyword);
}
