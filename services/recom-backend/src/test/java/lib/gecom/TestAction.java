package lib.gecom;

import lib.gecom.action.GeAction;
import lombok.NonNull;

public class TestAction extends GeAction {

    public TestAction(@NonNull final String name) {
        super(name);
    }

    public TestAction(@NonNull final String name, @NonNull final Float cost) {
        super(name, cost);
    }

    @Override
    public boolean prePerform() {
        return false;
    }

    @Override
    public boolean postPerform() {
        return false;
    }

}