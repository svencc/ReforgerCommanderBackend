package com.recom.security;

import com.recom.entity.Account;
import com.recom.security.account.RECOMAccount;
import com.recom.security.account.RECOMAuthentication;
import com.recom.security.account.RECOMAuthorities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RECOMAuthenticationMapperTest {

    private RECOMAuthenticationMapper authenticationMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void toAuthentication() {
    }

    @BeforeEach
    public void setup() {
        authenticationMapper = new RECOMAuthenticationMapper();
    }

    @Test
    public void testToAuthentication() {
        // Arrange
        final UUID accountUuid = UUID.randomUUID();
        final Account mockUser = mock(Account.class);
        when(mockUser.getAccountUuid()).thenReturn(accountUuid);
        when(mockUser.getAccessKey()).thenReturn("user-access-key");

        // Act
        final RECOMAuthentication authentication = authenticationMapper.toAuthentication(mockUser);

        // Assert
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());

        final RECOMAccount principal = (RECOMAccount) authentication.getPrincipal();
        assertNotNull(principal);
        assertEquals(accountUuid, principal.getAccountUuid());
        assertEquals("user-access-key", principal.getAccessKey());

        final Set<String> expectedRoles = Set.of(RECOMAuthorities.AUTHORITY_TEST.name(), RECOMAuthorities.AUTHORITY_EVERYBODY.name());
        final Set<String> actualRoles = authentication.getAuthorities().stream()
                .map(authority -> (SimpleGrantedAuthority) authority)
                .map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertEquals(expectedRoles, actualRoles);
    }

    @Test
    public void testToAuthentication_withNullUser_shouldFail() {
        // Expect an exception when a null Account is passed to toAuthentication
        assertThrows(NullPointerException.class, () -> {
            authenticationMapper.toAuthentication(null);
        });
    }

}
