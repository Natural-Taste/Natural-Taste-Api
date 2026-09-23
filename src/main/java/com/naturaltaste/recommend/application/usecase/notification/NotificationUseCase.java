package com.naturaltaste.recommend.application.usecase.notification;

import java.util.List;

public interface NotificationUseCase {

    void createFriendRequest(Long recipientId, Long actorId, Long requestId);

    void createCommunityComment(Long recipientId, Long actorId, Long postId);

    void createCommunityRecommendation(Long recipientId, Long actorId, Long postId);

    List<NotificationResponse> findAll(Long userId);

    NotificationUnreadCountResponse countUnread(Long userId);

    NotificationResponse markAsRead(Long userId, Long notificationId);

    void markAllAsRead(Long userId);
}
