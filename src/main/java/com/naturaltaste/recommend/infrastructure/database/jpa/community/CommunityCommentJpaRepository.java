package com.naturaltaste.recommend.infrastructure.database.jpa.community;

import com.naturaltaste.recommend.domain.community.CommunityComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCommentJpaRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findAllByPostIdOrderByCreatedAtAsc(Long postId);

    long countByPostId(Long postId);
}
