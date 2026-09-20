package com.naturaltaste.recommend.infrastructure.database.jpa.user;

import com.naturaltaste.recommend.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("""
            select u
            from User u
            where u.deleted = false
              and u.id <> :currentUserId
              and (lower(u.name) like lower(concat('%', :keyword, '%'))
                or lower(u.email) like lower(concat('%', :keyword, '%')))
            order by u.name asc, u.id asc
            """)
    List<User> searchActiveUsers(
            @Param("currentUserId") Long currentUserId,
            @Param("keyword") String keyword
    );
}
