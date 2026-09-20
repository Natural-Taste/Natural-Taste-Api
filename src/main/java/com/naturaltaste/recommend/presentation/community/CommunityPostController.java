package com.naturaltaste.recommend.presentation.community;

import com.naturaltaste.recommend.application.usecase.community.CommunityPostResponse;
import com.naturaltaste.recommend.application.usecase.community.CommunityPostUseCase;
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
    public List<CommunityPostResponse> findAll() {
        return communityPostUseCase.findAll();
    }

    @GetMapping("/community/posts/{postId}")
    public CommunityPostResponse findById(@PathVariable Long postId) {
        return communityPostUseCase.findById(postId);
    }

    @PostMapping("/community/posts/{postId}/save")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse saveRestaurant(Authentication authentication, @PathVariable Long postId) {
        return communityPostUseCase.saveRestaurant(currentUserId(authentication), postId);
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
