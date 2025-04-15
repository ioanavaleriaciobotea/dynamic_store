package com.grid.dynamicstore.service;

import com.grid.dynamicstore.dto.LoginRequestDto;
import com.grid.dynamicstore.dto.RegisterRequestDto;

public interface UserService {
    boolean register(RegisterRequestDto dto);
    void login(LoginRequestDto dto);
}
