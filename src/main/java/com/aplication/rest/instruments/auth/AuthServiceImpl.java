package com.aplication.rest.instruments.auth;

import com.aplication.rest.instruments.auth.dto.*;
import com.aplication.rest.instruments.auth.jwt.JwtService;
import com.aplication.rest.instruments.auth.token.IRefreshTokenService;
import com.aplication.rest.instruments.auth.token.RefreshToken;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.core.exceptions.ValidationException;
import com.aplication.rest.instruments.user.User;
import com.aplication.rest.instruments.user.UserRepository;
import com.aplication.rest.instruments.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IRefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public Result<AuthResponse> register(RegisterRequest request) {
        //1. Validate if email or dni already exist
        if (userRepository.existsByEmail(request.email())) {
            throw new ValidationException("Email address is already registered.");
        }
        if (userRepository.existsByDni(request.dni())) {
            throw new ValidationException("Dni is already registered");
        }
        //2. Build the user
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .dni(request.dni())
                .phone(request.phone())
                .role(Role.ROLE_CUSTOMER)
                .active(true)
                .build();
        //3. save in DB
        User savedUser =userRepository.save(user);
        //4. generate token with extra claims
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFirstName() + " " + user.getLastName());
        extraClaims.put("role", user.getRole().name());

        String jwtAccessToken = jwtService.generateToken(extraClaims, user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return Result.success(new AuthResponse(jwtAccessToken,  refreshToken.getToken(), "Register successfully" ));
    }

    @Override
    public Result<AuthResponse> login(LoginRequest request) {
        //Call manager to verify credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getActive()) {
            throw new ValidationException("This account has been deactivated");
        }
        //Generate token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFirstName() + " " + user.getLastName());
        extraClaims.put("role", user.getRole().name());

        String jwtAccessToken = jwtService.generateToken(extraClaims, user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return Result.success(
                new AuthResponse(jwtAccessToken, refreshToken.getToken(), "Login successfully")
        );
    }

    @Override
    public Result<UserProfileDTO> getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
        UserProfileDTO profileDTO = new UserProfileDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDni(),
                user.getPhone(),
                user.getRole().name()
        );
        return Result.success(profileDTO);
    }

    @Override
    @Transactional
    public Result<String> changePassword(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new ValidationException("The new password must not match the previous one.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return Result.success("The password has been successfully updated.");
    }

    @Override
    public Result<AuthResponse> refreshToken(RefreshTokenRequest request) {

        return refreshTokenService.findByToken(request.refreshToken())
                //verify token expiration if its expired throw error and removed from db
                .map(refreshTokenService::verifyExpiration)
                //get user associated with the token
                .map(RefreshToken::getUser)
                //generate new access token
                .map(user-> {
                    String newAccessToken = jwtService.generateToken(user);
                    return Result.success(AuthResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(request.refreshToken())
                            .message("token has been successfully updated")
                            .build());
                })
                .orElseThrow(()-> new RuntimeException("Invalid Refresh Token or not found"));
    }
}
