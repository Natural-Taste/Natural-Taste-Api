package com.naturaltaste.recommend.presentation.community;

import com.naturaltaste.recommend.application.usecase.community.CommunityCommentResponse;
import com.naturaltaste.recommend.application.usecase.community.CommunityPostResponse;
import com.naturaltaste.recommend.application.usecase.community.CommunityPostUseCase;
import com.naturaltaste.recommend.application.usecase.community.CreateCommunityCommentRequest;
import com.naturaltaste.recommend.application.usecase.community.CreateCommunityPostRequest;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostUseCase communityPostUseCase;

    @PostMapping("/community/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityPostResponse create(
            Authentication authentication,
            @Valid @RequestBody CreateCommunityPostRequest request
    ) {
        return communityPostUseCase.create(currentUserId(authentication), request);
    }

    @GetMapping("/community/posts")
    public List<CommunityPostResponse> findAll(Authentication authentication) {
        return communityPostUseCase.findAll(currentUserId(authentication));
    }

    @GetMapping("/community/posts/{postId}")
    public CommunityPostResponse findById(Authentication authentication, @PathVariable Long postId) {
        return communityPostUseCase.findById(currentUserId(authentication), postId);
    }

    @PostMapping("/community/posts/{postId}/save")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse saveRestaurant(Authentication authentication, @PathVariable Long postId) {
        return communityPostUseCase.saveRestaurant(currentUserId(authentication), postId);
    }

    @PostMapping("/community/posts/{postId}/recommend")
    public CommunityPostResponse toggleRecommendation(Authentication authentication, @PathVariable Long postId) {
        return communityPostUseCase.toggleRecommendation(currentUserId(authentication), postId);
    }

    @PostMapping("/community/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityCommentResponse createComment(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommunityCommentRequest request
    ) {
        return communityPostUseCase.createComment(currentUserId(authentication), postId, request);
    }

    @GetMapping("/community/posts/{postId}/comments")
    public List<CommunityCommentResponse> findComments(@PathVariable Long postId) {
        return communityPostUseCase.findComments(postId);
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
