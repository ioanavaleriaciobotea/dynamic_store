package com.grid.dynamicstore.controller;

import com.grid.dynamicstore.dto.LoginRequestDto;
import com.grid.dynamicstore.dto.RegisterRequestDto;
import com.grid.dynamicstore.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto dto) {
        boolean success = userService.register(dto);

        if (!success) {
            return ResponseEntity.status(409).body("User with this email already exists.");
        }

        return ResponseEntity.ok("Registration successful.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginRequestDto dto,
            HttpServletRequest request) {

        try {
            userService.login(dto);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            String sessionId = session.getId();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful.");
            response.put("sessionId", sessionId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials."));
        }
    }
}
