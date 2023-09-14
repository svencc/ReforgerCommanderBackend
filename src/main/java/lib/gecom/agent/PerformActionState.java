package lib.gecom.agent;

import lib.goap.UnperformableActionException;
import lib.goap.action.GoapActionBase;
import lib.goap.fsm.FSM;
import lib.goap.fsm.states.FSMStateful;
import lib.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;

import java.util.Queue;

class PerformActionState implements FSMStateful {

    private final GeAgent agent;

    public PerformActionState(@NonNull final GeAgent agent) {
        this.agent = agent;
    }

    @Override
    public boolean isStateStillPerforming(@NonNull IGoapUnit goapUnit) throws Exception {
        return false;
    }

}
