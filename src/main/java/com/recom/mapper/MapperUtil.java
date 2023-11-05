package com.recom.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.recom.service.provider.StaticObjectMapperProvider;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class MapperUtil {

    @Nullable
    public String encodeVectorToJsonString(@Nullable final List<BigDecimal> vectorXYZ) throws JsonProcessingException {
        if (vectorXYZ == null) {
            return null;
        } else {
            return StaticObjectMapperProvider.provide().writeValueAsString(vectorXYZ);
        }
    }

    @Nullable
    public List<BigDecimal> decodeJsonStringToVector(@Nullable final String vectorXYZString) throws JsonProcessingException {
        if (vectorXYZString == null) {
            return null;
        } else {
            return StaticObjectMapperProvider.provide().readValue(vectorXYZString, new TypeReference<List<BigDecimal>>() {
            });
        }
    }

    @Nullable
    public String blankStringToNull(@Nullable final String blankableString) throws JsonProcessingException {
        if (blankableString == null) {
            return null;
        } else {
            if (blankableString.isBlank()) {
                return null;
            } else {
                return blankableString;
            }
        }
    }

}
