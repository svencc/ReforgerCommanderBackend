package com.recom.tacview.engine.entity.interfaces;

import lombok.NonNull;

import java.util.List;

public interface HasComponents {

    @NonNull List<IsComponent> getComponents();

}
