package com.naturaltaste.recommend.infrastructure.database.jpa.community;

import com.naturaltaste.recommend.domain.community.CommunityPost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostJpaRepository extends JpaRepository<CommunityPost, Long> {

    List<CommunityPost> findAllByOrderByCreatedAtDesc();
}
