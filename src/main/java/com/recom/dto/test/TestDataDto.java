package com.recom.dto.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestDataDto {

    @Schema
    @JsonProperty()
    private String stringValue;

    @Schema
    @JsonProperty()
    @Builder.Default
    private List<NestedTestDataDto> nestedDataList = new ArrayList<>();

}
