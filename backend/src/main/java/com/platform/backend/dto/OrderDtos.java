package com.platform.backend.dto;

import com.platform.backend.entity.OrderStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderDtos {
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class CreateOrderRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;
        
        @Email(message = "Invalid email format") @NotBlank(message = "Buyer email is required")
        private String buyerEmail;

        @NotBlank(message = "Buyer phone is required")
        private String buyerPhone;
        
        private String paymentMethod; // QRIS, GOPAY, BCA_VA
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class OrderResponse {
        private UUID orderId;
        private UUID productId;
        private String productTitle;
        private String creatorUsername;
        private String buyerEmail;
        private String buyerPhone;
        private BigDecimal totalAmount;
        private OrderStatus status;
        private String paymentRef;
        private String paymentMethod;
        private String qrCodeString; // QRIS QR string / simulation data
        private String virtualAccount; // Simulated VA number
        private String watermarkedFileUrl;
        private LocalDateTime createdAt;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class OrderStatusResponse {
        private UUID orderId;
        private OrderStatus status;
        private String watermarkedFileUrl;
        private boolean isReadyToRead;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class PaymentWebhookPayload {
        private String order_id;
        private String transaction_status;
        private String status_code;
        private String signature_key;
        private String payment_type;
        private String gross_amount;
    }
}