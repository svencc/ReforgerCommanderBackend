package lib.goap.testimplementations;

import lib.goap.GoapState;
import lib.goap.IGoapUnit;

public class TestActionThree extends TestActionOne {
    public TestActionThree(Object target) {
        super(target);

        this.addPrecondition(new GoapState(0, "goal", false));
        this.addEffect(new GoapState(0, "goal", true));
    }

    @Override
    protected float generateBaseCost(IGoapUnit goapUnit) {
        return 0;
    }
}
