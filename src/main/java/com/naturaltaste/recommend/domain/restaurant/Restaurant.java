package com.naturaltaste.recommend.domain.restaurant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "restaurants",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_restaurants_provider_place",
                columnNames = {"provider", "provider_place_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_place_id", nullable = false, length = 100)
    private String providerPlaceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, precision = 16, scale = 12)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 16, scale = 12)
    private BigDecimal longitude;

    private String category;

    private String phone;

    @Column(name = "place_url")
    private String placeUrl;

    public static Restaurant create(
            String provider,
            String providerPlaceId,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String category,
            String phone,
            String placeUrl
    ) {
        return Restaurant.builder()
                .provider(provider)
                .providerPlaceId(providerPlaceId)
                .name(name)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .category(category)
                .phone(phone)
                .placeUrl(placeUrl)
                .build();
    }

    public void update(
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String category,
            String phone,
            String placeUrl
    ) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.phone = phone;
        this.placeUrl = placeUrl;
    }
}
