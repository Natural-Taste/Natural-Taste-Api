package com.naturaltaste.recommend.presentation.auth;

import com.naturaltaste.recommend.application.usecase.auth.AuthResponse;
import com.naturaltaste.recommend.application.usecase.auth.AuthUseCase;
import com.naturaltaste.recommend.application.usecase.auth.ChangePasswordRequest;
import com.naturaltaste.recommend.application.usecase.auth.DeleteUserRequest;
import com.naturaltaste.recommend.application.usecase.auth.LoginRequest;
import com.naturaltaste.recommend.application.usecase.auth.SignupRequest;
import com.naturaltaste.recommend.application.usecase.auth.UpdateUserRequest;
import com.naturaltaste.recommend.application.usecase.auth.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        return authUseCase.signup(request);
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authUseCase.login(request);
    }

    @PostMapping("/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(Authentication authentication) {
        authUseCase.logout(currentUserId(authentication));
    }

    @GetMapping("/users/me")
    public UserResponse getUser(Authentication authentication) {
        return authUseCase.getUser(currentUserId(authentication));
    }

    @PatchMapping("/users/me")
    public UserResponse updateUser(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return authUseCase.updateUser(currentUserId(authentication), request);
    }

    @DeleteMapping("/users/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            Authentication authentication,
            @Valid @RequestBody DeleteUserRequest request
    ) {
        authUseCase.deleteUser(currentUserId(authentication), request);
    }

    @PatchMapping("/users/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authUseCase.changePassword(currentUserId(authentication), request);
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
