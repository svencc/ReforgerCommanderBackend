package com.recom.security.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RECOMUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        // hier kommt dann eine Implementierung die nachschaut ob der user in der Datenbank existiert!
        // TODO - user-service
        // TODO - user-persistence-service
        // TODO - user-datenbank

        return User.withUsername(username)
                .password("{noop}1234!")
//                    .password("{bcrypt}$2a$12$IE5sllLrm.L3GLli.ZB0vevZihVKgXBvrALic8KnXJ90qVFB0wMtO")
                .authorities(
                        List.of(
                                new SimpleGrantedAuthority(RECOMUserAuthorities.AUTHORITY_EVERYBODY.name()),
                                new SimpleGrantedAuthority(RECOMUserAuthorities.AUTHORITY_TEST.name())
                        )
                )
//                .roles(
//                        RECOMUserRoles.TEST.name(),
//                        RECOMUserRoles.ANYBODY.name()
//                )
                .disabled(false)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
//
//        return switch (username) {
//            case "test" -> User.withUsername(username)
//                    .password("{noop}1234!")
////                    .password("{bcrypt}$2a$12$IE5sllLrm.L3GLli.ZB0vevZihVKgXBvrALic8KnXJ90qVFB0wMtO")
//                    .authorities(
//                            RECOMUserAuthorities.ROLE_USER,
//                            RECOMUserAuthorities.ROLE_ADMIN
//                    )
//                    .roles(
//                            RECOMUserRoles.TEST_USER,
//                            RECOMUserRoles.EVERYBODY
//                    )
//                    .disabled(false)
//                    .accountExpired(false)
//                    .accountLocked(false)
//                    .credentialsExpired(false)
//                    .build();
//            case "candy.kane@mills.info" -> new User(username, "{noop}mk8suwi4kq", Collections.emptyList());
//            default -> throw new UsernameNotFoundException(username);
    }

    ;
}

