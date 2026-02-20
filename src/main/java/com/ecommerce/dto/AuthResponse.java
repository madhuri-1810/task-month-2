package com.ecommerce.dto;

import lombok.*;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String name;
    private String role;
}
