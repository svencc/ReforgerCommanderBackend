package com.recom.commander.enginemodule.entity.component;

import com.recom.tacview.engine.entitycomponentsystem.component.ComponentType;
import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.entitycomponentsystem.component.PhysicCoreComponent;
import com.recom.tacview.engine.input.command.IsCommand;
import com.recom.tacview.engine.input.command.keyboard.KeyboardCommand;
import com.recom.tacview.engine.input.command.mousebutton.MouseButton;
import com.recom.tacview.engine.input.command.mousebutton.MouseButtonCommand;
import com.recom.tacview.engine.input.command.mousebutton.MouseDragCommand;
import com.recom.tacview.engine.input.command.scroll.ScrollCommand;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RECOMMapInputComponent extends InputComponent {

    private static final float velocityX = 140f;
    private static final int DRAG_SPEED_COEFICIENT = 4;


    @Override
    public void handleInputCommand(@NonNull final IsCommand<?> inputCommand) {
        switch (inputCommand) {
            case KeyboardCommand keyboardCommand -> {
                this.getEntity().<PhysicCoreComponent>locateComponent(ComponentType.PhysicsCoreComponent).ifPresent(physicsCoreComponent -> {
                    if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.LEFT)) {
                        physicsCoreComponent.addVelocityXComponent(velocityX);
                    } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.RIGHT)) {
                        physicsCoreComponent.addVelocityXComponent(-velocityX);
                    } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.UP)) {
                        physicsCoreComponent.addVelocityYComponent(velocityX);
                    } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.DOWN)) {
                        physicsCoreComponent.addVelocityYComponent(-velocityX);
                    }
                });
            }
            case ScrollCommand scrollCommand -> {
                // log.info("ScrollCommand received: {} ({})", inputCommand.getClass().getSimpleName(), mapToScrollDirection(scrollCommand.getNanoTimedEvent().getEvent()));
                // code to zoom in/out
            }
            case MouseDragCommand mouseDragCommand -> {
                if (mouseDragCommand.getMouseButton().equals(MouseButton.MIDDLE)) {
                    this.getEntity().<PhysicCoreComponent>locateComponent(ComponentType.PhysicsCoreComponent).ifPresent(physicsCoreComponent -> {
                        if (mouseDragCommand.isInOriginPosition()) {
                            physicsCoreComponent.setVelocityXComponent(0);
                            physicsCoreComponent.setVelocityYComponent(0);
                        } else {
                            physicsCoreComponent.setVelocityXComponent(-1 * mouseDragCommand.getDistanceX() * DRAG_SPEED_COEFICIENT);
                            physicsCoreComponent.setVelocityYComponent(-1 * mouseDragCommand.getDistanceY() * DRAG_SPEED_COEFICIENT);
                        }
                    });
                } else {
                    log.info("MouseDragCommand received: {} ({})", inputCommand.getClass().getSimpleName(), mouseDragCommand.getMouseButton());
                }
            }
            default -> logInputCommand(inputCommand);
        }
    }

    private void logInputCommand(@NonNull final IsCommand<?> inputCommand) {
        switch (inputCommand) {
            case MouseButtonCommand mouseButtonCommand ->
                    log.info("MouseButtonCommand received: {} (doubleClick: {}) - timeBetweenDragStartAndDragStop {} / probableDraggingIntention {}", mouseButtonCommand.getMouseButton(), mouseButtonCommand.isDoubleClick(), mouseButtonCommand.getTimeBetweenDragStartAndDragStop(), mouseButtonCommand.isProbableDraggingIntention());
            case MouseDragCommand mouseDragCommand ->
                    log.info("MouseDragCommand received: {} ({})", mouseDragCommand.getMouseButton(), inputCommand.getNanoTimedEvent().getEvent().getEventType());
            case ScrollCommand scrollCommand ->
                    log.info("ScrollCommand received: {} ({})", inputCommand.getClass().getSimpleName(), mapToScrollDirection(scrollCommand.getNanoTimedEvent().getEvent()));
            case KeyboardCommand keyboardCommand ->
                    log.info("KeyboardCommand received: {} ({})", keyboardCommand.getNanoTimedEvent().getEvent().getEventType(), keyboardCommand.getNanoTimedEvent().getEvent().getCode());
            default ->
                    log.info("GenericInputCommand received: {} -> {}", inputCommand.getClass().getSimpleName(), inputCommand.getNanoTimedEvent().getEvent().getEventType());
        }
    }

    @NonNull
    private String mapToScrollDirection(@NonNull final ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaX() < 0) {
            return "RIGHT";
        } else if (scrollEvent.getDeltaX() > 0) {
            return "LEFT";
        } else if (scrollEvent.getDeltaY() > 0) {
            return "UP";
        } else if (scrollEvent.getDeltaY() < 0) {
            return "DOWN";
        } else {
            return "NONE";
        }
    }

}
