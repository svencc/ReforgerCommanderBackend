package com.recom.security.jwt;

import com.recom.exception.HttpUnauthorizedException;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    @NonNull
    private final ConversionService conversionService;

    @NonNull
    public String passThroughIfPresent(@NonNull final Optional<String> header) throws HttpUnauthorizedException {
        if (header.isEmpty()) {
            throw new HttpUnauthorizedException("Value is not present");
        } else {
            return header.get();
        }
    }

    public void assertAuthorizationStartsWithBearer(@NonNull final String authorizationHeader) throws HttpUnauthorizedException {
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new HttpUnauthorizedException("Authorization header is malformed");
        }
    }

    @NonNull
    public String passThroughIfClaimIsPresent(@Nullable final Object sub) throws HttpUnauthorizedException {
        if (sub == null) {
            throw new HttpUnauthorizedException("Claim is not present");
        }

        return sub.toString();
    }

    @NonNull
    public UUID extractAndAssertSubjectIsUUID(@NonNull final String sub) throws HttpUnauthorizedException {
        try {
            return conversionService.convert(sub, UUID.class);
        } catch (ConversionException e) {
            throw new HttpUnauthorizedException("Invalid token; subject is not an UUID!");
        }
    }

    @NonNull
    public String extractToken(@NonNull final String authorizationHeader) {
        return authorizationHeader.substring("Bearer".length()).trim();
    }

}
