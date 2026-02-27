package com.aplication.rest.instruments.auth;

import com.aplication.rest.instruments.auth.dto.*;
import com.aplication.rest.instruments.core.error_handling.Result;

public interface IAuthService {
    Result<AuthResponse> register (RegisterRequest request);
    Result<AuthResponse> login (LoginRequest request);
    Result<UserProfileDTO> getMe();
    Result<String> changePassword(ChangePasswordRequest request);
}
