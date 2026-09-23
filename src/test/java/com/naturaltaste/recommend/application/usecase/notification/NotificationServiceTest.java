package com.naturaltaste.recommend.application.usecase.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.naturaltaste.recommend.domain.notification.Notification;
import com.naturaltaste.recommend.domain.notification.NotificationRepository;
import com.naturaltaste.recommend.domain.notification.NotificationType;
import com.naturaltaste.recommend.domain.user.User;
import com.naturaltaste.recommend.domain.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void createFriendRequestSavesNotification() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user(1L, "요청자")));
        given(userRepository.findById(2L)).willReturn(Optional.of(user(2L, "수신자")));

        notificationService.createFriendRequest(2L, 1L, 10L);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getRecipientId()).isEqualTo(2L);
        assertThat(captor.getValue().getActorId()).isEqualTo(1L);
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.FRIEND_REQUEST);
        assertThat(captor.getValue().getMessage()).isEqualTo("요청자님이 친구 요청을 보냈습니다.");
    }

    @Test
    void createCommunityCommentSkipsSelfNotification() {
        notificationService.createCommunityComment(1L, 1L, 20L);

        org.mockito.Mockito.verifyNoInteractions(notificationRepository);
    }

    @Test
    void findAllReturnsUserNotifications() {
        Notification notification = notification(1L, false);
        given(userRepository.findById(2L)).willReturn(Optional.of(user(2L, "수신자")));
        given(notificationRepository.findAllByRecipientId(2L)).willReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.findAll(2L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).message()).isEqualTo("알림입니다.");
    }

    @Test
    void markAsReadUpdatesNotification() {
        Notification notification = notification(1L, false);
        given(userRepository.findById(2L)).willReturn(Optional.of(user(2L, "수신자")));
        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));
        given(notificationRepository.save(notification)).willReturn(notification);

        NotificationResponse response = notificationService.markAsRead(2L, 1L);

        assertThat(response.read()).isTrue();
        verify(notificationRepository).save(notification);
    }

    private Notification notification(Long id, boolean read) {
        return Notification.builder()
                .id(id)
                .recipientId(2L)
                .actorId(1L)
                .type(NotificationType.COMMUNITY_COMMENT)
                .targetId(20L)
                .message("알림입니다.")
                .read(read)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private User user(Long id, String name) {
        return User.builder()
                .id(id)
                .email("user" + id + "@example.com")
                .password("password")
                .name(name)
                .deleted(false)
                .build();
    }
}
