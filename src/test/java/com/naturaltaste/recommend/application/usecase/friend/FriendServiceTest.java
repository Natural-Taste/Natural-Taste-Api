package com.naturaltaste.recommend.application.usecase.friend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantUseCase;
import com.naturaltaste.recommend.domain.friend.FriendRequest;
import com.naturaltaste.recommend.domain.friend.FriendRequestRepository;
import com.naturaltaste.recommend.domain.friend.Friendship;
import com.naturaltaste.recommend.domain.friend.FriendshipRepository;
import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import com.naturaltaste.recommend.domain.user.User;
import com.naturaltaste.recommend.domain.user.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private RestaurantUseCase restaurantUseCase;

    @InjectMocks
    private FriendService friendService;

    @Test
    void searchUsersExcludesCurrentUser() {
        User me = user(1L, "me@example.com", "나");
        User friend = user(2L, "friend@example.com", "친구");
        given(userRepository.findById(1L)).willReturn(Optional.of(me));
        given(userRepository.searchActiveUsers(1L, "친")).willReturn(List.of(friend));

        List<FriendUserResponse> responses = friendService.searchUsers(1L, "친");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo(2L);
    }

    @Test
    void requestFriendCreatesPendingRequest() {
        User requester = user(1L, "me@example.com", "나");
        User receiver = user(2L, "friend@example.com", "친구");
        FriendRequest request = FriendRequest.create(1L, 2L);
        given(userRepository.findById(1L)).willReturn(Optional.of(requester));
        given(userRepository.findById(2L)).willReturn(Optional.of(receiver));
        given(friendshipRepository.existsByUserIdAndFriendId(1L, 2L)).willReturn(false);
        given(friendRequestRepository.existsPendingBetween(1L, 2L)).willReturn(false);
        given(friendRequestRepository.save(org.mockito.ArgumentMatchers.any(FriendRequest.class)))
                .willReturn(request);

        FriendRequestResponse response = friendService.requestFriend(
                1L,
                new CreateFriendRequest(2L)
        );

        assertThat(response.requester().id()).isEqualTo(1L);
        verify(friendRequestRepository).save(org.mockito.ArgumentMatchers.any(FriendRequest.class));
    }

    @Test
    void acceptRequestCreatesTwoFriendships() {
        User requester = user(1L, "me@example.com", "나");
        User receiver = user(2L, "friend@example.com", "친구");
        FriendRequest request = FriendRequest.create(1L, 2L);
        given(userRepository.findById(1L)).willReturn(Optional.of(requester));
        given(userRepository.findById(2L)).willReturn(Optional.of(receiver));
        given(friendRequestRepository.findById(10L)).willReturn(Optional.of(request));
        given(friendshipRepository.existsByUserIdAndFriendId(1L, 2L)).willReturn(false);
        given(friendshipRepository.existsByUserIdAndFriendId(2L, 1L)).willReturn(false);

        FriendUserResponse response = friendService.acceptRequest(2L, 10L);

        assertThat(response.id()).isEqualTo(1L);
        ArgumentCaptor<Friendship> captor = ArgumentCaptor.forClass(Friendship.class);
        verify(friendshipRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(Friendship::getUserId, Friendship::getFriendId)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple(1L, 2L),
                        org.assertj.core.groups.Tuple.tuple(2L, 1L)
                );
    }

    @Test
    void findFriendSavedRestaurantsRequiresFriendship() {
        User me = user(1L, "me@example.com", "나");
        User other = user(2L, "other@example.com", "상대");
        given(userRepository.findById(1L)).willReturn(Optional.of(me));
        given(userRepository.findById(2L)).willReturn(Optional.of(other));
        given(friendshipRepository.existsByUserIdAndFriendId(1L, 2L)).willReturn(false);

        assertThatThrownBy(() -> friendService.findFriendSavedRestaurants(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FRIENDSHIP_REQUIRED);
    }

    @Test
    void findFriendSavedRestaurantsReturnsSavedRestaurants() {
        User me = user(1L, "me@example.com", "나");
        User friend = user(2L, "friend@example.com", "친구");
        Restaurant restaurant = Restaurant.builder()
                .id(20L)
                .provider("KAKAO")
                .providerPlaceId("place-1")
                .name("초밥집")
                .address("서울시 강남구")
                .latitude(new BigDecimal("37.1234567"))
                .longitude(new BigDecimal("127.1234567"))
                .category("음식점 > 일식")
                .build();
        given(userRepository.findById(1L)).willReturn(Optional.of(me));
        given(userRepository.findById(2L)).willReturn(Optional.of(friend));
        given(friendshipRepository.existsByUserIdAndFriendId(1L, 2L)).willReturn(true);
        given(restaurantUseCase.findSavedRestaurants(2L))
                .willReturn(List.of(RestaurantResponse.fromRestaurant(restaurant, true)));

        List<RestaurantResponse> responses = friendService.findFriendSavedRestaurants(1L, 2L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("초밥집");
    }

    private User user(Long id, String email, String name) {
        return User.builder()
                .id(id)
                .email(email)
                .password("password")
                .name(name)
                .deleted(false)
                .build();
    }
}
