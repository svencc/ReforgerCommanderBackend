package com.recom.api;

import com.recom.dto.test.NestedTestDataDto;
import com.recom.dto.test.TestDataDto;
import com.recom.util.JSNumberUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Test")
@RequestMapping("/api/v1/test")
public class TestController {

    @Operation(
            summary = "Test JSON-GET call",
            description = "Returns a static JSON demo structure."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "O.K.")
    })
    @GetMapping(path = "/json-test-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestDataDto> getJsonTestData() {
        log.info("Requested GET /api/v1/test/json-test-data");

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(TestDataDto.builder()
                        .stringValue("some-text-data")
                        .nestedDataList(List.of(
                                NestedTestDataDto.builder()
                                        .stringValue("another-text-data")
                                        .numberValue(JSNumberUtil.of(5))
                                        .build(),
                                NestedTestDataDto.builder()
                                        .stringValue("some-different-text-data")
                                        .numberValue(JSNumberUtil.of(2.5))
                                        .build()
                        ))
                        .build());
    }

}