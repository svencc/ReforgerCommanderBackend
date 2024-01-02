package com.recom.commander.enginemodule.entity;

import com.recom.commander.enginemodule.entity.component.RECOMMapComponent;
import com.recom.tacview.engine.entity.environment.EnvironmentBase;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class RECOMEnvironment extends EnvironmentBase {

    @NonNull
    private final RECOMEntity recomMapEntity;

    RECOMEnvironment(@NonNull final RECOMMapComponent mapComponent) {
        recomMapEntity = new RECOMEntity();
        recomMapEntity.addComponent(mapComponent);

        getEntities().add(recomMapEntity);
    }

}
