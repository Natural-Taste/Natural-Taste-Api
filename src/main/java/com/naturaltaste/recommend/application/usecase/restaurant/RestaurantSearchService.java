package com.naturaltaste.recommend.application.usecase.restaurant;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.port.RestaurantSearchPort;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantSearchService implements RestaurantSearchUseCase {

    private final RestaurantSearchPort restaurantSearchPort;

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> search(String query, BigDecimal longitude, BigDecimal latitude) {
        if (query == null || query.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_RESTAURANT_SEARCH_KEYWORD);
        }

        return restaurantSearchPort.search(query.trim(), longitude, latitude).stream()
                .map(RestaurantResponse::fromSearchResult)
                .toList();
    }
}
