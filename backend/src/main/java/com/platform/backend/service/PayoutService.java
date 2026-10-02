package com.platform.backend.service;

import com.platform.backend.dto.PayoutDtos.*;
import com.platform.backend.entity.OrderStatus;
import com.platform.backend.entity.PayoutRequest;
import com.platform.backend.entity.PayoutStatus;
import com.platform.backend.entity.User;
import com.platform.backend.repository.OrderRepository;
import com.platform.backend.repository.PayoutRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayoutService {
    private final PayoutRequestRepository payoutRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public CreatorBalanceResponse getBalance(User user) {
        BigDecimal totalGross = orderRepository.sumTotalRevenueByCreatorIdAndStatus(user.getId(), OrderStatus.PAID);
        if (totalGross == null)
            totalGross = BigDecimal.ZERO;

        List<PayoutRequest> allPayouts = payoutRepository.findByCreatorIdOrderByRequestedAtDesc(user.getId());
        BigDecimal pending = allPayouts.stream().filter(p -> p.getStatus() == PayoutStatus.PENDING)
                                                        .map(PayoutRequest::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal completed = allPayouts.stream().filter(p -> p.getStatus() == PayoutStatus.APPROVED)
                                                    .map(PayoutRequest::getAmount)
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal available = totalGross.subtract(pending).subtract(completed).max(BigDecimal.ZERO);

        return CreatorBalanceResponse.builder()
                        .totalGrossRevenue(totalGross)
                        .pendingPayouts(pending)
                        .completedPayouts(completed)
                        .availableBalance(available)
                        .build();
    }

    @Transactional(readOnly = true)
    public List<PayoutResponse> getPayoutsForCreator(User user) {
        return payoutRepository.findByCreatorIdOrderByRequestedAtDesc(user.getId()).stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList());
    }

    @Transactional
    public PayoutResponse requestPayout(User user, CreatePayoutRequest req) {
        CreatorBalanceResponse balance = getBalance(user);

        if (req.getAmount().compareTo(balance.getAvailableBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient available balance for this payout request. Available: Rp " + balance.getAvailableBalance());
        }

        PayoutRequest payout = PayoutRequest.builder()
                                            .creator(user)
                                            .amount(req.getAmount())
                                            .bankName(req.getBankName())
                                            .accountNumber(req.getAccountNumber())
                                            .accountHolderName(req.getAccountHolderName())
                                            .status(PayoutStatus.PENDING)
                                            .build();

        return toResponse(payoutRepository.save(payout));
    }

    private PayoutResponse toResponse(PayoutRequest p) {
        return PayoutResponse.builder()
                        .id(p.getId())
                        .amount(p.getAmount())
                        .bankName(p.getBankName())
                        .accountNumber(p.getAccountNumber())
                        .accountHolderName(p.getAccountHolderName())
                        .status(p.getStatus())
                        .requestedAt(p.getRequestedAt())
                        .processedAt(p.getProcessedAt())
                        .build();
    }
}