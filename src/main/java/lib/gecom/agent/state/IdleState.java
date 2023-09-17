package lib.gecom.agent.state;

import lib.gecom.agent.GeAgent;
import lombok.NonNull;

public class IdleState extends FSMState implements Startable, Stoppable {

    @NonNull
    private final GeAgent agent;

    public IdleState(@NonNull final GeAgent agent) {
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
        System.out.println("IdleState.update");
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
