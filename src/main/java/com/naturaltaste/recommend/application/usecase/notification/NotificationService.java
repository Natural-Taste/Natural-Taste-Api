package com.naturaltaste.recommend.application.usecase.notification;

import com.naturaltaste.recommend.application.common.BusinessException;
import com.naturaltaste.recommend.application.common.ErrorCode;
import com.naturaltaste.recommend.domain.notification.Notification;
import com.naturaltaste.recommend.domain.notification.NotificationRepository;
import com.naturaltaste.recommend.domain.notification.NotificationType;
import com.naturaltaste.recommend.domain.user.User;
import com.naturaltaste.recommend.domain.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService implements NotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createFriendRequest(Long recipientId, Long actorId, Long requestId) {
        save(recipientId, actorId, NotificationType.FRIEND_REQUEST, requestId, "님이 친구 요청을 보냈습니다.");
    }

    @Override
    @Transactional
    public void createCommunityComment(Long recipientId, Long actorId, Long postId) {
        if (recipientId.equals(actorId)) {
            return;
        }
        save(recipientId, actorId, NotificationType.COMMUNITY_COMMENT, postId, "님이 게시글에 댓글을 남겼습니다.");
    }

    @Override
    @Transactional
    public void createCommunityRecommendation(Long recipientId, Long actorId, Long postId) {
        if (recipientId.equals(actorId)) {
            return;
        }
        save(recipientId, actorId, NotificationType.COMMUNITY_RECOMMENDATION, postId, "님이 게시글을 추천했습니다.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll(Long userId) {
        getActiveUser(userId);
        return notificationRepository.findAllByRecipientId(userId).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationUnreadCountResponse countUnread(Long userId) {
        getActiveUser(userId);
        return new NotificationUnreadCountResponse(notificationRepository.countUnreadByRecipientId(userId));
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        getActiveUser(userId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
        if (!notification.getRecipientId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        notification.markAsRead();
        return NotificationResponse.from(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        getActiveUser(userId);
        notificationRepository.findAllByRecipientId(userId).stream()
                .filter(notification -> !notification.isRead())
                .forEach(notification -> {
                    notification.markAsRead();
                    notificationRepository.save(notification);
                });
    }

    private void save(
            Long recipientId,
            Long actorId,
            NotificationType type,
            Long targetId,
            String messageSuffix
    ) {
        User actor = getActiveUser(actorId);
        getActiveUser(recipientId);
        notificationRepository.save(Notification.create(
                recipientId,
                actorId,
                type,
                targetId,
                actor.getName() + messageSuffix
        ));
    }

    private User getActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (user.isDeleted()) {
            throw new BusinessException(ErrorCode.DELETED_USER);
        }
        return user;
    }
}
