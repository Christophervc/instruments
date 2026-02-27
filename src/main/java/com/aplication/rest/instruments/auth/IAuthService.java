package com.aplication.rest.instruments.auth;

import com.aplication.rest.instruments.auth.dto.AuthResponse;
import com.aplication.rest.instruments.auth.dto.LoginRequest;
import com.aplication.rest.instruments.auth.dto.RegisterRequest;
import com.aplication.rest.instruments.auth.dto.UserProfileDTO;
import com.aplication.rest.instruments.core.error_handling.Result;

public interface IAuthService {
    Result<AuthResponse> register (RegisterRequest request);
    Result<AuthResponse> login (LoginRequest request);
    Result<UserProfileDTO> getMe();
}
