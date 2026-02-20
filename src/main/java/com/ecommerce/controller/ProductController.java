package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    @Autowired private ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products with pagination")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts(page, size, sortBy)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by keyword")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchProducts(keyword, page, size)));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByCategory(categoryId, page, size)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create product (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", productService.createProduct(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable Long id, @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }
}
