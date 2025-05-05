package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.LoginRequestDto;
import com.grid.dynamicstore.dto.RegisterRequestDto;
import com.grid.dynamicstore.dto.ResetPasswordDto;
import com.grid.dynamicstore.exception.DuplicateEntityException;
import com.grid.dynamicstore.exception.SecurityException;
import com.grid.dynamicstore.exception.WrongEmailException;
import com.grid.dynamicstore.exception.WrongPasswordException;
import com.grid.dynamicstore.model.User;
import com.grid.dynamicstore.model.UserRole;
import com.grid.dynamicstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, authenticationManager);
    }

    @Test
    void testRegister_Successful() {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password");
        dto.setRole(UserRole.USER);

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded");

        userService.register(dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_DuplicateEmail() {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password");
        dto.setRole(UserRole.USER);

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> userService.register(dto));
    }

    @Test
    void testLogin_Successful() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash("encoded");
        user.setFailedLoginAttempts(0);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));

        userService.login(dto);

        verify(userRepository).save(user); // reset login attempts
    }

    @Test
    void testLogin_WrongEmail() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("unknown@example.com");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThrows(WrongEmailException.class, () -> userService.login(dto));
    }

    @Test
    void testLogin_WrongPassword() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("wrongpass");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash("correct");
        user.setFailedLoginAttempts(0);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> userService.login(dto));
    }

    @Test
    void testRequestPasswordReset() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        String result = userService.requestPasswordReset(email);

        assertTrue(result.contains("Generated reset token:"));
        verify(userRepository).save(user);
    }

    @Test
    void testResetPassword_Successful() {
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setToken("token");
        dto.setNewPassword("newPass");

        User user = new User();
        user.setResetToken("token");

        when(userRepository.findByResetToken("token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encoded");

        userService.resetPassword(dto);

        assertEquals("encoded", user.getPasswordHash());
        assertNull(user.getResetToken());
        verify(userRepository).save(user);
    }

    @Test
    void testResetPassword_InvalidToken() {
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setToken("invalid");

        when(userRepository.findByResetToken("invalid")).thenReturn(Optional.empty());

        assertThrows(SecurityException.class, () -> userService.resetPassword(dto));
    }
}
