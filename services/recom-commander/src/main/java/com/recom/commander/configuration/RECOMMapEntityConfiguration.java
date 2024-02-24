package com.recom.commander.configuration;

import com.recom.commander.enginemodule.entity.recommapentity.RECOMMapEntity;
import com.recom.commander.enginemodule.entity.recommapentity.component.RECOMMapComponent;
import com.recom.commander.enginemodule.entity.recommapentity.component.RECOMMapInputComponent;
import com.recom.commander.enginemodule.entity.recommapentity.component.RECOMUICommands;
import com.recom.tacview.engine.ecs.component.PhysicComponent;
import com.recom.tacview.engine.ecs.component.PhysicCoreComponent;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RECOMMapEntityConfiguration {

    @Bean()
    public RECOMMapEntity getRECOMMapEntity(@NonNull final RECOMMapComponent recomMapComponent) {
        final RECOMMapEntity recomMapEntity = new RECOMMapEntity();

        final PhysicCoreComponent component = new PhysicCoreComponent();
        component.setFrictionForce(100);

        recomMapEntity.addComponent(recomMapComponent);
        recomMapEntity.addComponent(new RECOMMapInputComponent());
        recomMapEntity.addComponent(component);
        recomMapEntity.addComponent(new PhysicComponent());

        return recomMapEntity;
    }

}