package com.platform.backend.dto;

import com.platform.backend.entity.PayoutStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PayoutDtos {
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class CreatePayoutRequest {
        @NotNull
        @DecimalMin(value = "50000.0", message = "Minimum payout is Rp 50.000")
        private BigDecimal amount;

        @NotBlank(message = "Bank name is required")
        private String bankName;

        @NotBlank(message = "Account number is required")
        private String accountNumber;

        @NotBlank(message = "Account holder name is required")
        private String accountHolderName;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class PayoutResponse {
        private UUID id;
        private BigDecimal amount;
        private String bankName;
        private String accountNumber;
        private String accountHolderName;
        private PayoutStatus status;
        private LocalDateTime requestedAt;
        private LocalDateTime processedAt;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class CreatorBalanceResponse {
        private BigDecimal totalGrossRevenue;
        private BigDecimal pendingPayouts;
        private BigDecimal completedPayouts;
        private BigDecimal availableBalance;
    }
}