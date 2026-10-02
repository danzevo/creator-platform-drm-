package com.platform.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.backend.dto.CreatorDtos.*;
import com.platform.backend.entity.*;
import com.platform.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreatorProfileService {
    private final CreatorProfileRepository profileRepository;
    private final BioBlockRepository bioBlockRepository;
    private final DigitalProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PayoutRequestRepository payoutRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public PublicCreatorProfileResponse getPublicProfile(String username) {
        CreatorProfile profile = profileRepository.findByUsername(username.toLowerCase())
                                                    .orElseThrow(() -> new IllegalArgumentException("Creator profile not found for @" + username));

        List<BioBlock> blocks = bioBlockRepository.findByProfileIdAndIsEnabledTrueOrderBySortOrderAsc(profile.getId());
        List<DigitalProduct> products = productRepository.findByCreatorIdAndIsPublishedTrueOrderByCreatedAtDesc(profile.getUser().getId());
        List<PublicBioBlockDto> blockDtos = blocks.stream()
                                            .map(b -> PublicBioBlockDto.builder()
                                                    .id(b.getId())
                                                    .title(b.getTitle())
                                                    .url(b.getUrl())
                                                    .iconUrl(b.getIconUrl())
                                                    .blockType(b.getBlockType())
                                                    .sortOrder(b.getSortOrder())
                                                    .build()
                                            )
                                            .collect(Collectors.toList());
        List<PublicProductDto> productDtos = products.stream()
                                                    .map(p -> PublicProductDto.builder()
                                                                .id(p.getId())
                                                                .title(p.getTitle())
                                                                .description(p.getDescription())
                                                                .coverImageUrl(p.getCoverImageUrl())
                                                                .price(p.getPrice())
                                                                .enableWatermark(p.getEnableWatermark())
                                                                .build()
                                                    )
                                                    .collect(Collectors.toList());
        
        return PublicCreatorProfileResponse.builder()
                    .id(profile.getId())
                    .username(profile.getUsername())
                    .displayName(profile.getDisplayName())
                    .bio(profile.getBio())
                    .avatarUrl(profile.getAvatarUrl())
                    .themeConfigJson(profile.getThemeConfigJson())
                    .blocks(blockDtos)
                    .products(productDtos)
                    .build();
    }

    @Transactional(readOnly = true)
    public CreatorDashboardResponse getDashboard(User user) {
        CreatorProfile profile = profileRepository.findByUserId(user.getId())
                                                    .orElseThrow(() -> new IllegalArgumentException("Creator profile not found"));

        BigDecimal grossRevenue = orderRepository.sumTotalRevenueByCreatorIdAndStatus(user.getId(), OrderStatus.PAID);

        if (grossRevenue == null)
        grossRevenue = BigDecimal.ZERO;

        List<PayoutRequest> payouts = payoutRepository.findByCreatorIdOrderByRequestedAtDesc(user.getId());
        BigDecimal totalPaidOrPending = payouts.stream()
                                                .filter(p -> p.getStatus() == PayoutStatus.APPROVED || p.getStatus() == PayoutStatus.PENDING)
                                                .map(PayoutRequest::getAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal availableBalance = grossRevenue.subtract(totalPaidOrPending).max(BigDecimal.ZERO);
        long ordersCount = orderRepository.findByCreatorIdOrderByCreatedAtDesc(user.getId()).size();
        long productsCount = productRepository.findByCreatorIdOrderByCreatedAtDesc(user.getId()).size();

        return CreatorDashboardResponse.builder()
                .username(profile.getUsername())
                .displayName(profile.getDisplayName())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .themeConfigJson(profile.getThemeConfigJson())
                .totalRevenue(grossRevenue)
                .availableBalance(availableBalance)
                .totalOrdersCount(ordersCount)
                .totalProductsCount(productsCount)
                .build();
    }

    @Transactional
    public CreatorProfile updateProfile(User user, UpdateProfileRequest req) {
        CreatorProfile profile = profileRepository.findByUserId(user.getId())
                                                .orElseThrow(() -> new IllegalArgumentException("Creator profile not found"));
        
        profile.setDisplayName(req.getDisplayName());
        profile.setBio(req.getBio());
        if (req.getAvatarUrl() != null) {
            profile.setAvatarUrl(req.getAvatarUrl());
        }

        return profileRepository.save(profile);
    }

    @Transactional
    public CreatorProfile updateTheme(User user, ThemeConfigRequest themeReq) {
        CreatorProfile profile = profileRepository.findByUserId(user.getId())
                                                .orElseThrow(() -> new IllegalArgumentException("Creator profile not found"));

        try {
            String themeJson = objectMapper.writeValueAsString(themeReq);
            profile.setThemeConfigJson(themeJson);

            return profileRepository.save(profile);
        } catch(Exception e) {
            throw new IllegalArgumentException("Invalid theme configuration format");
        }
    }
}