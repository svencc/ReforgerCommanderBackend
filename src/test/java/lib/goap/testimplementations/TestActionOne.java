package lib.goap.testimplementations;

import lib.goap.action.GoapActionBase;
import lib.goap.state.GoapState;
import lib.goap.target.GoapTargetable;
import lib.goap.unit.IGoapUnit;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
public class TestActionOne extends GoapActionBase {

    public TestActionOne(@NonNull final GoapTargetable target) {
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
