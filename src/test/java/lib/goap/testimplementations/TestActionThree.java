package lib.goap.testimplementations;

import lib.goap.GoapState;
import lib.goap.unit.IGoapUnit;
import lombok.NonNull;

public class TestActionThree extends TestActionOne {
    public TestActionThree(@NonNull final Object target) {
        super(target);

        this.addPrecondition(new GoapState(0, "goal", false));
        this.addEffect(new GoapState(0, "goal", true));
    }

    @Override
    public float generateBaseCost(@NonNull final IGoapUnit goapUnit) {
        return 0;
    }
}
