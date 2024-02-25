package com.recom.service.map.topography;

import com.recom.commons.map.PixelBufferMapperUtil;
import com.recom.commons.map.rasterizer.ContourMapRasterizer;
import com.recom.commons.map.rasterizer.HeightMapRasterizer;
import com.recom.commons.map.rasterizer.ShadowedMapRasterizer;
import com.recom.commons.map.rasterizer.SlopeMapRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.ReforgerMapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapPNGGeneratorService {

    @NonNull
    private final HeightMapRasterizer heightMapRasterizer;
    @NonNull
    private final ShadowedMapRasterizer shadowedMapRasterizer;
    @NonNull
    private final ContourMapRasterizer contourMapRasterizer;
    @NonNull
    private final SlopeMapRasterizer slopeMapRasterizer;


    @NonNull
    public ByteArrayOutputStream generateHeightmapPNG(@NonNull final DEMDescriptor demDescriptor) throws IOException {
        try {
            final int[] pixelBuffer = heightMapRasterizer.rasterizeHeightMap(demDescriptor);

            return PixelBufferMapperUtil.map(demDescriptor, pixelBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public ByteArrayOutputStream generateShadeMapPNG(@NonNull final DEMDescriptor demDescriptor) {
        try {
            final int[] pixelBuffer = shadowedMapRasterizer.rasterizeShadowedMap(demDescriptor, new ReforgerMapDesignScheme());

            return PixelBufferMapperUtil.map(demDescriptor, pixelBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public ByteArrayOutputStream generateContourMapPNG(@NonNull final DEMDescriptor demDescriptor) {
        try {
            final int[] pixelBuffer = contourMapRasterizer.rasterizeContourMap(demDescriptor, new ReforgerMapDesignScheme());

            return PixelBufferMapperUtil.map(demDescriptor, pixelBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public ByteArrayOutputStream generateSlopeMapPNG(@NonNull final DEMDescriptor demDescriptor) {
        try {
            final int[] pixelBuffer = slopeMapRasterizer.rasterizeSlopeMap(demDescriptor, new ReforgerMapDesignScheme());

            return PixelBufferMapperUtil.map(demDescriptor, pixelBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}