package com.trafficiq.service;

import com.trafficiq.config.JwtUtils;
import com.trafficiq.dto.LoginRequest;
import com.trafficiq.dto.LoginResponse;
import com.trafficiq.entity.User;
import com.trafficiq.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;

    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        if (!user.getIsActive()) {
            throw new BadCredentialsException("User account is inactive. Please contact administrator.");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getUserId());
        return new LoginResponse(token, user.getUserId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public Optional<User> getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        String email = auth.getName();
        return userRepository.findByEmail(email);
    }
}
