package com.recom.tacview.engine.module;

import com.recom.tacview.engine.entity.World;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class EngineModuleTemplate {

    @NonNull
    private final World world;


    public void run() {
        init();
        startEngineModule();
    }

    public abstract void init();

    public abstract void startEngineModule();


    public void update() {
        /*
            @TODO !
            When this realization percolated through the game industry, the solution that emerged was the Component pattern.
            Using that, update() would be on the entity’s components and not on Entity itself.
            That lets you avoid creating complicated class hierarchies of entities to define and reuse behavior. Instead, you just mix and match components.
         */
        world.update();
    }


    public void handleInput() {
        // @TODO: Handle input
    }

}
