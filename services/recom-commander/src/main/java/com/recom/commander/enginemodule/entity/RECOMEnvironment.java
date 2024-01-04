package com.recom.commander.enginemodule.entity;

import com.recom.commander.enginemodule.entity.component.RECOMMapComponent;
import com.recom.tacview.engine.entity.component.MergeableLayerComponent;
import com.recom.tacview.engine.entity.environment.EnvironmentBase;
import com.recom.tacview.engine.renderables.mergeable.SolidColorMergeable;
import com.sun.scenario.effect.Merge;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class RECOMEnvironment extends EnvironmentBase {

    RECOMEnvironment() {
        super();
    }

}
