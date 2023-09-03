package lib.goap.fsm.states;

import lib.goap.GoapAction;
import lib.goap.UnperformableActionException;
import lib.goap.fsm.FSM;
import lib.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;

import java.util.Queue;

public class PerformActionState implements FSMStateful {

    @NonNull
    private final FSM fsm;
    @Getter
    @NonNull
    private final Queue<GoapAction> currentActions;

    public PerformActionState(
            @NonNull final FSM fsm,
            @NonNull final Queue<GoapAction> currentActions
    ) {
        this.fsm = fsm;
        this.currentActions = currentActions;
    }


    /**
     * Cycle through all actions until an invalid one or the end of the Queue is
     * reached. A false return type here causes the FSM to pop the state from
     * its stack.
     */
    /**
     * Returning false results in the removing of the implementers instance on
     * @param goapUnit the unit which action is being run.
     * @return
     * @throws Exception
     */
    @Override
    public boolean isStateStillPerforming(@NonNull final IGoapUnit goapUnit) throws Exception {
        boolean workingOnQueue = false;
        boolean missingAction = true;

        try {
            while (missingAction) {
                if (!currentActions.isEmpty() && currentActions.peek().isDone(goapUnit)) {
                    currentActions.poll().reset();
                } else {
                    missingAction = false;
                }
            }

            if (!currentActions.isEmpty()) {
                final GoapAction currentAction = currentActions.peek();

                // No Exception since handling this is user specific.
                if (currentAction.getTarget() == null) {
                    System.out.println("Target is null! " + currentAction.getClass().getSimpleName());
                }

                if (currentAction.requiresInRange(goapUnit) && !currentAction.isInRange(goapUnit)) {
                    fsm.pushStack(new MoveToState(currentAction));
                } else if (currentAction.checkProceduralPrecondition(goapUnit)
                        && !currentAction.performAction(goapUnit)) {
                    throw new UnperformableActionException(currentAction.getClass().getSimpleName());
                }

                workingOnQueue = true;
            }
        } catch (UnperformableActionException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();

            // throw new Exception();
        }
        return workingOnQueue;
    }

}
