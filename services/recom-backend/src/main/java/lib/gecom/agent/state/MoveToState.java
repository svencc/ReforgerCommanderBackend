package lib.gecom.agent.state;

import lib.gecom.agent.GeAgent;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class MoveToState extends FSMState implements Movable {

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }

    @Override
    public void execute(@NonNull final GeAgent agent) {
        System.out.println("MoveToState.update");
    }

}
