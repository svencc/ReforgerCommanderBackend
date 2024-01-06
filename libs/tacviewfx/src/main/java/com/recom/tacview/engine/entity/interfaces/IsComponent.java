package com.recom.tacview.engine.entity.interfaces;

import com.recom.tacview.engine.Updatable;
import com.recom.tacview.engine.entity.component.ComponentTemplate;

public interface IsComponent extends MaybeHasEntity, HasComponentType, Updatable {

    Class<? extends ComponentTemplate> getComponentClass();

    int getComponentProcessingOrder();

}
