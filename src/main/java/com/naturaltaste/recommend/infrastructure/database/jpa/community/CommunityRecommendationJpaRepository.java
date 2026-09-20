package com.naturaltaste.recommend.infrastructure.database.jpa.community;

import com.naturaltaste.recommend.domain.community.CommunityRecommendation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRecommendationJpaRepository extends JpaRepository<CommunityRecommendation, Long> {

    Optional<CommunityRecommendation> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);
}
