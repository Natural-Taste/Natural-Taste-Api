package com.naturaltaste.recommend.domain.community;

import java.util.Optional;

public interface CommunityRecommendationRepository {

    CommunityRecommendation save(CommunityRecommendation recommendation);

    Optional<CommunityRecommendation> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);

    void delete(CommunityRecommendation recommendation);

    void deleteAllByPostId(Long postId);
}
