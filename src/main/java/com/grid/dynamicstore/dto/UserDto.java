package com.grid.dynamicstore.dto;

import com.grid.dynamicstore.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {

    private Long id;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }
}
