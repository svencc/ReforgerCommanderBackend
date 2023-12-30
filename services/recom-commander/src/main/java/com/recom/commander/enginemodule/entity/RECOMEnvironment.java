package com.recom.commander.enginemodule.entity;

import com.recom.commander.enginemodule.entity.component.RECOMMapComponent;
import com.recom.tacview.engine.entity.Environment;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RECOMEnvironment extends Environment {

    @NonNull
    private final RECOMEntity recomEntity;
    @NonNull
    private final RECOMMapComponent mapComponent;

    @PostConstruct
    public void init() {
        recomEntity.addComponent(mapComponent);
        getEntities().add(recomEntity);
    }

}
