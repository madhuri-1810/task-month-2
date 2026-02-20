package com.ecommerce.model;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "cart_items")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Min(1)
    private Integer quantity;
}
