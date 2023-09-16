package lib.gecom.agent;

import lib.goap.action.GoapActionBase;
import lib.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
class MoveToState implements FSMStateful {

    @NonNull
    private final GeAgent agent;

    public MoveToState(@NonNull final GeAgent agent) {
        this.agent = agent;
    }

    @Override
    public boolean isStateStillPerforming(@NonNull IGoapUnit goapUnit) throws Exception {
        return false;
    }

}
