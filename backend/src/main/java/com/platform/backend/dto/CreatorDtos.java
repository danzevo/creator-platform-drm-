package com.platform.backend.dto;

import com.platform.backend.entity.BlockType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CreatorDtos {
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class UpdateProfileRequest {
        @NotBlank
        private String displayName;
        private String bio;
        private String avatarUrl;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class ThemeConfigRequest {
        private String bg;
        private String primary;
        private String font;
        private String cardStyle;
        private String buttonShape;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class PublicBioBlockDto {
        private UUID id;
        private String title;
        private String url;
        private String iconUrl;
        private BlockType blockType;
        private Integer sortOrder;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class PublicProductDto {
        private UUID id;
        private String title;
        private String description;
        private String coverImageUrl;
        private BigDecimal price;
        private Boolean enableWatermark;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class PublicCreatorProfileResponse {
        private UUID id;
        private String username;
        private String displayName;
        private String bio;
        private String avatarUrl;
        private String themeConfigJson;
        private List<PublicBioBlockDto> blocks;
        private List<PublicProductDto> products;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class CreatorDashboardResponse {
        private String username;
        private String displayName;
        private String bio;
        private String avatarUrl;
        private String themeConfigJson;
        private BigDecimal totalRevenue;
        private BigDecimal availableBalance;
        private long totalOrdersCount;
        private long totalProductsCount;
    }
}