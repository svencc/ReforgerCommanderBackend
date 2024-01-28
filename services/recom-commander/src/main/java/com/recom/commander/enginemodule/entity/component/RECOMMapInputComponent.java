package com.recom.commander.enginemodule.entity.component;

import com.recom.tacview.engine.entitycomponentsystem.component.ComponentType;
import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.entitycomponentsystem.component.PhysicCoreComponent;
import com.recom.tacview.engine.entitycomponentsystem.component.RenderableComponent;
import com.recom.tacview.engine.input.command.IsCommand;
import com.recom.tacview.engine.input.command.keyboard.KeyboardCommand;
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

    @Override
    public void handleInputCommand(@NonNull final IsCommand<?> inputCommand) {
        switch (inputCommand) {
            case KeyboardCommand keyboardCommand -> {
                this.getEntity().<PhysicCoreComponent>locateComponent(ComponentType.PhysicsCoreComponent).ifPresent(physicsCoreComponent -> {
                    if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.LEFT)) {
                        physicsCoreComponent.setPositionX(physicsCoreComponent.getPositionX() + 50f);
                        this.getEntity().<RenderableComponent>locateComponent(ComponentType.RenderableComponent).ifPresent(RenderableComponent::propagateDirtyStateToParent);
                    } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.RIGHT)) {
                        physicsCoreComponent.setPositionX(physicsCoreComponent.getPositionX() - 50f);
                        this.getEntity().<RenderableComponent>locateComponent(ComponentType.RenderableComponent).ifPresent(RenderableComponent::propagateDirtyStateToParent);
                    } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.UP)) {
                        physicsCoreComponent.setPositionY(physicsCoreComponent.getPositionY() + 50f);
                        this.getEntity().<RenderableComponent>locateComponent(ComponentType.RenderableComponent).ifPresent(RenderableComponent::propagateDirtyStateToParent);
                    } else if (keyboardCommand.getNanoTimedEvent().getEvent().getCode().equals(KeyCode.DOWN)) {
                        physicsCoreComponent.setPositionY(physicsCoreComponent.getPositionY() - 50f);
                        this.getEntity().<RenderableComponent>locateComponent(ComponentType.RenderableComponent).ifPresent(RenderableComponent::propagateDirtyStateToParent);
                    }
                });
            }
            case ScrollCommand scrollCommand -> {
                // log.info("ScrollCommand received: {} ({})", inputCommand.getClass().getSimpleName(), mapToScrollDirection(scrollCommand.getNanoTimedEvent().getEvent()));
                // code to zoom in/out
            }
            default -> logInputCommand(inputCommand);
        }
    }

    private void logInputCommand(@NonNull final IsCommand<?> inputCommand) {
        switch (inputCommand) {
            case MouseButtonCommand mouseButtonCommand ->
                    log.info("MouseButtonCommand received: {} (doubleClick: {})", mouseButtonCommand.getMouseButton(), mouseButtonCommand.isDoubleClick());
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
