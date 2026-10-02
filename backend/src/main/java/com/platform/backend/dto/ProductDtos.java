package com.platform.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductDtos {
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class CreateProductRequest {
        @NotBlank(message = "Title is required")
        private String title;
        private String description;
        private String coverImageUrl;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        private BigDecimal price;

        private Boolean enableWatermark;
        private Boolean isPublished;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class UpdateProductRequest {
        private String title;
        private String description;
        private String coverImageUrl;
        private BigDecimal price;
        private Boolean enableWatermark;
        private Boolean isPublished;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class ProductResponse {
        private UUID id;
        private String title;
        private String description;
        private String coverImageUrl;
        private BigDecimal price;
        private String masterFileUrl;
        private Boolean enableWatermark;
        private Boolean isPublished;
        private LocalDateTime createdAt;
    }
}