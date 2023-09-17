package lib.gecom.agent.state;

import lib.gecom.action.GeAction;
import lib.gecom.agent.GeAgent;
import lib.gecom.stuff.GeNullTarget;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Getter
public class PerformActionState extends FSMState implements Performable {


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
            if (agent.getCurrentActionStack().peek().getTarget() instanceof GeNullTarget) {
                // dont move; execute action
                System.out.println("... executing action without moving");
                agent.setCurrentAction(agent.getCurrentActionStack().pop());
                final GeAction currentAction = agent.getCurrentAction();
                if (currentAction.isAchievable()) {
                    if (currentAction.arePreconditionsMet(agent.getAgentsBelieves())) {
                        currentAction.setActionRunning(true);

                        if (currentAction.getDuration() == 0.0f) {
                            currentAction.prePerform();
                            currentAction.getEffects().forEach((key, value) -> agent.getAgentsBelieves().put(key, value)); // apply effects
                            currentAction.postPerform();
                        }
                    }
                }
            } else {
                // check agent position and target position: calculate distance
                // then move ...
                // ... or execute action
            }
        }
    }

    private void profile() {
        lastExecution = Instant.now();
        System.out.printf("%s.execute\n", getClass().getSimpleName());
        System.out.printf("... performing since %s ms\n", Duration.between(stateEntered, lastExecution).toMillis());
    }

}
