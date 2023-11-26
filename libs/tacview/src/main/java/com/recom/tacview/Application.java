//package com.recom.tacview;
//
//import cc.sven.hexwarriorproton.minefront.engine.GameLoop;
//import cc.sven.hexwarriorproton.minefront.property.MetaProperties;
//import cc.sven.hexwarriorproton.minefront.property.RendererProperties;
//import cc.sven.hexwarriorproton.minefront.property.TickProperties;
//import cc.sven.hexwarriorproton.minefront.strategy.SetJFrameTitleStrategy;
//import lombok.NonNull;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.Bean;
//
//import java.awt.*;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//
//@EnableConfigurationProperties({
//        MetaProperties.class,
//        RendererProperties.class,
//        TickProperties.class
//})
//@SpringBootApplication
//public class Application {
//
//    public static void main(String[] args) {
//        final ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Application.class)
//                .headless(false)
//                .run(args);
//        ctx.start();
//    }
//
//    @Bean
//    public CommandLineRunner provideCommandLineRunnerBean(
//            @NonNull final GameLoop gameLoop,
//            @NonNull final MetaProperties metaProperties,
//            @NonNull final RendererProperties rendererProperties
//    ) {
//        return args -> {
//            final Frame frame = new Frame();
//            frame.setTitle(metaProperties.getName());
//            frame.addWindowListener(new WindowAdapter() {
//                @Override
//                public void windowClosing(WindowEvent we) {
//                    gameLoop.stop();
//                }
//            });
//            frame.setLocationByPlatform(true);
//            frame.setSize(rendererProperties.getScaledWidth(), rendererProperties.getScaledHeight());
//            frame.add(gameLoop);
//            frame.setVisible(true);
//
//            final int verticalInset = frame.getInsets().top + frame.getInsets().bottom;
//            final int horizontalInset = frame.getInsets().left + frame.getInsets().right;
//            frame.setSize(rendererProperties.getScaledWidth() + horizontalInset, rendererProperties.getScaledHeight() + verticalInset);
//            frame.setResizable(false);
//            frame.pack();
//
//            gameLoop.start(SetJFrameTitleStrategy.builder().frame(frame).build());
//        };
//    }
//
//}