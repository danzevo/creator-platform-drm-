package com.platform.backend.controller;

import com.platform.backend.entity.Order;
import com.platform.backend.entity.OrderStatus;
import com.platform.backend.repository.OrderRepository;
import com.platform.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentAccessController {
    private final OrderRepository orderRepository;
    private final FileStorageService fileStorageService;

    @GetMapping("/read/{orderId}")
    public ResponseEntity<Resource> readProtectedDocument(@PathVariable UUID orderId) {
        Order order = orderRepository.findById(orderId)
                                        .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.PAID) {
            return ResponseEntity.status(403).build();
        }

        String targetPath = order.getWatermarkedFileUrl() != null ? order.getWatermarkedFileUrl() : order.getProduct().getMasterFileUrl();
        Resource resource = fileStorageService.loadFileAsResource(targetPath);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                                .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "inline;filename=\"protected_document.pdf\"")
                                .header("X-Content-Type-Options", "nosniff")
                                .body(resource);
    }
}