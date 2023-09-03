package lib.goap.testimplementations;

import lib.goap.GoapAction;
import lib.goap.GoapState;
import lib.goap.unit.IGoapUnit;
import lombok.NonNull;

public class TestActionOne extends GoapAction {

    public TestActionOne(Object target) {
        super(target);
        addPrecondition(new GoapState(0, "goal", false));
        addEffect(new GoapState(0, "step", true));
    }

    @Override
    public boolean isDone(@NonNull final IGoapUnit goapUnit) {
        return true;
    }

    @Override
    public boolean performAction(@NonNull final IGoapUnit goapUnit) {
        return true;
    }

    @Override
    public float generateBaseCost(@NonNull final IGoapUnit goapUnit) {
        return 0;
    }

    @Override
    public float generateCostRelativeToTarget(@NonNull final IGoapUnit goapUnit) {
        return 0;
    }

    @Override
    public boolean checkProceduralPrecondition(@NonNull final IGoapUnit goapUnit) {
        return true;
    }

    @Override
    public boolean requiresInRange(@NonNull final IGoapUnit goapUnit) {
        return false;
    }

    @Override
    public boolean isInRange(@NonNull final IGoapUnit goapUnit) {
        return false;
    }

    @Override
    public void reset() {

    }

}
