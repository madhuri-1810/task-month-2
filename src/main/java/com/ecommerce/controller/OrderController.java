package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    @Autowired private OrderService orderService;

    @PostMapping("/api/orders")
    public ResponseEntity<ApiResponse<OrderDTO>> placeOrder(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) String shippingAddress) {
        OrderDTO order = orderService.placeOrder(user.getUsername(), shippingAddress);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", order));
    }

    @GetMapping("/api/orders")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getUserOrders(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(user.getUsername(), page, size)));
    }

    @GetMapping("/api/orders/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id, user.getUsername())));
    }

    // Admin endpoints
    @GetMapping("/api/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(page, size)));
    }

    @PutMapping("/api/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", orderService.updateOrderStatus(id, status)));
    }
}
