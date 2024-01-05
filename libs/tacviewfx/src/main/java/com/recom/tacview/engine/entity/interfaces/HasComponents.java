package com.recom.tacview.engine.entity.interfaces;

import com.recom.tacview.engine.entity.component.ComponentTemplate;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface HasComponents {

    @NonNull List<IsComponent> getComponents();

}
