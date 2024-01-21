package com.recom.tacview.engine.module;

import com.recom.tacview.engine.IsUpdatable;
import com.recom.tacview.engine.entitycomponentsystem.component.ComponentType;
import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.input.command.IsInputCommand;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Getter
@RequiredArgsConstructor
public abstract class EngineModule implements IsUpdatable {

    @NonNull
    private final Environment environment;


    public void run() {
        init();
        startEngineModule();
    }

    public abstract void init();

    public abstract void startEngineModule();

    public void update(final long elapsedNanoTime) {
        environment.update(elapsedNanoTime);
    }

    public void handleInputCommands(@NonNull final List<IsInputCommand> inputEventQueue) {
        final List<InputComponent> inputComponents = environment.getEntities().stream()
                .flatMap(entity -> entity.<InputComponent>locateComponents(ComponentType.InputComponent).stream())
                .toList();

        for (final IsInputCommand inputCommand : inputEventQueue) {
            for (final InputComponent inputComponent : inputComponents) {
                inputComponent.handleInputCommand(inputCommand);
            }
        }
    }

}
