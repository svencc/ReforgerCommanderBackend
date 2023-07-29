package com.recom.security.jwt;

import com.recom.exception.HttpUnauthorizedException;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenAssertionService {

    @NonNull
    private final ConversionService conversionService;

    public void assertAuthorizationHeaderIsPresent(@NonNull final Optional<String> authorizationHeader) {
        if (authorizationHeader.isEmpty()) {
            throw new HttpUnauthorizedException("Authorization header is not present");
        }
    }

    public void assertAuthorizationHeaderStartsWithBearer(@NonNull final String authorizationHeader) {
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new HttpUnauthorizedException("Authorization header is not present");
        }
    }

    public void assertTokenIsNotExpired(@Nullable final Object expiration) {
        if (expiration == null) {
            throw new HttpUnauthorizedException("Token expiration is not present");
        } else {
            try {
                final Instant expirationDate = conversionService.convert(expiration, Instant.class);
                if (!Instant.now().isBefore(expirationDate)) {
                    throw new HttpUnauthorizedException("Token is expired");
                }
            } catch (ConversionException e) {
                throw new HttpUnauthorizedException("Invalid token; expiration is not a valid date");
            }
        }
    }

    public void assertSubjectIsPresent(@Nullable final Object sub) {
        if (sub == null) {
            throw new HttpUnauthorizedException("Token subject is not present");
        }
    }

    @NonNull
    public UUID extractAndAssertSubjectIsUUID(@NonNull final String sub) {
        try {
            return conversionService.convert(sub, UUID.class);
        } catch (ConversionException e) {
            throw new HttpUnauthorizedException("Invalid token; expiration is not a valid date");
        }
    }
}
