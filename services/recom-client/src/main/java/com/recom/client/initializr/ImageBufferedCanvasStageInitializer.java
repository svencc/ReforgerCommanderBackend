package com.recom.client.initializr;

import com.recom.client.event.StageReadyEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.nio.IntBuffer;


@Component
public class ImageBufferedCanvasStageInitializer implements ApplicationListener<StageReadyEvent> {

    @Override
    public void onApplicationEvent(@NonNull final StageReadyEvent __) {
        final Stage canvasStage = new Stage();
        populateImageBufferedCanvasStage(canvasStage);
        canvasStage.show();
    }

    @NonNull
    private void populateImageBufferedCanvasStage(@NonNull final Stage canvasStage) {
        final BorderPane root = new BorderPane();


        // -> so schreiben wir in den buffer
        // Creating a PixelBuffer using INT_ARGB_PRE pixel format.
        final int width = 300;
        final int height = 240;
        final IntBuffer intBuffer = IntBuffer.allocate(width * height);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                final int index = (x * width) + y;
                final int value = ColorUtil.ARGB(255, 230, 50, 50).intValue();
                intBuffer.put(index, value);

            }
        }

//        https://www.youtube.com/watch?v=UDNrJAvKc0k
//      fetch data with new REST Client


        final PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
        final PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer<>(width, height, intBuffer, pixelFormat);
        final Image img = new WritableImage(pixelBuffer);


        final Canvas canvas = new Canvas(width, height);
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(img, 0, 0); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< <<< das muss ich aufrufen wenn das nächste image zu Verfügung steht
        root.setCenter(canvas);

        final Scene scene = new Scene(root, 400, 400, Color.rgb(230, 185, 120));
        canvasStage.setTitle("Canvas Stage");
        canvasStage.setScene(scene);
    }

}
