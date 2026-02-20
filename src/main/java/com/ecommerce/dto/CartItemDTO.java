package com.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}
