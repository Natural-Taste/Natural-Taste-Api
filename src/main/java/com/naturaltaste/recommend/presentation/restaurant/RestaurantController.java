package com.naturaltaste.recommend.presentation.restaurant;

import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantResponse;
import com.naturaltaste.recommend.application.usecase.restaurant.RestaurantUseCase;
import com.naturaltaste.recommend.application.usecase.restaurant.SaveRestaurantRequest;
import com.naturaltaste.recommend.application.usecase.restaurant.UpdateSavedRestaurantMemoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantUseCase restaurantUseCase;

    @GetMapping("/restaurants/search")
    public List<RestaurantResponse> search(
            @RequestParam @NotBlank String query,
            @RequestParam(required = false) BigDecimal x,
            @RequestParam(required = false) BigDecimal y
    ) {
        return restaurantUseCase.search(query, x, y);
    }

    @PostMapping("/restaurants/saved")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse save(
            Authentication authentication,
            @Valid @RequestBody SaveRestaurantRequest request
    ) {
        return restaurantUseCase.save(currentUserId(authentication), request);
    }

    @DeleteMapping("/restaurants/saved/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelSave(Authentication authentication, @PathVariable Long restaurantId) {
        restaurantUseCase.cancelSave(currentUserId(authentication), restaurantId);
    }

    @GetMapping("/restaurants/saved")
    public List<RestaurantResponse> findSavedRestaurants(Authentication authentication) {
        return restaurantUseCase.findSavedRestaurants(currentUserId(authentication));
    }

    @PatchMapping("/restaurants/saved/{restaurantId}/memo")
    public RestaurantResponse updateSavedRestaurantMemo(
            Authentication authentication,
            @PathVariable Long restaurantId,
            @RequestBody UpdateSavedRestaurantMemoRequest request
    ) {
        return restaurantUseCase.updateSavedRestaurantMemo(currentUserId(authentication), restaurantId, request);
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
