package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.LoginRequestDto;
import com.grid.dynamicstore.dto.RegisterRequestDto;
import com.grid.dynamicstore.dto.ResetPasswordDto;
import com.grid.dynamicstore.exception.*;
import com.grid.dynamicstore.exception.SecurityException;
import com.grid.dynamicstore.model.User;
import com.grid.dynamicstore.model.UserRole;
import com.grid.dynamicstore.repository.UserRepository;
import com.grid.dynamicstore.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Value("${app.security.admin-registration-key}")
    private String adminRegistrationKey;

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_TIME = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void register(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEntityException("User already exists!");
        }

        if (dto.getRole() == UserRole.ADMIN) {
            if (!adminRegistrationKey.equals(dto.getAdminKey())) {
                throw new SecurityException("Invalid admin registration key.");
            }
        }

        User user = dto.convertToEntity(passwordEncoder);
        user.setFailedLoginAttempts(0);

        userRepository.save(user);
    }

    @Override
    public void login(LoginRequestDto dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new WrongEmailException("No account found with this email!"));

        handleBruteForce(user);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            recordFailedAttempt(user);
            throw new WrongPasswordException("Incorrect password!");
        }

        resetLoginAttempts(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleBruteForce(User user) {
        Integer attempts = user.getFailedLoginAttempts();
        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            LocalDateTime lastFail = user.getLastFailedLogin();
            if (lastFail != null && Duration.between(lastFail, LocalDateTime.now()).compareTo(LOCK_TIME) < 0) {
                throw new SecurityException("Account temporarily locked. Try again later.");
            } else {
                resetLoginAttempts(user);
            }
        }
    }

    private void recordFailedAttempt(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        user.setLastFailedLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    private void resetLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastFailedLogin(null);
        userRepository.save(user);
    }

    @Override
    public String requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        // In a real life project: send token by email
        return "Generated reset token: " + token;
    }

    @Override
    public void resetPassword(ResetPasswordDto dto) {
        User user = userRepository.findByResetToken(dto.getToken())
                .orElseThrow(() -> new SecurityException("Invalid reset token."));

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetToken(null);
        userRepository.save(user);
    }
}
