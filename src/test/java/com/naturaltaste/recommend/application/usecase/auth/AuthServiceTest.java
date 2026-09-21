package com.naturaltaste.recommend.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.application.port.TokenPort;
import com.naturaltaste.recommend.domain.user.User;
import com.naturaltaste.recommend.domain.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenPort tokenPort;

    @InjectMocks
    private AuthService authService;

    @Test
    void signupFailsWhenEmailAlreadyExists() {
        SignupRequest request = new SignupRequest("user@example.com", "password", "사용자");
        given(userRepository.existsByEmail(request.email())).willReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATED_EMAIL);
    }

    @Test
    void loginFailsWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("user@example.com", "wrong-password");
        User user = User.builder()
                .id(1L)
                .email(request.email())
                .password("encoded-password")
                .name("사용자")
                .deleted(false)
                .build();
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_LOGIN);
    }

    @Test
    void changePasswordEncodesNewPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest("old-password", "new-password");
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("old-encoded-password")
                .name("사용자")
                .deleted(false)
                .build();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.currentPassword(), user.getPassword())).willReturn(true);
        given(passwordEncoder.encode(request.newPassword())).willReturn("new-encoded-password");

        authService.changePassword(user.getId(), request);

        verify(passwordEncoder).encode(request.newPassword());
    }

    @Test
    void updateUserChangesName() {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded-password")
                .name("사용자")
                .deleted(false)
                .build();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UserResponse response = authService.updateUser(user.getId(), new UpdateUserRequest("새 이름"));

        assertThat(response.name()).isEqualTo("새 이름");
        assertThat(user.getName()).isEqualTo("새 이름");
    }
}
