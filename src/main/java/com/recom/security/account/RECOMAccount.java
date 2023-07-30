package com.recom.security.account;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class RECOMAccount {

    private Set<RECOMAuthorities> roles;
    private UUID accountUuid;
    private String accessKey;

}
