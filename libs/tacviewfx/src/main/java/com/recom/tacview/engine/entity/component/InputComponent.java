package com.recom.tacview.engine.entity.component;

public abstract class InputComponent extends ComponentTemplate {

    @Override
    public ComponentType componentType() {
        return ComponentType.INPUT;
    };

    public InputComponent() {
        super(ComponentType.InputComponent);
    }

}
