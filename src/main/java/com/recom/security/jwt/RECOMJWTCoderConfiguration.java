package com.recom.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;


@Configuration
public class RECOMJWTCoderConfiguration {

    @Bean("RECOMSecurityJwtDecoder")
    public JwtDecoder JwtDecoderFactory(@NonNull final RSAKey rsaKey) throws JOSEException {
        return NimbusJwtDecoder
                .withPublicKey(rsaKey.toRSAPublicKey())
                .build();
    }

    @Bean("RECOMSecurityJwtEncoder")
    public JwtEncoder jwtEncoderFactory(@NonNull final RSAKey rsaKey) {
        return new NimbusJwtEncoder(
                new ImmutableJWKSet<>(new JWKSet(rsaKey))
        );
    }

}
