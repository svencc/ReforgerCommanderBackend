package lib.goap.testimplementations;

import lib.goap.state.GoapState;
import lib.goap.target.GoapTargetable;
import lib.goap.unit.IGoapUnit;
import lombok.NonNull;

public class TestActionTwo extends TestActionOne {

    public TestActionTwo(GoapTargetable target) {
        super(target);

        this.addPrecondition(new GoapState(0, "step", true));
        this.addEffect(new GoapState(0, "goal", true));
    }

    @Override
    public float generateBaseCost(@NonNull final IGoapUnit goapUnit) {
        return 1;
    }

}