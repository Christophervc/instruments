package com.aplication.rest.instruments.user;

import com.aplication.rest.instruments.auth.dto.UserProfileDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUserService {
    Result<Page<UserProfileDTO>> findAllUsers(Pageable pageable);
    Result<UserProfileDTO> findById(UUID id);
}
