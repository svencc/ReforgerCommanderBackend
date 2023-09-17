package lib.gecom.agent.state;

import lib.gecom.agent.GeAgent;
import lib.goap.unit.IGoapUnit;
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
    public void update() {
super.getSubject().reportMyDeath();
    }

    @Override
    public boolean isStateStillPerforming(@NonNull IGoapUnit goapUnit) throws Exception {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
