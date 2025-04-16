package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.LoginRequestDto;
import com.grid.dynamicstore.dto.RegisterRequestDto;
import com.grid.dynamicstore.exception.DuplicateEntityException;
import com.grid.dynamicstore.exception.SecurityException;
import com.grid.dynamicstore.exception.WrongEmailException;
import com.grid.dynamicstore.exception.WrongPasswordException;
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

@Service
public class UserServiceImpl implements UserService {

    @Value("${app.security.admin-registration-key}")
    private String adminRegistrationKey;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean register(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEntityException("User already exists!");
        }

        if (dto.getRole() == UserRole.ADMIN) {
            if (!adminRegistrationKey.equals(dto.getAdminKey())) {
                throw new SecurityException("Invalid admin registration key.");
            }
        }

        User user = dto.convertToEntity(passwordEncoder);

        userRepository.save(user);

        return true;
    }

    @Override
    public void login(LoginRequestDto dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new WrongEmailException("No account found with this email!"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new WrongPasswordException("Incorrect password!");
        }


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
