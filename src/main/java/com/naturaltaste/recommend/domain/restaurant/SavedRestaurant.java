package com.naturaltaste.recommend.domain.restaurant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "saved_restaurants",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_saved_restaurants_user_restaurant",
                columnNames = {"user_id", "restaurant_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SavedRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "memo")
    private String memo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static SavedRestaurant create(Long userId, Long restaurantId) {
        return SavedRestaurant.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }
}
