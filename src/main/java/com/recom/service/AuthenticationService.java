package com.recom.service;

import com.recom.dto.authentication.AccountRequestDto;
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

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
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
    public AccountRequestDto createNewAccount() {
        final Account account = accountPersistenceLayer.createAccount();

        return AccountRequestDto.builder()
                .accountUUID(account.getAccountUuid().toString())
                .accessKey(account.getAccessKey())
                .build();
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
        final Duration expiresIn = recomSecurityProperties.getJwtExpirationTime();
        final Instant expiresAt = now.plus(expiresIn);
        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(recomSecurityProperties.getJwtIssuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(authenticationRequestDto.getAccountUUID())
                .build();

        final String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        return AuthenticationResponseDto.builder()
                .token(jwt)
                .issuedAt(conversionService.convert(now, Date.class))
                .expiresAt(conversionService.convert(expiresAt, Date.class))
                .expiresAtEpoch(expiresAt.getEpochSecond())
                .expiresInSeconds(expiresIn.toSeconds())
                .build();
    }

}
