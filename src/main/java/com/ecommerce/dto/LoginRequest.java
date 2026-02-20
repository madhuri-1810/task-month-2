package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class LoginRequest {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;
}
