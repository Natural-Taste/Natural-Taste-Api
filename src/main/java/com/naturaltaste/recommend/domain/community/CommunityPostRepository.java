package com.naturaltaste.recommend.domain.community;

import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository {

    CommunityPost save(CommunityPost communityPost);

    Optional<CommunityPost> findById(Long id);

    List<CommunityPost> findAll();
}
