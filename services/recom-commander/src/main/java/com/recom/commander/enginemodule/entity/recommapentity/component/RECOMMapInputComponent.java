package com.recom.commander.enginemodule.entity.recommapentity.component;

import com.recom.tacview.engine.ecs.component.ComponentType;
import com.recom.tacview.engine.ecs.component.InputComponent;
import com.recom.tacview.engine.ecs.component.PhysicCoreComponent;
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

import java.util.Optional;

@Slf4j
@Component
public class RECOMMapInputComponent extends InputComponent {

    private static final float MOVING_FORCE = 140f;
    private static final int DRAG_SPEED_COEFFICIENT = 4;

    @NonNull
    private Optional<RECOMMapComponent> maybeMapComponent = Optional.empty();


    @Override
    public void handleInputCommand(@NonNull final IsCommand<?> inputCommand) {
        switch (inputCommand) {
            case KeyboardCommand keyboardCommand -> handleKeyboardCommand(keyboardCommand);
            case ScrollCommand scrollCommand -> handleScrollCommand(scrollCommand);
            case MouseDragCommand mouseDragCommand -> handleMouseDragCommand(inputCommand, mouseDragCommand);
            default -> logInputCommand(inputCommand);
        }
    }

    private void handleKeyboardCommand(@NonNull final KeyboardCommand keyboardCommand) {
        if (this.getMaybeEntity().isPresent()) {
            this.getMaybeEntity().get().<PhysicCoreComponent>locateComponent(ComponentType.PhysicsCoreComponent).ifPresent(physicsCoreComponent -> {
                if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.LEFT)) {
                    physicsCoreComponent.addVelocityXComponent(MOVING_FORCE);
                } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.RIGHT)) {
                    physicsCoreComponent.addVelocityXComponent(-MOVING_FORCE);
                } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.UP)) {
                    physicsCoreComponent.addVelocityYComponent(MOVING_FORCE);
                } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.DOWN)) {
                    physicsCoreComponent.addVelocityYComponent(-MOVING_FORCE);
                } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.ADD) && keyboardCommand.isButtonReleased()) {
                    locateRecomMapComponent().ifPresent((mapComponent) -> mapComponent.zoomInByKey(keyboardCommand.getNanoTimedEvent(), physicsCoreComponent));
                } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.SUBTRACT) && keyboardCommand.isButtonReleased()) {
                    locateRecomMapComponent().ifPresent((mapComponent) -> mapComponent.zoomOutByKey(keyboardCommand.getNanoTimedEvent(), physicsCoreComponent));
                }
            });
        }
    }

    private void handleScrollCommand(@NonNull final ScrollCommand scrollCommand) {
        if (this.getMaybeEntity().isPresent()) {
            this.getMaybeEntity().get().<PhysicCoreComponent>locateComponent(ComponentType.PhysicsCoreComponent).ifPresent(physicsCoreComponent -> {
                if (scrollCommand.getNanoTimedEvent().getEvent().getDeltaY() > 0) {
                    locateRecomMapComponent().ifPresent((mapComponent) -> mapComponent.zoomByMouseIn(scrollCommand.getNanoTimedEvent(), physicsCoreComponent));
                } else if (scrollCommand.getNanoTimedEvent().getEvent().getDeltaY() < 0) {
                    locateRecomMapComponent().ifPresent((mapComponent) -> mapComponent.zoomOutByMouse(scrollCommand.getNanoTimedEvent(), physicsCoreComponent));
                }
            });
        }
    }

    private void handleMouseDragCommand(
            @NonNull final IsCommand<?> inputCommand,
            @NonNull final MouseDragCommand mouseDragCommand
    ) {
        if (this.getMaybeEntity().isPresent()) {
            if (mouseDragCommand.getMouseButton().equals(MouseButton.SECONDARY)) {
                this.getMaybeEntity().get().<PhysicCoreComponent>locateComponent(ComponentType.PhysicsCoreComponent).ifPresent(physicsCoreComponent -> {
                    if (mouseDragCommand.isInOriginPosition()) {
                        physicsCoreComponent.setVelocityXComponent(0);
                        physicsCoreComponent.setVelocityYComponent(0);
                    } else {
                        physicsCoreComponent.setVelocityXComponent(-1 * mouseDragCommand.getDistanceX() * DRAG_SPEED_COEFFICIENT);
                        physicsCoreComponent.setVelocityYComponent(-1 * mouseDragCommand.getDistanceY() * DRAG_SPEED_COEFFICIENT);
                    }
                });
            } else {
                log.info("MouseDragCommand received: {} ({})", inputCommand.getClass().getSimpleName(), mouseDragCommand.getMouseButton());
            }
        }
    }

    // @TODO; kann man das generalisieren?
    @NonNull
    private Optional<RECOMMapComponent> locateRecomMapComponent() {
        if (maybeMapComponent.isEmpty() && this.getMaybeEntity().isPresent()) {
            maybeMapComponent = this.getMaybeEntity().get().locateComponents(ComponentType.RenderableComponent).stream()
                    .filter(component -> component instanceof RECOMMapComponent)
                    .map(component -> (RECOMMapComponent) component)
                    .findFirst();
        }

        return maybeMapComponent;
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
