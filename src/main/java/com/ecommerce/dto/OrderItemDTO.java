package com.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;
}
