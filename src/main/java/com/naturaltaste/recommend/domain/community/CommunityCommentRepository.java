package com.naturaltaste.recommend.domain.community;

import java.util.List;

public interface CommunityCommentRepository {

    CommunityComment save(CommunityComment comment);

    List<CommunityComment> findAllByPostId(Long postId);

    long countByPostId(Long postId);
}
