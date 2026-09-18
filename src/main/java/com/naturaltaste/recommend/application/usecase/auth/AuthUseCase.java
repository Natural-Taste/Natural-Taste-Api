package com.naturaltaste.recommend.application.usecase.auth;

public interface AuthUseCase {

    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    void logout(Long userId);

    void deleteUser(Long userId, DeleteUserRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}
