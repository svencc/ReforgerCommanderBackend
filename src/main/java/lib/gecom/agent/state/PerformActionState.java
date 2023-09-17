package lib.gecom.agent.state;

import lib.gecom.agent.GeAgent;
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
    public void update(@NonNull final GeAgent agent) {
        System.out.println("PerformActionState.update");

    }

}
