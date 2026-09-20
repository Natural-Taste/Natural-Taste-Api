package com.naturaltaste.recommend.application.usecase.community;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantUseCase;
import com.naturaltaste.recommend.application.usecase.restaurant.SaveRestaurantRequest;
import com.naturaltaste.recommend.domain.community.CommunityPost;
import com.naturaltaste.recommend.domain.community.CommunityPostRepository;
import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import com.naturaltaste.recommend.domain.restaurant.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostService implements CommunityPostUseCase {

    private final CommunityPostRepository communityPostRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantUseCase restaurantUseCase;

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
                request.content()
        ));

        return CommunityPostResponse.from(post, savedRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostResponse> findAll() {
        return communityPostRepository.findAll().stream()
                .map(post -> CommunityPostResponse.from(post, findRestaurant(post.getRestaurantId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityPostResponse findById(Long postId) {
        CommunityPost post = findPost(postId);
        return CommunityPostResponse.from(post, findRestaurant(post.getRestaurantId()));
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

    private CommunityPost findPost(Long postId) {
        return communityPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_POST_NOT_FOUND));
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
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
