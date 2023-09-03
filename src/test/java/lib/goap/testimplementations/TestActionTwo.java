package lib.goap.testimplementations;

import lib.goap.GoapState;
import lib.goap.IGoapUnit;

public class TestActionTwo extends TestActionOne {

    public TestActionTwo(Object target) {
        super(target);

        this.addPrecondition(new GoapState(0, "step", true));
        this.addEffect(new GoapState(0, "goal", true));
    }

    @Override
    protected float generateBaseCost(IGoapUnit goapUnit) {
        return 1;
    }

}
