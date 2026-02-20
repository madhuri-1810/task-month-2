package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.exception.*;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    public CartDTO getCart(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
        return toDTO(cart);
    }

    @Transactional
    public CartDTO addToCart(String email, Long productId, int quantity) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresentOrElse(
                    item -> { item.setQuantity(item.getQuantity() + quantity); cartItemRepository.save(item); },
                    () -> cartItemRepository.save(CartItem.builder().cart(cart).product(product).quantity(quantity).build())
                );

        return toDTO(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartDTO updateCartItem(String email, Long itemId, int quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        User user = userRepository.findByEmail(email).orElseThrow();
        return toDTO(cartRepository.findByUserId(user.getId()).orElseThrow());
    }

    @Transactional
    public CartDTO removeFromCart(String email, Long itemId) {
        cartItemRepository.deleteById(itemId);
        User user = userRepository.findByEmail(email).orElseThrow();
        return toDTO(cartRepository.findByUserId(user.getId()).orElseThrow());
    }

    private CartDTO toDTO(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream().map(item -> {
            BigDecimal subtotal = item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            return CartItemDTO.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productPrice(item.getProduct().getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(subtotal)
                    .build();
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
                .id(cart.getId())
                .items(items)
                .totalPrice(total)
                .totalItems(items.size())
                .build();
    }
}
