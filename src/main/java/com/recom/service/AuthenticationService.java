package com.recom.service;

import com.recom.dto.authentication.AuthenticationRequestDto;
import com.recom.dto.authentication.AuthenticationResponseDto;
import com.recom.entity.Account;
import com.recom.exception.HttpUnauthorizedException;
import com.recom.persistence.account.AccountPersistenceLayer;
import com.recom.property.RECOMSecurityProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @NonNull
    private final JwtEncoder jwtEncoder;
    @NonNull
    private final ConversionService conversionService;
    @NonNull
    private final RECOMSecurityProperties recomSecurityProperties;
    @NonNull
    private final AccountPersistenceLayer accountPersistenceLayer;

    @NonNull
    public AuthenticationRequestDto createNewAccount() {
        return Optional.of(accountPersistenceLayer.createAccount())
                .map(account -> AuthenticationRequestDto.builder()
                        .accountUUID(account.getAccountUuid().toString())
                        .accessKey(account.getAccessKey())
                        .build()
                )
                .get();
    }

    @NonNull
    public AuthenticationResponseDto authenticateWith(@NonNull final AuthenticationRequestDto authenticationRequestDto) {
        final Account account = Optional.ofNullable(conversionService.convert(authenticationRequestDto.getAccountUUID(), UUID.class))
                .flatMap(accountPersistenceLayer::findByUUID)
                .orElseThrow(() -> new HttpUnauthorizedException("Account not found"));

        if (!account.getAccessKey().equals(authenticationRequestDto.getAccessKey())) {
            throw new HttpUnauthorizedException("Invalid access key");
        }

        final Instant now = Instant.now();
        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(recomSecurityProperties.getJwtIssuer())
                .issuedAt(now)
                .expiresAt(now.plus(recomSecurityProperties.getJwtExpirationTime()))
                .subject(authenticationRequestDto.getAccountUUID())
                .claim("accessKey", authenticationRequestDto.getAccessKey())
                .build();

        final String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        return AuthenticationResponseDto.builder()
                .bearerToken(jwt)
                .build();
    }

}
