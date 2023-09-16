package lib.gecom.agent;

import lib.goap.agent.PlanCreatedEventListenable;
import lib.goap.unit.IGoapUnit;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

class IdleState implements FSMStateful, FSMStartableState {

    @NonNull
    private final GeAgent agent;

    public IdleState(@NonNull final GeAgent agent) {
        this.agent = agent;
    }

    @Override
    public boolean isStateStillPerforming(@NonNull IGoapUnit goapUnit) throws Exception {
        return false;
    }

    @Override
    public void start() {

    }

}
