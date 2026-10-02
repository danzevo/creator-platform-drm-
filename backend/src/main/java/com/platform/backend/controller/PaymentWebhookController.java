package com.platform.backend.controller;

import com.platform.backend.dto.OrderDtos.PaymentWebhookPayload;
import com.platform.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class PaymentWebhookController {
    private final OrderService orderService;

    @PostMapping("/payment")
    public ResponseEntity<?> handlePaymentWebhook(@RequestBody PaymentWebhookPayload payload) {
        log.info("Received payment gateway webhook for order: {}", payload.getOrder_id());
        String status = payload.getTransaction_status();

        if ("settlement".equalsIgnoreCase(status) || "capture".equalsIgnoreCase(status) || "success".equalsIgnoreCase(status)) {
            try {
                UUID orderId = UUID.fromString(payload.getOrder_id());

                orderService.markOrderAsPaid(orderId);
            } catch (Exception e) {
                log.error("Failed to parse or process webhook orderId: {}", payload.getOrder_id(), e);
            }
        }

        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/payment/simulate-success/{orderId}")
    public ResponseEntity<?> simulateSuccessPayment(@PathVariable UUID orderId) {
        log.info("Developer simulated instant success payment for order: {}", orderId);

        orderService.markOrderAsPaid(orderId);

        return ResponseEntity.ok(Map.of("message", "Simulated payment successful, watermark task dispatched", "orderId", orderId));
    }
}