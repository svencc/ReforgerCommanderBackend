package lib.gecom.agent.state;

import lib.gecom.agent.GeAgent;
import lib.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class PerformActionState extends FSMState {

    private final GeAgent agent;

    public PerformActionState(@NonNull final GeAgent agent) {
        super();
        this.agent = agent;
    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }

    @Override
    public void update() {

    }

    @Override
    public boolean isStateStillPerforming(@NonNull IGoapUnit goapUnit) throws Exception {
        return false;
    }

}
