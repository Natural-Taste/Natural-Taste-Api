package com.naturaltaste.recommend.infrastructure.database.jpa.notification;

import com.naturaltaste.recommend.domain.notification.Notification;
import com.naturaltaste.recommend.domain.notification.NotificationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationJpaRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id);
    }

    @Override
    public List<Notification> findAllByRecipientId(Long recipientId) {
        return notificationJpaRepository.findAllByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    @Override
    public long countUnreadByRecipientId(Long recipientId) {
        return notificationJpaRepository.countByRecipientIdAndReadFalse(recipientId);
    }
}
