package com.naturaltaste.recommend.infrastructure.database.jpa.community;

import com.naturaltaste.recommend.domain.community.CommunityPost;
import com.naturaltaste.recommend.domain.community.CommunityPostRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommunityPostRepositoryAdapter implements CommunityPostRepository {

    private final CommunityPostJpaRepository communityPostJpaRepository;

    @Override
    public CommunityPost save(CommunityPost communityPost) {
        return communityPostJpaRepository.save(communityPost);
    }

    @Override
    public Optional<CommunityPost> findById(Long id) {
        return communityPostJpaRepository.findById(id);
    }

    @Override
    public List<CommunityPost> findAll() {
        return communityPostJpaRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public void delete(CommunityPost post) {
        communityPostJpaRepository.delete(post);
    }
}
