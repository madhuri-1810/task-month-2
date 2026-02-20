package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.exception.*;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public OrderDTO placeOrder(String email, String shippingAddress) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot place order with empty cart");
        }

        // Validate stock for all items
        cart.getItems().forEach(item -> {
            if (item.getProduct().getStockQuantity() < item.getQuantity()) {
                throw new BusinessException("Insufficient stock for: " + item.getProduct().getName());
            }
        });

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            // Deduct stock
            Product p = cartItem.getProduct();
            p.setStockQuantity(p.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(p);

            return OrderItem.builder()
                    .order(order)
                    .product(p)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(p.getPrice())
                    .build();
        }).collect(Collectors.toList());

        BigDecimal total = orderItems.stream()
                .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(orderItems);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return toDTO(saved);
    }

    public Page<OrderDTO> getUserOrders(String email, int page, int size) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return orderRepository.findByUserId(user.getId(), PageRequest.of(page, size))
                .map(this::toDTO);
    }

    public OrderDTO getOrderById(Long orderId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getEmail().equals(email)) {
            throw new BusinessException("Access denied");
        }

        return toDTO(order);
    }

    public Page<OrderDTO> getAllOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size)).map(this::toDTO);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        return toDTO(orderRepository.save(order));
    }

    private OrderDTO toDTO(Order order) {
        List<OrderItemDTO> items = order.getItems().stream().map(item ->
            OrderItemDTO.builder()
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .priceAtPurchase(item.getPriceAtPurchase())
                    .subtotal(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build()
        ).collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .shippingAddress(order.getShippingAddress())
                .build();
    }
}
