package com.aplication.rest.instruments.config.rate_limit;

import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientKey = request.getRemoteAddr(); // IP Address by default if user is not authenticated (guest)
        String uri = request.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            clientKey = authentication.getName(); // if user has logged in, use client email
        }

        //Bucket Decision based on the URL
        Bucket tokenBucket;
        // Normal Endpoints
        if (uri.contains("/export/excel") || (uri.contains("/orders") && request.getMethod().equals("POST"))) {
            // Heavy Endpoints
            tokenBucket = rateLimitingService.resolveHeavyBucket(clientKey);
        } else tokenBucket = rateLimitingService.resolveBucket(clientKey);
        /* consume 1 token */
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Success: add remaining tokens to response header
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true; // let the request pass
        } else {
            // Failure: add retry after to response header
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429 Error too many requests
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ApiError error = new ApiError("TOO_MANY_REQUEST", "Rate limit is exceeded try again in "+waitForRefill+" seconds.");
            Result<Object> result = Result.isFailure(error);
            response.getWriter().write(objectMapper.writeValueAsString(result));

            return false; // Block request
        }
    }
}
