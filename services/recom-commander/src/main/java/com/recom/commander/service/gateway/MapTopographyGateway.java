package com.recom.commander.service.gateway;

import com.recom.commander.exception.HttpErrorException;
import com.recom.commander.exception.RequestLogger;
import com.recom.commander.property.gateway.MapTopographyGatewayProperties;
import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.dto.map.topography.MapTopographyRequestDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MapTopographyGateway {

    @NonNull
    @Qualifier("recomRestClient")
    private final RestClient recomRestClient;
    @NonNull
    private final RequestLogger requestLogger;
    @NonNull
    private final MapTopographyGatewayProperties mapTopographyGatewayProperties;

    @NonNull
    public HeightMapDescriptorDto provideMapTopographyData() throws HttpErrorException {
        final Instant start = Instant.now();
        final ResponseEntity<HeightMapDescriptorDto> responseEntity = recomRestClient
                .post()
                .uri(mapTopographyGatewayProperties.getEndpoint())
                .accept(MediaType.APPLICATION_JSON)
                .body(MapTopographyRequestDto.builder()
                        .mapName("$RECOMClient:worlds/Everon_CTI/RefCom_CTI_Campaign_Eden.ent")
                        .build()
                )
                .retrieve()
                .onStatus(
                        httpStatus -> !httpStatus.is2xxSuccessful(),
                        requestLogger::logRequestInErrorCase
                )
                .onStatus(
                        HttpStatusCode::is2xxSuccessful,
                        (request, response) -> requestLogger.profileRequest(start)
                )
                .toEntity(HeightMapDescriptorDto.class);

        return responseEntity.getBody();
    }

    public void doSomething() {
        // https://spring.io/blog/2023/07/13/new-in-spring-6-1-restclient
        final Map<String, String> uriVariables = Map.of("id", "1");
        final UriComponents localhost = UriComponentsBuilder.newInstance()
//                .host("localhost")
//                .port(8080)
                .path("/persons/{id}")
                .buildAndExpand(uriVariables);
//                .buildAndExpand(1);

        ResponseEntity<HeightMapDescriptorDto> topography = recomRestClient.get()
                .uri(localhost.toUri())
//                .uri("/persons/1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(HeightMapDescriptorDto.class);

        topography.getStatusCode();
        topography.getBody();


//        HeightMapDescriptorDto response2 = restClient.get()
//                .uri("/persons/1")
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                    throw new RuntimeException("4xx");
//                })
//                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
//                    request.
//                    throw new RuntimeException("4xx");
//                })
//                .body(HeightMapDescriptorDto.class);


//        https://stackoverflow.com/questions/8297215/spring-resttemplate-get-with-parameters
//        https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface


    }

}
