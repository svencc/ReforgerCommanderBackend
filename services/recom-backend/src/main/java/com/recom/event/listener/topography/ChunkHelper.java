package com.recom.event.listener.topography;

import com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ChunkHelper {

    @NonNull
    public ChunkCoordinate extractChunkCoordinateFromSessionIdentifier(@NonNull final String sessionIdentifier) {
        return extractFromSessionIdentifier(sessionIdentifier).chunkCoordinate();
    }

    @NonNull
    public MapScanSessionIdentifierData extractFromSessionIdentifier(@NonNull final String sessionIdentifier) {
        final String[] additionalInformation = sessionIdentifier.split("#####");

        if (additionalInformation.length < 2) {
            throw new IllegalArgumentException("Session identifier does not contain the required information! (chunkCoordinate)");
        }

        final String mapName = additionalInformation[0];
        final String chunkXZCoordinate = additionalInformation[1];


        final String[] chunkCoordinates = chunkXZCoordinate.split(",");

        if (chunkCoordinates.length < 2) {
            throw new IllegalArgumentException("Session identifier does not contain the required information (2 x chunkCoordinates)!");
        }

        final String chunkCoordinatesX = chunkCoordinates[0];
        final String chunkCoordinatesZ = chunkCoordinates[1];

        final long chunkX = Integer.parseInt(chunkCoordinatesX);
        final long chunkZ = Integer.parseInt(chunkCoordinatesZ);

        final ChunkCoordinate chunkCoordinate = new ChunkCoordinate(chunkX, chunkZ);

        return new MapScanSessionIdentifierData(mapName, chunkCoordinate);
    }

    @NonNull
    public ChunkDimensions determineChunkDimensions(@NonNull final List<TransactionalMapTopographyEntityPackageDto> packages) throws IllegalArgumentException {
        final int dimensionX = packages.stream()
                .flatMap((entityPackage) -> entityPackage.getEntities().stream())
                .map(MapTopographyEntityDto::getCoordinates)
                .mapToInt((coordinates) -> coordinates.get(0).intValue())
                .max()
                .orElseThrow(() -> new IllegalArgumentException("No max X found!"));

        final int dimensionZ = packages.stream()
                .flatMap((entityPackage) -> entityPackage.getEntities().stream())
                .map(MapTopographyEntityDto::getCoordinates)
                .mapToInt((coordinates) -> coordinates.get(2).intValue())
                .max()
                .orElseThrow(() -> new IllegalArgumentException("No max Z found!"));

        return new ChunkDimensions(dimensionX, dimensionZ);
    }

}
