package com.recom.service;

import com.recom.dto.authentication.AuthenticationRequestDto;
import com.recom.dto.authentication.AuthenticationResponseDto;
import com.recom.property.RECOMSecurityProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @NonNull
    private final JwtEncoder jwtEncoder;
    @NonNull
    private final RECOMSecurityProperties recomSecurityProperties;

    @NonNull
    public AuthenticationResponseDto authenticateWith(@NonNull final AuthenticationRequestDto authenticationRequestDto) {
        final Instant now = Instant.now();
        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(recomSecurityProperties.getJwtIssuer())
                .issuedAt(now)
                .expiresAt(now.plus(recomSecurityProperties.getJwtExpirationTime()))
                .subject(authenticationRequestDto.getUsername())
                .claim("password", authenticationRequestDto.getPassword())
                .build();

        final String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        return AuthenticationResponseDto.builder()
                .bearerToken(jwt)
                .build();
    }

}
