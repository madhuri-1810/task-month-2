package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull @DecimalMin("0.01")
    private BigDecimal price;

    @NotNull @Min(0)
    private Integer stockQuantity;

    private String imageUrl;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
