package com.naturaltaste.recommend.application.usecase.restaurant;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.port.RestaurantSearchPort;
import com.naturaltaste.recommend.domain.restaurant.Restaurant;
import com.naturaltaste.recommend.domain.restaurant.RestaurantRepository;
import com.naturaltaste.recommend.domain.restaurant.SavedRestaurant;
import com.naturaltaste.recommend.domain.restaurant.SavedRestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService implements RestaurantUseCase {

    private final RestaurantSearchPort restaurantSearchPort;
    private final RestaurantRepository restaurantRepository;
    private final SavedRestaurantRepository savedRestaurantRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> search(String query) {
        return restaurantSearchPort.search(query).stream()
                .map(RestaurantResponse::fromSearchResult)
                .toList();
    }

    @Override
    @Transactional
    public RestaurantResponse save(Long userId, SaveRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findByProviderAndProviderPlaceId(
                        request.provider(),
                        request.providerPlaceId()
                )
                .map(existingRestaurant -> updateRestaurant(existingRestaurant, request))
                .orElseGet(() -> createRestaurant(request));
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        SavedRestaurant userSavedRestaurant = savedRestaurantRepository
                .findByUserIdAndRestaurantId(userId, savedRestaurant.getId())
                .orElseGet(() -> savedRestaurantRepository.save(SavedRestaurant.create(userId, savedRestaurant.getId())));

        return RestaurantResponse.fromSavedRestaurant(savedRestaurant, userSavedRestaurant);
    }

    @Override
    @Transactional
    public void cancelSave(Long userId, Long restaurantId) {
        SavedRestaurant savedRestaurant = savedRestaurantRepository
                .findByUserIdAndRestaurantId(userId, restaurantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        savedRestaurantRepository.delete(savedRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findSavedRestaurants(Long userId) {
        return savedRestaurantRepository.findAllByUserId(userId).stream()
                .map(savedRestaurant -> {
                    Restaurant restaurant = restaurantRepository.findById(savedRestaurant.getRestaurantId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
                    return RestaurantResponse.fromSavedRestaurant(restaurant, savedRestaurant);
                })
                .toList();
    }

    @Override
    @Transactional
    public RestaurantResponse updateSavedRestaurantMemo(
            Long userId,
            Long restaurantId,
            UpdateSavedRestaurantMemoRequest request
    ) {
        SavedRestaurant savedRestaurant = savedRestaurantRepository
                .findByUserIdAndRestaurantId(userId, restaurantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        savedRestaurant.updateMemo(request.memo());
        SavedRestaurant updatedSavedRestaurant = savedRestaurantRepository.save(savedRestaurant);

        return RestaurantResponse.fromSavedRestaurant(restaurant, updatedSavedRestaurant);
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
