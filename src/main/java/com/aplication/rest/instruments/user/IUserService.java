package com.aplication.rest.instruments.user;

import com.aplication.rest.instruments.auth.dto.UserProfileDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.user.dto.ChangeRoleRequest;
import com.aplication.rest.instruments.user.dto.ChangeStatusRequest;
import com.aplication.rest.instruments.user.dto.UpdateProfileRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUserService {
    Result<Page<UserProfileDTO>> findAllUsers(Pageable pageable);
    Result<UserProfileDTO> findById(UUID id);
    Result<UserProfileDTO> changeRole(UUID id, ChangeRoleRequest request);
    Result<UserProfileDTO> changeStatus(UUID id, ChangeStatusRequest request);
    Result<UserProfileDTO> updateMe(UpdateProfileRequest request);
}
