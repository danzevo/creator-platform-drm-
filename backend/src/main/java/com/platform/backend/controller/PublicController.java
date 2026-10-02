package com.platform.backend.controller;

import com.platform.backend.dto.CreatorDtos.PublicCreatorProfileResponse;
import com.platform.backend.dto.OrderDtos.CreateOrderRequest;
import com.platform.backend.dto.OrderDtos.OrderResponse;
import com.platform.backend.dto.OrderDtos.OrderStatusResponse;
import com.platform.backend.dto.ProductDtos.ProductResponse;
import com.platform.backend.service.CreatorProfileService;
import com.platform.backend.service.DigitalProductService;
import com.platform.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    private final CreatorProfileService profileService;
    private final DigitalProductService productService;
    private final OrderService orderService;

    @GetMapping("/creators/{username}")
    public ResponseEntity<PublicCreatorProfileResponse> getPublicProfile(@PathVariable String username) {
        return ResponseEntity.ok(profileService.getPublicProfile(username));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.ok(orderService.createOrder(req));
    }

    @GetMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderStatus(orderId));
    }
}