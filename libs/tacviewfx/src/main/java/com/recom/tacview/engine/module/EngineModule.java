package com.recom.tacview.engine.module;

import com.recom.tacview.engine.Updatable;
import com.recom.tacview.engine.entitycomponentsystem.component.ComponentType;
import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.input.InputCommandQueue;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Getter
@RequiredArgsConstructor
public abstract class EngineModule implements Updatable {

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

    public void handleInput(@NonNull final InputCommandQueue inputCommandQueue) {
        final List<InputComponent> inputComponents = environment.getEntities().stream()
                .flatMap(entity -> entity.<InputComponent>locateComponents(ComponentType.InputComponent).stream())
                .toList();

        while (inputCommandQueue.hasCommand()) {
            for (final InputComponent inputComponent : inputComponents) {
                inputComponent.handleInput(inputCommandQueue.dequeueCommand());
            }
        }
    }

}
