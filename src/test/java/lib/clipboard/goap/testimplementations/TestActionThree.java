package lib.clipboard.goap.testimplementations;

import lib.clipboard.goap.state.GoapState;
import lib.clipboard.goap.target.GoapTargetable;
import lib.clipboard.goap.unit.IGoapUnit;
import lombok.NonNull;

public class TestActionThree extends TestActionOne {
    public TestActionThree(@NonNull final GoapTargetable target) {
        super(target);

        this.addPrecondition(new GoapState(0, "goal", false));
        this.addEffect(new GoapState(0, "goal", true));
    }

    @Override
    public float generateBaseCost(@NonNull final IGoapUnit goapUnit) {
        return 0;
    }
}
