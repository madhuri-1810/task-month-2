package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Shopping cart APIs")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    @Autowired private CartService cartService;

    @GetMapping
    @Operation(summary = "View current user cart")
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(user.getUsername())));
    }

    @PostMapping("/add")
    @Operation(summary = "Add product to cart")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(ApiResponse.success(cartService.addToCart(user.getUsername(), productId, quantity)));
    }

    @PutMapping("/update/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartDTO>> updateItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(ApiResponse.success(cartService.updateCartItem(user.getUsername(), itemId, quantity)));
    }

    @DeleteMapping("/remove/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartDTO>> removeItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.removeFromCart(user.getUsername(), itemId)));
    }
}
