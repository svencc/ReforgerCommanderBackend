package com.rcb.api;

import com.rcb.dto.test.NestedTestDataDto;
import com.rcb.dto.test.TestDataDto;
import com.rcb.util.JSNumber;
import com.rcb.util.ReforgerPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@Tag(name = "Health")
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {

    @Operation(
            summary = "Test JSON-GET call",
            description = "Returns a static JSON demo structure.",
            tags = "test"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "O.K.")
    })
    @GetMapping(path = "/json-test-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestDataDto> getJsonTestData() {
        log.debug("Requested GET /api/v1/test/json-test-data");

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(TestDataDto.builder()
                        .stringValue("some-text-data")
                        .nestedDataList(List.of(
                                NestedTestDataDto.builder()
                                        .stringValue("another-text-data")
                                        .numberValue(JSNumber.of(5))
                                        .build(),
                                NestedTestDataDto.builder()
                                        .stringValue("some-different-text-data")
                                        .numberValue(JSNumber.of(2.5))
                                        .build()
                        ))
                        .build());
    }

    @Operation(
            summary = "Test JSON-POST call",
            description = "Receives a String and converts it into a JSON structure.",
            tags = "test"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "O.K.")
    })
    @PostMapping(path = "/json-test-data", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> postJsonTestData(
            @RequestParam Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/test/json-test-data");

        final Optional<TestDataDto> testDataDtoOpt = ReforgerPayload.parse(payload, TestDataDto.class);
        log.info(" -> Received: {}", testDataDtoOpt);

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

}
