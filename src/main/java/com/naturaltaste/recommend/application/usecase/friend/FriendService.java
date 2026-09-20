package com.naturaltaste.recommend.application.usecase.friend;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantUseCase;
import com.naturaltaste.recommend.domain.friend.FriendRequest;
import com.naturaltaste.recommend.domain.friend.FriendRequestRepository;
import com.naturaltaste.recommend.domain.friend.FriendRequestStatus;
import com.naturaltaste.recommend.domain.friend.Friendship;
import com.naturaltaste.recommend.domain.friend.FriendshipRepository;
import com.naturaltaste.recommend.domain.user.User;
import com.naturaltaste.recommend.domain.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendService implements FriendUseCase {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final RestaurantUseCase restaurantUseCase;

    @Override
    @Transactional(readOnly = true)
    public List<FriendUserResponse> searchUsers(Long userId, String query) {
        if (query == null || query.isBlank()) {
            throw new BusinessException(ErrorCode.USER_SEARCH_KEYWORD_REQUIRED);
        }

        getActiveUser(userId);
        return userRepository.searchActiveUsers(userId, query.trim()).stream()
                .map(FriendUserResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public FriendRequestResponse requestFriend(Long userId, CreateFriendRequest request) {
        if (userId.equals(request.receiverId())) {
            throw new BusinessException(ErrorCode.INVALID_FRIEND_REQUEST);
        }

        User requester = getActiveUser(userId);
        User receiver = getActiveUser(request.receiverId());
        if (friendshipRepository.existsByUserIdAndFriendId(userId, receiver.getId())
                || friendRequestRepository.existsPendingBetween(userId, receiver.getId())) {
            throw new BusinessException(ErrorCode.INVALID_FRIEND_REQUEST);
        }

        FriendRequest savedRequest = friendRequestRepository.save(
                FriendRequest.create(requester.getId(), receiver.getId())
        );
        return FriendRequestResponse.from(savedRequest, requester);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequestResponse> findReceivedRequests(Long userId) {
        getActiveUser(userId);
        return friendRequestRepository.findReceivedPendingRequests(userId).stream()
                .map(request -> FriendRequestResponse.from(request, getActiveUser(request.getRequesterId())))
                .toList();
    }

    @Override
    @Transactional
    public FriendUserResponse acceptRequest(Long userId, Long requestId) {
        getActiveUser(userId);
        FriendRequest request = getPendingReceivedRequest(userId, requestId);
        request.accept();
        friendRequestRepository.save(request);
        saveFriendshipIfAbsent(request.getRequesterId(), request.getReceiverId());
        saveFriendshipIfAbsent(request.getReceiverId(), request.getRequesterId());

        return FriendUserResponse.from(getActiveUser(request.getRequesterId()));
    }

    @Override
    @Transactional
    public void rejectRequest(Long userId, Long requestId) {
        getActiveUser(userId);
        FriendRequest request = getPendingReceivedRequest(userId, requestId);
        request.reject();
        friendRequestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendUserResponse> findFriends(Long userId) {
        getActiveUser(userId);
        return friendshipRepository.findAllByUserId(userId).stream()
                .map(friendship -> FriendUserResponse.from(getActiveUser(friendship.getFriendId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findFriendSavedRestaurants(Long userId, Long friendId) {
        getActiveUser(userId);
        getActiveUser(friendId);
        if (!friendshipRepository.existsByUserIdAndFriendId(userId, friendId)) {
            throw new BusinessException(ErrorCode.FRIENDSHIP_REQUIRED);
        }

        return restaurantUseCase.findSavedRestaurants(friendId);
    }

    private FriendRequest getPendingReceivedRequest(Long userId, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
        if (!request.getReceiverId().equals(userId)
                || request.getStatus() != FriendRequestStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_FRIEND_REQUEST);
        }
        return request;
    }

    private User getActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (user.isDeleted()) {
            throw new BusinessException(ErrorCode.DELETED_USER);
        }
        return user;
    }

    private void saveFriendshipIfAbsent(Long userId, Long friendId) {
        if (!friendshipRepository.existsByUserIdAndFriendId(userId, friendId)) {
            friendshipRepository.save(Friendship.create(userId, friendId));
        }
    }
}
