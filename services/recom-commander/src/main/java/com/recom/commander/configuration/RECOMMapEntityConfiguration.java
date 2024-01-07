package com.recom.commander.configuration;

import com.recom.commander.enginemodule.entity.RECOMMapEntity;
import com.recom.commander.enginemodule.entity.component.RECOMMapComponent;
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

        return recomMapEntity;
    }

}