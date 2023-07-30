package com.recom.security.account;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class RECOMUser {

    private Set<RECOMUserAuthorities> roles;
    private UUID userUuid;
    private String accessKey;

}
