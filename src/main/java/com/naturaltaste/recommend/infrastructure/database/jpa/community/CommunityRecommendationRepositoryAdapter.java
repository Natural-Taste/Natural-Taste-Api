package com.naturaltaste.recommend.infrastructure.database.jpa.community;

import com.naturaltaste.recommend.domain.community.CommunityRecommendation;
import com.naturaltaste.recommend.domain.community.CommunityRecommendationRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommunityRecommendationRepositoryAdapter implements CommunityRecommendationRepository {

    private final CommunityRecommendationJpaRepository communityRecommendationJpaRepository;

    @Override
    public CommunityRecommendation save(CommunityRecommendation recommendation) {
        return communityRecommendationJpaRepository.save(recommendation);
    }

    @Override
    public Optional<CommunityRecommendation> findByPostIdAndUserId(Long postId, Long userId) {
        return communityRecommendationJpaRepository.findByPostIdAndUserId(postId, userId);
    }

    @Override
    public boolean existsByPostIdAndUserId(Long postId, Long userId) {
        return communityRecommendationJpaRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Override
    public long countByPostId(Long postId) {
        return communityRecommendationJpaRepository.countByPostId(postId);
    }

    @Override
    public void delete(CommunityRecommendation recommendation) {
        communityRecommendationJpaRepository.delete(recommendation);
    }
}
