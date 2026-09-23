package com.naturaltaste.recommend.application.usecase.community;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.usecase.notification.NotificationUseCase;
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
import com.naturaltaste.recommend.domain.user.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostService implements CommunityPostUseCase {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityRecommendationRepository communityRecommendationRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantUseCase restaurantUseCase;
    private final NotificationUseCase notificationUseCase;

    @Override
    @Transactional
    public CommunityPostResponse create(Long authorId, CreateCommunityPostRequest request) {
        Restaurant restaurant = restaurantRepository.findByProviderAndProviderPlaceId(
                        request.restaurant().provider(),
                        request.restaurant().providerPlaceId()
                )
                .map(existingRestaurant -> updateRestaurant(existingRestaurant, request.restaurant()))
                .orElseGet(() -> createRestaurant(request.restaurant()));
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        CommunityPost post = communityPostRepository.save(CommunityPost.create(
                authorId,
                savedRestaurant.getId(),
                request.title(),
                request.content(),
                request.imageUrl()
        ));

        return toResponse(post, savedRestaurant, authorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostResponse> findAll(Long userId) {
        return communityPostRepository.findAll().stream()
                .map(post -> toResponse(post, findRestaurant(post.getRestaurantId()), userId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityPostResponse findById(Long userId, Long postId) {
        CommunityPost post = findPost(postId);
        return toResponse(post, findRestaurant(post.getRestaurantId()), userId);
    }

    @Override
    @Transactional
    public CommunityPostResponse update(Long userId, Long postId, UpdateCommunityPostRequest request) {
        CommunityPost post = findPost(postId);
        validateAuthor(userId, post.getAuthorId());
        post.update(request.title(), request.content(), request.imageUrl());

        return toResponse(post, findRestaurant(post.getRestaurantId()), userId);
    }

    @Override
    @Transactional
    public RestaurantResponse saveRestaurant(Long userId, Long postId) {
        CommunityPost post = findPost(postId);
        Restaurant restaurant = findRestaurant(post.getRestaurantId());

        return restaurantUseCase.save(userId, new SaveRestaurantRequest(
                restaurant.getProvider(),
                restaurant.getProviderPlaceId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getCategory(),
                restaurant.getPhone(),
                restaurant.getPlaceUrl()
        ));
    }

    @Override
    @Transactional
    public CommunityPostResponse toggleRecommendation(Long userId, Long postId) {
        CommunityPost post = findPost(postId);
        Optional<CommunityRecommendation> recommendation =
                communityRecommendationRepository.findByPostIdAndUserId(postId, userId);
        if (recommendation.isPresent()) {
            communityRecommendationRepository.delete(recommendation.get());
        } else {
            communityRecommendationRepository.save(CommunityRecommendation.create(postId, userId));
            notificationUseCase.createCommunityRecommendation(post.getAuthorId(), userId, postId);
        }

        return toResponse(post, findRestaurant(post.getRestaurantId()), userId);
    }

    @Override
    @Transactional
    public CommunityCommentResponse createComment(
            Long userId,
            Long postId,
            CreateCommunityCommentRequest request
    ) {
        CommunityPost post = findPost(postId);
        CommunityComment comment = communityCommentRepository.save(CommunityComment.create(
                postId,
                userId,
                request.content()
        ));
        notificationUseCase.createCommunityComment(post.getAuthorId(), userId, postId);

        return CommunityCommentResponse.from(comment, findAuthorName(comment.getAuthorId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityCommentResponse> findComments(Long postId) {
        findPost(postId);
        return communityCommentRepository.findAllByPostId(postId).stream()
                .map(comment -> CommunityCommentResponse.from(comment, findAuthorName(comment.getAuthorId())))
                .toList();
    }

    @Override
    @Transactional
    public CommunityCommentResponse updateComment(
            Long userId,
            Long postId,
            Long commentId,
            UpdateCommunityCommentRequest request
    ) {
        findPost(postId);
        CommunityComment comment = findCommentInPost(postId, commentId);
        validateAuthor(userId, comment.getAuthorId());
        comment.update(request.content());

        return CommunityCommentResponse.from(comment, findAuthorName(comment.getAuthorId()));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long postId) {
        CommunityPost post = findPost(postId);
        validateAuthor(userId, post.getAuthorId());
        communityCommentRepository.deleteAllByPostId(postId);
        communityRecommendationRepository.deleteAllByPostId(postId);
        communityPostRepository.delete(post);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long postId, Long commentId) {
        findPost(postId);
        CommunityComment comment = findCommentInPost(postId, commentId);
        validateAuthor(userId, comment.getAuthorId());
        communityCommentRepository.delete(comment);
    }

    private CommunityPost findPost(Long postId) {
        return communityPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_POST_NOT_FOUND));
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
    }

    private CommunityComment findCommentInPost(Long postId, Long commentId) {
        return communityCommentRepository.findById(commentId)
                .filter(foundComment -> foundComment.getPostId().equals(postId))
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_COMMENT_NOT_FOUND));
    }

    private CommunityPostResponse toResponse(CommunityPost post, Restaurant restaurant, Long userId) {
        return CommunityPostResponse.from(
                post,
                findAuthorName(post.getAuthorId()),
                restaurant,
                communityCommentRepository.countByPostId(post.getId()),
                communityRecommendationRepository.countByPostId(post.getId()),
                communityRecommendationRepository.existsByPostIdAndUserId(post.getId(), userId)
        );
    }

    private String findAuthorName(Long authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getName();
    }

    private void validateAuthor(Long userId, Long authorId) {
        if (!authorId.equals(userId)) {
            throw new BusinessException(ErrorCode.COMMUNITY_AUTHOR_REQUIRED);
        }
    }

    private Restaurant createRestaurant(SaveRestaurantRequest request) {
        return Restaurant.create(
                request.provider(),
                request.providerPlaceId(),
                request.name(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.category(),
                request.phone(),
                request.placeUrl()
        );
    }

    private Restaurant updateRestaurant(Restaurant restaurant, SaveRestaurantRequest request) {
        restaurant.update(
                request.name(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.category(),
                request.phone(),
                request.placeUrl()
        );
        return restaurant;
    }
}
