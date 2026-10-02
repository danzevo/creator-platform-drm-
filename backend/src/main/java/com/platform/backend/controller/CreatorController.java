package com.platform.backend.controller;

import com.platform.backend.dto.BioBlockDtos.*;
import com.platform.backend.dto.CreatorDtos.*;
import com.platform.backend.dto.OrderDtos.OrderResponse;
import com.platform.backend.dto.PayoutDtos.*;
import com.platform.backend.dto.ProductDtos.*;
import com.platform.backend.entity.User;
import com.platform.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/creator")
@RequiredArgsConstructor
public class CreatorController {
    private final CreatorProfileService profileService;
    private final BioBlockService bioBlockService;
    private final DigitalProductService productService;
    private final OrderService orderService;
    private final PayoutService payoutService;

    // --- Dashboard & Profile ---
    @GetMapping("/dashboard")
    public ResponseEntity<CreatorDashboardResponse> getDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getDashboard(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(profileService.updateProfile(user, req));
    }

    @PutMapping("/theme")
    public ResponseEntity<?> updateTheme(@AuthenticationPrincipal User user, @RequestBody ThemeConfigRequest req) {
        return ResponseEntity.ok(profileService.updateTheme(user, req));
    }

    // --- Bio Blocks ---
    @GetMapping("/blocks")
    public ResponseEntity<List<BioBlockResponse>> getBlocks(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bioBlockService.getBlocksForCreator(user));
    }

    @PostMapping("/blocks")
    public ResponseEntity<BioBlockResponse> createBlock(@AuthenticationPrincipal User user, @Valid @RequestBody CreateBioBlockRequest req) {
        return ResponseEntity.ok(bioBlockService.createBlock(user, req));
    }

    @PutMapping("/blocks/{id}")
    public ResponseEntity<BioBlockResponse> updateBlock(@AuthenticationPrincipal User user, 
                                                        @PathVariable UUID id,
                                                        @RequestBody UpdateBioBlockRequest req) {
        return ResponseEntity.ok(bioBlockService.updateBlock(user, id, req));
    }

    @DeleteMapping("/blocks/{id}")
    public ResponseEntity<?> deleteBlock(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        bioBlockService.deleteBlock(user, id);
        return ResponseEntity.ok(Map.of("message", "Block deleted successfully"));
    }

    @PutMapping("/blocks/reorder")
    public ResponseEntity<?> reorderBlocks(@AuthenticationPrincipal User user, @RequestBody ReorderBlocksRequest req) {
        bioBlockService.reorderBlocks(user, req);
        return ResponseEntity.ok(Map.of("message", "Blocks reordered successfully"));
    }

    // --- Digital Products ---
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(productService.getProductsForCreator(user));
    }

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
                                                @AuthenticationPrincipal User user,
                                                @RequestParam("title") String title,
                                                @RequestParam(value = "description", required = false) String description,
                                                @RequestParam("price") BigDecimal price,
                                                @RequestParam(value = "coverImageUrl", required = false) String coverImageUrl,
                                                @RequestParam(value = "enableWatermark", defaultValue = "true") Boolean enableWatermark,
                                                @RequestParam(value = "isPublished", defaultValue = "true") Boolean isPublished,
                                                @RequestPart("file") MultipartFile file
                                            ) throws IOException {
        CreateProductRequest req = CreateProductRequest.builder()
                                    .title(title)
                                    .description(description)
                                    .price(price)
                                    .coverImageUrl(coverImageUrl)
                                    .enableWatermark(enableWatermark)
                                    .isPublished(isPublished)
                                    .build();

        return ResponseEntity.ok(productService.createProduct(user, req, file));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@AuthenticationPrincipal User user,
                                                        @PathVariable UUID id,
                                                        @RequestBody UpdateProductRequest req) {
        return ResponseEntity.ok(productService.updateProduct(user, id, req));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal User user,
                                            @PathVariable UUID id) {
        productService.deleteProduct(user, id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    // --- Orders ---
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getOrdersForCreator(user));
    }

    // --- Payouts & Balance ---
    @GetMapping("/balance")
    public ResponseEntity<CreatorBalanceResponse> getBalance(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(payoutService.getBalance(user));
    }

    @GetMapping("/payouts")
    public ResponseEntity<List<PayoutResponse>> getPayouts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(payoutService.getPayoutsForCreator(user));
    }

    @PostMapping("/payouts")
    public ResponseEntity<PayoutResponse> requestPayout(@AuthenticationPrincipal User user, @Valid @RequestBody CreatePayoutRequest req) {
        return ResponseEntity.ok(payoutService.requestPayout(user, req));
    }
}