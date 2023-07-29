package com.recom.security.user;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class RECOMUser {

    private Set<RECOMUserAuthorities> roles;
    private UUID userUuid;
    private String accessKey;

}
