package com.recom.tacview.engine.renderables;

import lombok.NonNull;

public interface HasMergeable {

    @NonNull
    IsMergeable getMergeable();

}
