package com.recom.commander.enginemodule;

import com.recom.commander.enginemodule.entity.RECOMMapEntity;
import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.module.EngineModule;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TacViewerEngineModule extends EngineModule {

    @NonNull
    private final RECOMMapEntity mapEntity;


    public TacViewerEngineModule(
            @NonNull final Environment environment,
            @NonNull final RECOMMapEntity mapEntity
    ) {
        super(environment);
        this.mapEntity = mapEntity;
    }

    @Override
    public void init() {
        getEnvironment().registerNewEntity(mapEntity);
    }

    @Override
    public void startEngineModule() {

    }

}
