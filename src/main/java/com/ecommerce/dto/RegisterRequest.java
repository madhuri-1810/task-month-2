package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email required")
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String phone;
    private String address;
}
