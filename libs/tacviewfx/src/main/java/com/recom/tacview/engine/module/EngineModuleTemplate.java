package com.recom.tacview.engine.module;

import com.recom.tacview.engine.entity.environment.EnvironmentBase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class EngineModuleTemplate {

    @NonNull
    private final EnvironmentBase environment;


    public void run() {
        init();
        startEngineModule();
    }

    public abstract void init();

    public abstract void startEngineModule();


    public void update(final long elapsedNanoTime) {
        /*
            @TODO !
            When this realization percolated through the game industry, the solution that emerged was the Component pattern.
            Using that, update() would be on the entity’s components and not on Entity itself.
            That lets you avoid creating complicated class hierarchies of entities to define and reuse behavior. Instead, you just mix and match components.
         */
        environment.update(elapsedNanoTime);
    }


    public void handleInput() {
        // @TODO: Handle input
    }

}
