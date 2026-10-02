package com.platform.backend.service;

import com.platform.backend.dto.OrderDtos.*;
import com.platform.backend.entity.DigitalProduct;
import com.platform.backend.entity.Order;
import com.platform.backend.entity.OrderStatus;
import com.platform.backend.entity.User;
import com.platform.backend.repository.CreatorProfileRepository;
import com.platform.backend.repository.DigitalProductRepository;
import com.platform.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final DigitalProductRepository productRepository;
    private final CreatorProfileRepository profileRepository;
    private final TaskPublisherService taskPublisherService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest req) {
        DigitalProduct product = productRepository.findById(req.getProductId())
                                                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getIsPublished()) {
            throw new IllegalArgumentException("This product is not currently available for purchase");
        }

        String paymentMethod = req.getPaymentMethod() != null ? req.getPaymentMethod().toUpperCase() : "QRIS";
        String paymentRef = "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Order order = Order.builder()
                            .product(product)
                            .creator(product.getCreator())
                            .buyerEmail(req.getBuyerEmail().trim().toLowerCase())
                            .buyerPhone(req.getBuyerPhone().trim())
                            .totalAmount(product.getPrice())
                            .status(OrderStatus.PENDING)
                            .paymentMethod(paymentMethod)
                            .paymentRef(paymentRef)
                            .build();
        
        Order saved = orderRepository.save(order);
        String creatorUsername = profileRepository.findByUserId(product.getCreator().getId())
                                                    .map(p -> p.getUsername()).orElse("Creator");

        // QRIS Mock Barcode payload for simulated Indonesian payment scanning
        String qrPayload = "00020101021226580016ID.CO.CREATOR.WWW0118" + saved.getId().toString() + "5204541153033605802ID5915" + creatorUsername + "6007JAKARTA6304ABCD";
        String vaNumber = "8808" + req.getBuyerPhone().replaceAll("[^0-9]", "");

        return OrderResponse.builder()
                            .orderId(saved.getId())
                            .productId(product.getId())
                            .productTitle(product.getTitle())
                            .creatorUsername(creatorUsername)
                            .buyerEmail(saved.getBuyerEmail())
                            .buyerPhone(saved.getBuyerPhone())
                            .totalAmount(saved.getTotalAmount())
                            .status(saved.getStatus())
                            .paymentRef(saved.getPaymentRef())
                            .paymentMethod(saved.getPaymentMethod())
                            .qrCodeString(qrPayload)
                            .virtualAccount(vaNumber)
                            .watermarkedFileUrl(saved.getWatermarkedFileUrl())
                            .createdAt(saved.getCreatedAt())
                            .build();
    }

    @Transactional(readOnly = true)
    public OrderStatusResponse getOrderStatus(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        return OrderStatusResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .watermarkedFileUrl(order.getWatermarkedFileUrl())
                .isReadyToRead(order.getStatus() == OrderStatus.PAID && order.getWatermarkedFileUrl() != null)
                .build();
    }

    @Transactional
    public void markOrderAsPaid(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                                        .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            log.info("Order {} is already PAID", orderId);
            return;
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // Dispatch background Python PyMuPDF stamping task
        taskPublisherService.dispatchWatermarkTask(
            order.getId().toString(),
            order.getBuyerEmail(),
            order.getBuyerPhone(),
            order.getProduct().getMasterFileUrl()
        );

        log.info("Marked order {} as PAID and dispatched watermark task", orderId);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForCreator(User user) {
        return orderRepository.findByCreatorIdOrderByCreatedAtDesc(user.getId()).stream()
                            .map(o -> OrderResponse.builder()
                                    .orderId(o.getId())
                                    .productId(o.getProduct().getId())
                                    .productTitle(o.getProduct().getTitle())
                                    .buyerEmail(o.getBuyerEmail())
                                    .buyerPhone(o.getBuyerPhone())
                                    .totalAmount(o.getTotalAmount())
                                    .status(o.getStatus())
                                    .paymentRef(o.getPaymentRef())
                                    .paymentMethod(o.getPaymentMethod())
                                    .watermarkedFileUrl(o.getWatermarkedFileUrl())
                                    .createdAt(o.getCreatedAt())
                                    .build()
                            )
                            .collect(Collectors.toList());
    }
}