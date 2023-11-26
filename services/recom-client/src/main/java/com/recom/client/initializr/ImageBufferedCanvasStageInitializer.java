package com.recom.client.initializr;

import com.recom.client.event.StageReadyEvent;
import com.recom.util.ColorUtil;
import javafx.animation.AnimationTimer;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.nio.IntBuffer;
import java.util.Random;


@Component
public class ImageBufferedCanvasStageInitializer implements ApplicationListener<StageReadyEvent> {

    private long currentTime = 0;
    private long lastTime = 0;
    private int fpsCounter = 0;
    private double delta = 0;
    private double fpsAverageLastSecond = 0;
    private AnimationTimer mainLoop = null;


    final int width = 1024;
    final int height = 768;

    private IntBuffer intBuffer = null;
    private PixelFormat<IntBuffer> pixelFormat = null;
    private PixelBuffer<IntBuffer> pixelBuffer = null;
    private Image img = null;
    private Canvas canvas = null;
    private GraphicsContext gc = null;

    private final Random random = new Random();


    @Override
    public void onApplicationEvent(@NonNull final StageReadyEvent __) {
        final Stage canvasStage = new Stage();
        canvasStage.show();
        populateImageBufferedCanvasStage(canvasStage);
        mainLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderStuff();
            }
        };

        mainLoop.start();
    }

    private void renderStuff() {
        currentTime = System.currentTimeMillis();
        /*
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                final int index = (x * width) + y;
                final int value = ColorUtil.ARGB(255, 255, 255, 255).intValue();
                intBuffer.put(index, value);
            }
        }
         */

        pixelBuffer.updateBuffer(__ -> null);
        gc.drawImage(img, 0, 0);


        fpsCounter++;
        if (currentTime - lastTime >= 1000) {
            delta = (currentTime - lastTime) / 1000.0;
            fpsAverageLastSecond = fpsCounter; // calculate average fps:
            fpsCounter = 0;
            lastTime = currentTime;
        }

        gc.fillText("FPS: " + fpsAverageLastSecond + " Delta: " + delta, 10, 10);
    }

    private void populateImageBufferedCanvasStage(@NonNull final Stage canvasStage) {
        final BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        intBuffer = IntBuffer.allocate(width * height);
        pixelFormat = PixelFormat.getIntArgbPreInstance();
        pixelBuffer = new PixelBuffer<>(width, height, intBuffer, pixelFormat);
        img = new WritableImage(pixelBuffer);
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                final int index = (x * width) + y;
//                final int value = ColorUtil.ARGB(random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256)).intValue();
                final int value = ColorUtil.ARGB(255, 255, 255, 255).intValue();
                intBuffer.put(index, value);
            }
        }

        root.setCenter(canvas);
        final Scene scene = new Scene(root, width, height);
        canvasStage.setTitle("BufferedCanvas Stage");
        canvasStage.setResizable(false);
        canvasStage.setScene(scene);
    }

}

//      https://www.youtube.com/watch?v=UDNrJAvKc0k https://youtu.be/UDNrJAvKc0k?si=GDu7RdGS3CeVM0rc&t=819
//      fetch data with new REST Client