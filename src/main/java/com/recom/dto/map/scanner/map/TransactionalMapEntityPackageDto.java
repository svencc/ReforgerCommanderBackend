package com.recom.dto.map.scanner.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.dto.map.scanner.TransactionalMapEntityPackage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TransactionalMapEntityPackageDto implements TransactionalMapEntityPackage<MapEntityDto> {

    @NotEmpty
    @Schema
    @JsonProperty()
    private String sessionIdentifier;

    @NotNull
    @Schema
    @JsonProperty()
    private Integer packageOrder;

    @Schema
    @JsonProperty()
    @Builder.Default
    private List<MapEntityDto> entities = new ArrayList<>();

}
