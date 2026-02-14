package com.aplication.rest.instruments.config.rate_limit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        //Bucket Decision based on the URL
        Bucket tokenBucket;
        // Normal Endpoints
        if (uri.contains("/export/excel") || (uri.contains("/orders") && request.getMethod().equals("POST"))) {
            // Heavy Endpoints
            tokenBucket = rateLimitingService.resolveHeavyBucket(ip);
        } else tokenBucket = rateLimitingService.resolveBucket(ip);
        /* 2 consume 1 token */
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
            response.getWriter().write("Too many requests. Please try again in " + waitForRefill + " seconds.");

            return false; // Block request
        }
    }
}
