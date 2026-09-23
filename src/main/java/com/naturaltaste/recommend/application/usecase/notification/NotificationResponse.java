package com.naturaltaste.recommend.application.usecase.notification;

import com.naturaltaste.recommend.domain.notification.Notification;
import com.naturaltaste.recommend.domain.notification.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        Long actorId,
        Long targetId,
        String message,
        boolean read,
        LocalDateTime createdAt
) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getActorId(),
                notification.getTargetId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
