package com.recom.security;

import lombok.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PublicEndpoints {

    @NonNull
    public RequestMatcher publicEndpointsMatcher() {
        return new OrRequestMatcher(publicEndpoints().stream().filter(Objects::nonNull).toList());
    }

    @NonNull
    private static List<RequestMatcher> publicEndpoints() {
        final ArrayList<RequestMatcher> publicEndpoints = new ArrayList<>();
        publicEndpoints.addAll(swaggerEndpoints());
        publicEndpoints.addAll(authenticateEndpoints());

        return publicEndpoints;
    }

    @NonNull
    private static List<RequestMatcher> swaggerEndpoints() {
        return List.of(
                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/swagger-ui.html"),
                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/swagger-ui/**"),
                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/v3/api-docs/**"),
                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/v2/api-docs/**"),
                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/swagger**")
        );
    }

    @NonNull
    private static List<RequestMatcher> authenticateEndpoints() {
        return List.of(
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/v1/authenticate"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/v1/authenticate/new-account")
        );
    }

}
