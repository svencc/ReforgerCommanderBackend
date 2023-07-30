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
public class JwtTokenService {

    @NonNull
    private final ConversionService conversionService;

    public void assertAuthorizationHeaderIsPresent(@NonNull final Optional<String> authorizationHeader) throws HttpUnauthorizedException {
        if (authorizationHeader.isEmpty()) {
            throw new HttpUnauthorizedException("Authorization header is not present");
        }
    }

    public void assertAuthorizationHeaderStartsWithBearer(@NonNull final String authorizationHeader) throws HttpUnauthorizedException {
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new HttpUnauthorizedException("Authorization header is not present");
        }
    }

    public void assertTokenIsNotExpired(@Nullable final Object expiration) throws HttpUnauthorizedException {
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

    public void assertSubjectIsPresent(@Nullable final Object sub) throws HttpUnauthorizedException {
        if (sub == null) {
            throw new HttpUnauthorizedException("Token subject is not present");
        }
    }

    @NonNull
    public UUID extractAndAssertSubjectIsUUID(@NonNull final String sub) throws HttpUnauthorizedException {
        try {
            return conversionService.convert(sub, UUID.class);
        } catch (ConversionException e) {
            throw new HttpUnauthorizedException("Invalid token; expiration is not a valid date");
        }
    }

    @NonNull
    public String extractToken(@NonNull final String authorizationHeaderOpt) {
        return authorizationHeaderOpt.substring("Bearer".length()).trim();
    }

    @NonNull
    public String validateAnProvideToken(@NonNull final String authorizationHeaderOpt) {
        return authorizationHeaderOpt.substring("Bearer".length()).trim();
    }

}
