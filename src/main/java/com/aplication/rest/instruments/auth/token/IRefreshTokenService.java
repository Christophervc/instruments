package com.aplication.rest.instruments.auth.token;

import java.util.Optional;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(String email);
    RefreshToken verifyExpiration(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
}
