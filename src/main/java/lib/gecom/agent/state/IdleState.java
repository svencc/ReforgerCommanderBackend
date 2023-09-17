package lib.gecom.agent.state;

import lib.gecom.agent.GeAgent;
import lib.gecom.agent.event.RequestTransitionChangeEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Getter
public class IdleState extends FSMState implements Startable, Stoppable {

    private Instant stateEntered;

    private Instant lastExecution;

    @Override
    public void enter() {
        stateEntered = Instant.now();
    }

    @Override
    public void exit() {
        stateEntered = null;
        lastExecution = null;
    }

    @Override
    public void execute(@NonNull final GeAgent agent) {
        profile();
        if (!agent.getCurrentActionStack().isEmpty()) {
            if (isTransitionToPerformable(agent.getFsm().provideState(FSMStates.PERFORMABLE))) {
                System.out.println("... transitioning to PerformActionState");
                getSubject().notifyObserversWith(new RequestTransitionChangeEvent(FSMStates.PERFORMABLE, true));
            }
        }
    }

    private void profile() {
        lastExecution = Instant.now();
        System.out.printf("%s.execute\n", getClass().getSimpleName());
        System.out.printf("... idling since %s ms\n", Duration.between(stateEntered, lastExecution).toMillis());
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
