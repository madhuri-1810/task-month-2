package com.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    private BigDecimal totalPrice;
    private Integer totalItems;
}
