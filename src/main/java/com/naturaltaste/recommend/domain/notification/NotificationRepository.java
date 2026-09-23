package com.naturaltaste.recommend.domain.notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    List<Notification> findAllByRecipientId(Long recipientId);

    long countUnreadByRecipientId(Long recipientId);
}
