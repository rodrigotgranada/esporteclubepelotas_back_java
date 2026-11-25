package com.esporteclubepelotas.api.modules.integrations.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class StoreOrderBridgeDto {

    private StoreOrderBridgeDto() {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreSsoTokenResponseDto {
        private String ssoToken;
        private int expiresInSeconds;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortalStoreOrderItemDto {
        private String productName;
        private String sku;
        private String sizeLabel;
        private String colorLabel;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal appliedPrice;
        private BigDecimal lineTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortalStoreOrderSummaryDto {
        private String id;
        private String orderCode;
        private String channel;
        private String status;
        private boolean isSocioDiscountApplied;
        private BigDecimal subtotalAmount;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private String whatsappSummaryText;
        private Instant expiresAt;
        private Instant confirmedAt;
        private Instant cancelledAt;
        private String cancelledReason;
        private Instant createdAt;
        private List<PortalStoreOrderItemDto> items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortalStoreOrdersResponseDto {
        private boolean storeOnline;
        private String message;
        private List<PortalStoreOrderSummaryDto> orders;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelStoreOrderRequestDto {
        private String reason;
    }
}
