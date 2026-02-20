package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.exception.BusinessException;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import com.ecommerce.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtTokenProvider tokenProvider;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role("USER")
                .build();

        User saved = userRepository.save(user);

        // Create empty cart for new user
        Cart cart = Cart.builder().user(saved).build();
        cartRepository.save(cart);

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        return AuthResponse.builder()
                .token(tokenProvider.generateToken(auth))
                .email(saved.getEmail())
                .name(saved.getName())
                .role(saved.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        return AuthResponse.builder()
                .token(tokenProvider.generateToken(auth))
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
