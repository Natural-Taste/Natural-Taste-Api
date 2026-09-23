package com.naturaltaste.recommend.presentation.notification;

import com.naturaltaste.recommend.application.usecase.notification.NotificationResponse;
import com.naturaltaste.recommend.application.usecase.notification.NotificationUnreadCountResponse;
import com.naturaltaste.recommend.application.usecase.notification.NotificationUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    @GetMapping("/notifications")
    public List<NotificationResponse> findAll(Authentication authentication) {
        return notificationUseCase.findAll(currentUserId(authentication));
    }

    @GetMapping("/notifications/unread-count")
    public NotificationUnreadCountResponse countUnread(Authentication authentication) {
        return notificationUseCase.countUnread(currentUserId(authentication));
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public NotificationResponse markAsRead(
            Authentication authentication,
            @PathVariable Long notificationId
    ) {
        return notificationUseCase.markAsRead(currentUserId(authentication), notificationId);
    }

    @PatchMapping("/notifications/read-all")
    public void markAllAsRead(Authentication authentication) {
        notificationUseCase.markAllAsRead(currentUserId(authentication));
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
