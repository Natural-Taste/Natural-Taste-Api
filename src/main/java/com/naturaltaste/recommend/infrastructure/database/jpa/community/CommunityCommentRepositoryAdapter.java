package com.naturaltaste.recommend.infrastructure.database.jpa.community;

import com.naturaltaste.recommend.domain.community.CommunityComment;
import com.naturaltaste.recommend.domain.community.CommunityCommentRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommunityCommentRepositoryAdapter implements CommunityCommentRepository {

    private final CommunityCommentJpaRepository communityCommentJpaRepository;

    @Override
    public CommunityComment save(CommunityComment comment) {
        return communityCommentJpaRepository.save(comment);
    }

    @Override
    public Optional<CommunityComment> findById(Long id) {
        return communityCommentJpaRepository.findById(id);
    }

    @Override
    public List<CommunityComment> findAllByPostId(Long postId) {
        return communityCommentJpaRepository.findAllByPostIdOrderByCreatedAtAsc(postId);
    }

    @Override
    public long countByPostId(Long postId) {
        return communityCommentJpaRepository.countByPostId(postId);
    }

    @Override
    public void delete(CommunityComment comment) {
        communityCommentJpaRepository.delete(comment);
    }

    @Override
    public void deleteAllByPostId(Long postId) {
        communityCommentJpaRepository.deleteAllByPostId(postId);
    }
}
