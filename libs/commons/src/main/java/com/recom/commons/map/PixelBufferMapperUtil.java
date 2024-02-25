package com.recom.commons.map;

import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@UtilityClass
public class PixelBufferMapperUtil {

    @NonNull
    public ByteArrayOutputStream map(
            @NonNull DEMDescriptor demDescriptor,
            @NonNull int[] pixelBuffer
    ) throws IOException {
        return map(pixelBuffer, demDescriptor.getDemWidth(), demDescriptor.getDemHeight());
    }

    @NonNull
    public ByteArrayOutputStream map(
            @NonNull int[] pixelBuffer,
            final int width,
            final int height
    ) throws IOException {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelBuffer, 0, imagePixels, 0, pixelBuffer.length);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        return outputStream;
    }

}