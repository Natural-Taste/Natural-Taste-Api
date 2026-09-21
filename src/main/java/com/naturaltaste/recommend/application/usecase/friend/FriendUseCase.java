package com.naturaltaste.recommend.application.usecase.friend;

import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import java.util.List;

public interface FriendUseCase {

    List<FriendUserResponse> searchUsers(Long userId, String query);

    FriendRequestResponse requestFriend(Long userId, CreateFriendRequest request);

    List<FriendRequestResponse> findReceivedRequests(Long userId);

    List<FriendRequestResponse> findSentRequests(Long userId);

    FriendUserResponse acceptRequest(Long userId, Long requestId);

    void rejectRequest(Long userId, Long requestId);

    void cancelSentRequest(Long userId, Long requestId);

    List<FriendUserResponse> findFriends(Long userId);

    void deleteFriend(Long userId, Long friendId);

    List<RestaurantResponse> findFriendSavedRestaurants(Long userId, Long friendId);
}
