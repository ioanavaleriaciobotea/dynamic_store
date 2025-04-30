package com.grid.dynamicstore.service;

import com.grid.dynamicstore.dto.LoginRequestDto;
import com.grid.dynamicstore.dto.RegisterRequestDto;
import com.grid.dynamicstore.dto.ResetPasswordDto;

public interface UserService {
    void register(RegisterRequestDto dto);
    void login(LoginRequestDto dto);
    String requestPasswordReset(String email);
    void resetPassword(ResetPasswordDto dto);
}
