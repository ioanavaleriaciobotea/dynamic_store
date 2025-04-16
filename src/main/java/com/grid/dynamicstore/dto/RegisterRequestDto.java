package com.grid.dynamicstore.dto;

import com.grid.dynamicstore.model.User;
import com.grid.dynamicstore.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
public class RegisterRequestDto {

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotNull(message = "Role is required.")
    private UserRole role;

    private String adminKey;

    public User convertToEntity(PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setEmail(this.email);
        user.setPasswordHash(passwordEncoder.encode(this.password));
        user.setRole(this.role);
        return user;
    }
}
