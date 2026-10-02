package com.platform.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskPublisherService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final String WATERMARK_QUEUE = "order_tasks";

    public void dispatchWatermarkTask(String orderId, String buyerEmail, String buyerPhone, String masterFileUrl) {
        try {
            Map<String, String> payload = Map.of(
                "event", "WATERMARK_PDF",
                "order_id", orderId,
                "buyer_email", buyerEmail,
                "buyer_phone", buyerPhone,
                "master_file_url", masterFileUrl
            );

            // String jsonPayload = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForList().rightPush(WATERMARK_QUEUE, payload);
            log.info("Dispatched watermark task for orderId={}", orderId);
        } catch (Exception e) {
            log.error("Failed to push watermark task to Redis: {}", e.getMessage(), e);
        }
    }
}