package com.naturaltaste.recommend.presentation.friend;

import com.naturaltaste.recommend.application.usecase.friend.CreateFriendRequest;
import com.naturaltaste.recommend.application.usecase.friend.FriendRequestResponse;
import com.naturaltaste.recommend.application.usecase.friend.FriendUseCase;
import com.naturaltaste.recommend.application.usecase.friend.FriendUserResponse;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendUseCase friendUseCase;

    @GetMapping("/users/search")
    public List<FriendUserResponse> searchUsers(
            Authentication authentication,
            @RequestParam @NotBlank String query
    ) {
        return friendUseCase.searchUsers(currentUserId(authentication), query);
    }

    @PostMapping("/friends/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendRequestResponse requestFriend(
            Authentication authentication,
            @Valid @RequestBody CreateFriendRequest request
    ) {
        return friendUseCase.requestFriend(currentUserId(authentication), request);
    }

    @GetMapping("/friends/requests/received")
    public List<FriendRequestResponse> findReceivedRequests(Authentication authentication) {
        return friendUseCase.findReceivedRequests(currentUserId(authentication));
    }

    @PostMapping("/friends/requests/{requestId}/accept")
    public FriendUserResponse acceptRequest(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        return friendUseCase.acceptRequest(currentUserId(authentication), requestId);
    }

    @DeleteMapping("/friends/requests/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectRequest(Authentication authentication, @PathVariable Long requestId) {
        friendUseCase.rejectRequest(currentUserId(authentication), requestId);
    }

    @GetMapping("/friends")
    public List<FriendUserResponse> findFriends(Authentication authentication) {
        return friendUseCase.findFriends(currentUserId(authentication));
    }

    @GetMapping("/friends/{friendId}/restaurants/saved")
    public List<RestaurantResponse> findFriendSavedRestaurants(
            Authentication authentication,
            @PathVariable Long friendId
    ) {
        return friendUseCase.findFriendSavedRestaurants(currentUserId(authentication), friendId);
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
