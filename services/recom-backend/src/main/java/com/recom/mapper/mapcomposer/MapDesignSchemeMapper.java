package com.recom.mapper.mapcomposer;

import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignSchemeImplementation;
import com.recom.dto.map.mapcomposer.MapDesignSchemeDto;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapDesignSchemeMapper {

    MapDesignSchemeMapper INSTANCE = Mappers.getMapper(MapDesignSchemeMapper.class);


    @NonNull
    @Mapping(source = "baseColorTerrain", target = "baseColorTerrain", qualifiedByName = "hexStringToInteger")
    @Mapping(source = "baseColorWater", target = "baseColorWater", qualifiedByName = "hexStringToInteger")
    @Mapping(source = "baseColorForest", target = "baseColorForest", qualifiedByName = "hexStringToInteger")
    @Mapping(source = "baseColorContourBackground", target = "baseColorContourBackground", qualifiedByName = "hexStringToInteger")
    @Mapping(source = "baseColorContourLineTerrain", target = "baseColorContourLineTerrain", qualifiedByName = "hexStringToInteger")
    @Mapping(source = "baseColorContourLineWater", target = "baseColorContourLineWater", qualifiedByName = "hexStringToInteger")
    @Mapping(source = "shadowMapAlpha", target = "shadowMapAlpha", qualifiedByName = "hexStringToInteger")
    MapDesignSchemeImplementation toDesignScheme(final MapDesignSchemeDto dto);

    @NonNull
    @Named("hexStringToInteger")
    static int hexStringToInteger(@NonNull final String hexString) {
        try {
            String preparedHexString = hexString;
            if (hexString.startsWith("0x")) {
                preparedHexString = hexString.replace("0x", "");
            }

            // Parse hex string to integer (via long to avoid overflow in parseInt method -> NumberFormatException)
            return (int) Long.parseLong(preparedHexString, 16);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing hex string to integer: " + hexString);
            return 0;
        }
    }

}