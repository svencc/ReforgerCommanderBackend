package com.recom.commander.service.maptopography;

import com.recom.commander.exception.RequestLogger;
import com.recom.commander.exception.exceptions.http.HttpErrorException;
import com.recom.commander.exception.exceptions.http.HttpNoConnectionException;
import com.recom.commander.property.gateway.MapTopographyGatewayProperties;
import com.recom.commander.property.restclient.RECOMRestClientProvider;
import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.dto.map.topography.MapTopographyRequestDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MapTopographyGateway {

    @NonNull
    private final RECOMRestClientProvider recomRestClientProvider;
    @NonNull
    private final MapTopographyGatewayProperties mapTopographyGatewayProperties;
    @NonNull
    private final RequestLogger requestLogger;


    @NonNull
    public HeightMapDescriptorDto provideMapTopographyData() throws HttpErrorException {
        try {
            final Instant started = Instant.now();
            final MapTopographyRequestDto body = MapTopographyRequestDto.builder()
                    .mapName("$RECOMClient:worlds/Everon_CTI/RefCom_CTI_Campaign_Eden.ent")
                    .build();
            final ResponseEntity<HeightMapDescriptorDto> responseEntity = recomRestClientProvider.provide()
                    .post()
                    .uri(mapTopographyGatewayProperties.getEndpoint())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.IMAGE_PNG)
                    .body(body)
                    .retrieve()
                    .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(), (request, response) -> requestLogger.logRequestInErrorCase(request, response, null, body))
                    //.onStatus(HttpStatusCode::is2xxSuccessful, (request, response) -> requestLogger.profileRequest(request, response, body, null.started))
                    .toEntity(HeightMapDescriptorDto.class);

            return responseEntity.getBody();
        } catch (final ResourceAccessException e) {
            throw new HttpNoConnectionException(getClass(), e);
        }
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

        ResponseEntity<HeightMapDescriptorDto> topography = recomRestClientProvider.provide()
                .get()
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
