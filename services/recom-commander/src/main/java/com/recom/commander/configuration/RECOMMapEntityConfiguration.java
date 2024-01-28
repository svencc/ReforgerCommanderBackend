package com.recom.commander.configuration;

import com.recom.commander.enginemodule.entity.RECOMMapEntity;
import com.recom.commander.enginemodule.entity.component.RECOMMapComponent;
import com.recom.commander.enginemodule.entity.component.RECOMMapInputComponent;
import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.entitycomponentsystem.component.PhysicCoreComponent;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RECOMMapEntityConfiguration {

    @Bean()
    public RECOMMapEntity getRECOMMapEntity(
            @NonNull final RECOMMapComponent recomMapComponent
    ) {
        final RECOMMapEntity recomMapEntity = new RECOMMapEntity();
        recomMapEntity.addComponent(recomMapComponent);
        recomMapEntity.addComponent(new RECOMMapInputComponent());
        recomMapEntity.addComponent(new PhysicCoreComponent());

        return recomMapEntity;
    }

}