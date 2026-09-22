package com.naturaltaste.recommend.application.usecase.community;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantUseCase;
import com.naturaltaste.recommend.application.usecase.restaurant.SaveRestaurantRequest;
import com.naturaltaste.recommend.domain.community.CommunityComment;
import com.naturaltaste.recommend.domain.community.CommunityCommentRepository;
import com.naturaltaste.recommend.domain.community.CommunityPost;
import com.naturaltaste.recommend.domain.community.CommunityPostRepository;
import com.naturaltaste.recommend.domain.community.CommunityRecommendation;
import com.naturaltaste.recommend.domain.community.CommunityRecommendationRepository;
import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import com.naturaltaste.recommend.domain.restaurant.RestaurantRepository;
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
class CommunityPostServiceTest {

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private CommunityCommentRepository communityCommentRepository;

    @Mock
    private CommunityRecommendationRepository communityRecommendationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantUseCase restaurantUseCase;

    @InjectMocks
    private CommunityPostService communityPostService;

    @Test
    void createSavesRestaurantAndCommunityPost() {
        CreateCommunityPostRequest request = request();
        Restaurant restaurant = restaurant();
        CommunityPost post = CommunityPost.builder()
                .id(20L)
                .authorId(1L)
                .restaurantId(restaurant.getId())
                .title(request.title())
                .content(request.content())
                .imageUrl(request.imageUrl())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        given(restaurantRepository.findByProviderAndProviderPlaceId("KAKAO", "1")).willReturn(Optional.empty());
        given(restaurantRepository.save(org.mockito.ArgumentMatchers.any(Restaurant.class))).willReturn(restaurant);
        given(communityPostRepository.save(org.mockito.ArgumentMatchers.any(CommunityPost.class))).willReturn(post);
        givenAuthorName(1L, "작성자");

        CommunityPostResponse response = communityPostService.create(1L, request);

        assertThat(response.id()).isEqualTo(20L);
        assertThat(response.authorName()).isEqualTo("작성자");
        assertThat(response.imageUrl()).isEqualTo("https://example.com/sushi.jpg");
        assertThat(response.restaurant().id()).isEqualTo(10L);
        ArgumentCaptor<CommunityPost> captor = ArgumentCaptor.forClass(CommunityPost.class);
        verify(communityPostRepository).save(captor.capture());
        assertThat(captor.getValue().getRestaurantId()).isEqualTo(10L);
        assertThat(captor.getValue().getImageUrl()).isEqualTo("https://example.com/sushi.jpg");
    }

    @Test
    void findAllReturnsPostAuthorName() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        given(communityPostRepository.findAll()).willReturn(List.of(post));
        given(restaurantRepository.findById(10L)).willReturn(Optional.of(restaurant));
        givenAuthorName(1L, "작성자");

        List<CommunityPostResponse> responses = communityPostService.findAll(2L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).authorName()).isEqualTo("작성자");
    }

    @Test
    void findByIdReturnsPostAuthorName() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(restaurantRepository.findById(10L)).willReturn(Optional.of(restaurant));
        givenAuthorName(1L, "작성자");

        CommunityPostResponse response = communityPostService.findById(2L, 20L);

        assertThat(response.authorName()).isEqualTo("작성자");
    }

    @Test
    void saveRestaurantUsesExistingRestaurantSaveFlow() {
        Restaurant restaurant = restaurant();
        CommunityPost post = CommunityPost.builder()
                .id(20L)
                .authorId(1L)
                .restaurantId(restaurant.getId())
                .title("추천")
                .content("맛있습니다")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(restaurantRepository.findById(10L)).willReturn(Optional.of(restaurant));
        given(restaurantUseCase.save(org.mockito.ArgumentMatchers.eq(2L), org.mockito.ArgumentMatchers.any()))
                .willReturn(RestaurantResponse.fromRestaurant(restaurant, true));

        RestaurantResponse response = communityPostService.saveRestaurant(2L, 20L);

        assertThat(response.saved()).isTrue();
        verify(restaurantUseCase).save(org.mockito.ArgumentMatchers.eq(2L), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void toggleRecommendationCreatesRecommendation() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(restaurantRepository.findById(10L)).willReturn(Optional.of(restaurant));
        given(communityRecommendationRepository.findByPostIdAndUserId(20L, 2L)).willReturn(Optional.empty());
        given(communityRecommendationRepository.countByPostId(20L)).willReturn(1L);
        given(communityRecommendationRepository.existsByPostIdAndUserId(20L, 2L)).willReturn(true);
        givenAuthorName(1L, "작성자");

        CommunityPostResponse response = communityPostService.toggleRecommendation(2L, 20L);

        assertThat(response.recommendationCount()).isEqualTo(1L);
        assertThat(response.recommended()).isTrue();
        verify(communityRecommendationRepository).save(org.mockito.ArgumentMatchers.any(CommunityRecommendation.class));
    }

    @Test
    void updatePostChangesPostByAuthor() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(restaurantRepository.findById(10L)).willReturn(Optional.of(restaurant));
        givenAuthorName(1L, "작성자");

        CommunityPostResponse response = communityPostService.update(
                1L,
                20L,
                new UpdateCommunityPostRequest("수정 제목", "수정 내용", "https://example.com/updated.jpg")
        );

        assertThat(response.title()).isEqualTo("수정 제목");
        assertThat(response.authorName()).isEqualTo("작성자");
        assertThat(response.content()).isEqualTo("수정 내용");
        assertThat(response.imageUrl()).isEqualTo("https://example.com/updated.jpg");
        assertThat(post.getTitle()).isEqualTo("수정 제목");
    }

    @Test
    void updatePostRejectsNonAuthor() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));

        assertThatThrownBy(() -> communityPostService.update(
                2L,
                20L,
                new UpdateCommunityPostRequest("수정 제목", "수정 내용", null)
        ))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void createCommentSavesComment() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        CommunityComment comment = CommunityComment.builder()
                .id(30L)
                .postId(20L)
                .authorId(2L)
                .content("좋은 후기입니다")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(communityCommentRepository.save(org.mockito.ArgumentMatchers.any(CommunityComment.class)))
                .willReturn(comment);
        givenAuthorName(2L, "댓글 작성자");

        CommunityCommentResponse response = communityPostService.createComment(
                2L,
                20L,
                new CreateCommunityCommentRequest("좋은 후기입니다")
        );

        assertThat(response.content()).isEqualTo("좋은 후기입니다");
        assertThat(response.authorName()).isEqualTo("댓글 작성자");
        verify(communityCommentRepository).save(org.mockito.ArgumentMatchers.any(CommunityComment.class));
    }

    @Test
    void findCommentsReturnsCommentAuthorName() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        CommunityComment comment = comment();
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(communityCommentRepository.findAllByPostId(20L)).willReturn(List.of(comment));
        givenAuthorName(2L, "댓글 작성자");

        List<CommunityCommentResponse> responses = communityPostService.findComments(20L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).authorName()).isEqualTo("댓글 작성자");
    }

    @Test
    void updateCommentChangesCommentByAuthor() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        CommunityComment comment = comment();
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(communityCommentRepository.findById(30L)).willReturn(Optional.of(comment));
        givenAuthorName(2L, "댓글 작성자");

        CommunityCommentResponse response = communityPostService.updateComment(
                2L,
                20L,
                30L,
                new UpdateCommunityCommentRequest("수정한 댓글입니다")
        );

        assertThat(response.content()).isEqualTo("수정한 댓글입니다");
        assertThat(response.authorName()).isEqualTo("댓글 작성자");
        assertThat(comment.getContent()).isEqualTo("수정한 댓글입니다");
    }

    @Test
    void updateCommentRejectsNonAuthor() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        CommunityComment comment = comment();
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(communityCommentRepository.findById(30L)).willReturn(Optional.of(comment));

        assertThatThrownBy(() -> communityPostService.updateComment(
                1L,
                20L,
                30L,
                new UpdateCommunityCommentRequest("수정한 댓글입니다")
        ))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateCommentFailsWhenCommentBelongsToOtherPost() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        CommunityComment comment = CommunityComment.builder()
                .id(30L)
                .postId(21L)
                .authorId(2L)
                .content("다른 게시글 댓글입니다")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(communityCommentRepository.findById(30L)).willReturn(Optional.of(comment));

        assertThatThrownBy(() -> communityPostService.updateComment(
                2L,
                20L,
                30L,
                new UpdateCommunityCommentRequest("수정한 댓글입니다")
        ))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deleteRemovesPostByAuthor() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));

        communityPostService.delete(1L, 20L);

        verify(communityCommentRepository).deleteAllByPostId(20L);
        verify(communityRecommendationRepository).deleteAllByPostId(20L);
        verify(communityPostRepository).delete(post);
    }

    @Test
    void deleteRejectsNonAuthor() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));

        assertThatThrownBy(() -> communityPostService.delete(2L, 20L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deleteCommentRemovesCommentByAuthor() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        CommunityComment comment = comment();
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(communityCommentRepository.findById(30L)).willReturn(Optional.of(comment));

        communityPostService.deleteComment(2L, 20L, 30L);

        verify(communityCommentRepository).delete(comment);
    }

    @Test
    void deleteCommentRejectsNonAuthor() {
        Restaurant restaurant = restaurant();
        CommunityPost post = post(restaurant);
        CommunityComment comment = comment();
        given(communityPostRepository.findById(20L)).willReturn(Optional.of(post));
        given(communityCommentRepository.findById(30L)).willReturn(Optional.of(comment));

        assertThatThrownBy(() -> communityPostService.deleteComment(1L, 20L, 30L))
                .isInstanceOf(BusinessException.class);
    }

    private CreateCommunityPostRequest request() {
        return new CreateCommunityPostRequest(
                "추천",
                "맛있습니다",
                "https://example.com/sushi.jpg",
                restaurantRequest()
        );
    }

    private CommunityPost post(Restaurant restaurant) {
        return CommunityPost.builder()
                .id(20L)
                .authorId(1L)
                .restaurantId(restaurant.getId())
                .title("추천")
                .content("맛있습니다")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
    }

    private CommunityComment comment() {
        return CommunityComment.builder()
                .id(30L)
                .postId(20L)
                .authorId(2L)
                .content("좋은 후기입니다")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    private void givenAuthorName(Long userId, String name) {
        given(userRepository.findById(userId)).willReturn(Optional.of(User.builder()
                .id(userId)
                .email("user" + userId + "@example.com")
                .password("password")
                .name(name)
                .build()));
    }

    private SaveRestaurantRequest restaurantRequest() {
        return new SaveRestaurantRequest(
                "KAKAO",
                "1",
                "초밥집",
                "서울시 강남구",
                new BigDecimal("37.1234567"),
                new BigDecimal("127.1234567"),
                "음식점 > 일식",
                "02-000-0000",
                "https://place.map.kakao.com/1"
        );
    }

    private Restaurant restaurant() {
        SaveRestaurantRequest request = restaurantRequest();
        return Restaurant.builder()
                .id(10L)
                .provider(request.provider())
                .providerPlaceId(request.providerPlaceId())
                .name(request.name())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .category(request.category())
                .phone(request.phone())
                .placeUrl(request.placeUrl())
                .build();
    }
}
