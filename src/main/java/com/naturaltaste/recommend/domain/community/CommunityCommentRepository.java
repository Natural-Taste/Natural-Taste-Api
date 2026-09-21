package com.naturaltaste.recommend.domain.community;

import java.util.List;
import java.util.Optional;

public interface CommunityCommentRepository {

    CommunityComment save(CommunityComment comment);

    Optional<CommunityComment> findById(Long id);

    List<CommunityComment> findAllByPostId(Long postId);

    long countByPostId(Long postId);

    void delete(CommunityComment comment);

    void deleteAllByPostId(Long postId);
}
