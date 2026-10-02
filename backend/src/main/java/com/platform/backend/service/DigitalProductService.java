package com.platform.backend.service;

import com.platform.backend.dto.ProductDtos.*;
import com.platform.backend.entity.DigitalProduct;
import com.platform.backend.entity.User;
import com.platform.backend.repository.DigitalProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DigitalProductService {
    private final DigitalProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsForCreator(User user) {
        return productRepository.findByCreatorIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {
        DigitalProduct product = productRepository.findById(productId)
                                                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return toResponse(product);                                                    
    }

    @Transactional
    public ProductResponse createProduct(User user, CreateProductRequest req, MultipartFile masterFile) throws IOException {
        UUID tempId = UUID.randomUUID();
        String savedFilePath = fileStorageService.storeMasterFile(masterFile, tempId);
        
        DigitalProduct product = DigitalProduct.builder().creator(user)
                                                .title(req.getTitle())
                                                .description(req.getDescription())
                                                .coverImageUrl(req.getCoverImageUrl())
                                                .price(req.getPrice())
                                                .masterFileUrl(savedFilePath)
                                                .enableWatermark(req.getEnableWatermark() != null ? req.getEnableWatermark() : true)
                                                .isPublished(req.getIsPublished() != null ? req.getIsPublished() : true)
                                                .build();

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(User user, UUID productId, UpdateProductRequest req) {
        DigitalProduct product = productRepository.findById(productId)
                                                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        if (!product.getCreator().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to this product");
        }

        if (req.getTitle() != null)
            product.setTitle(req.getTitle());
        if (req.getDescription() != null)
            product.setDescription(req.getDescription());
        if (req.getCoverImageUrl() != null)
            product.setCoverImageUrl(req.getCoverImageUrl());
        if (req.getPrice() != null)
            product.setPrice(req.getPrice());
        if (req.getEnableWatermark() != null)
            product.setEnableWatermark(req.getEnableWatermark());
        if (req.getIsPublished() != null)
            product.setIsPublished(req.getIsPublished());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(User user, UUID productId) {
        DigitalProduct product = productRepository.findById(productId)
                                                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getCreator().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to this product");
        }

        productRepository.delete(product);
    }

    private ProductResponse toResponse(DigitalProduct p) {
        return ProductResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .coverImageUrl(p.getCoverImageUrl())
                .price(p.getPrice())
                .masterFileUrl(p.getMasterFileUrl())
                .enableWatermark(p.getEnableWatermark())
                .isPublished(p.getIsPublished())
                .createdAt(p.getCreatedAt())
                .build();
    }
}