package lib.goap.fsm.states;

import lib.goap.GoapAction;
import lib.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class MoveToState implements FSMStateful {

    @NonNull
    private final GoapAction currentAction;

    /**
     * Move to the target of the currentAction until the unit is in range to perform the action itself.
     *
     * @param goapUnit the unit which action is being run.
     * @return false if the unit is in range of the action, true if the unit is still moving.
     */
    @Override
    public boolean isStateStillPerforming(@NonNull final IGoapUnit goapUnit) {
        boolean stillMoving = true;

        if ((currentAction.requiresInRange(goapUnit) && currentAction.isInRange(goapUnit)) || currentAction.getTarget() == null) {
            stillMoving = false;
        } else {
            goapUnit.moveTo(currentAction.getTarget());
        }

        return stillMoving;
    }

}
