package com.recom.goapcom.action;

import lombok.NonNull;

public class GenericAction extends GeAction {


    public GenericAction(@NonNull final String name) {
        super(name);
    }

    public GenericAction(
            @NonNull final String name,
            @NonNull final Float cost
    ) {
        super(name, cost);
    }

    public boolean prePerform() {
        return true;
    }

    public boolean postPerform() {
        return true;
    }

}
