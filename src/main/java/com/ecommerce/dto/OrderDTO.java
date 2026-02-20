package com.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class OrderDTO {
    private Long id;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private String shippingAddress;
}
