package lib.gecom.agent.state;

import lib.gecom.agent.GeAgent;
import lib.gecom.agent.event.RequestTransitionChangeEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class IdleState extends FSMState implements Startable, Stoppable {

    @Getter
    private Instant startIdling;

    @Getter
    private Instant lastUpdate;

    @Override
    public void enter() {
        startIdling = Instant.now();
    }

    @Override
    public void exit() {

    }

    @Override
    public void execute(@NonNull final GeAgent agent) {
        lastUpdate = Instant.now();
        System.out.println("IdleState.update:");
        System.out.printf("... idling since %s ms\n", Duration.between(startIdling, lastUpdate).toMillis());
        if (!agent.getCurrentActionStack().isEmpty()) {
            if (isTransitionToPerformable(agent.getFsm().provideState(FSMStates.PERFORMABLE))) {
                System.out.println("... transitioning to PerformActionState");
                getSubject().notifyObserversWith(new RequestTransitionChangeEvent(FSMStates.PERFORMABLE, true));
//                agent.getFsm().transitionToState(FSMStates.PERFORMABLE);
//                agent.getFsm().provideState(FSMStates.PERFORMABLE).transitionFrom(this);
            }
        }
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
