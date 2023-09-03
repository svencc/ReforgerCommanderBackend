package lib.goap.testimplementations;

import lib.goap.GoapAction;
import lib.goap.GoapState;
import lib.goap.unit.GoapUnitBase;
import lombok.NonNull;

import java.util.Queue;

public class TestUnit extends GoapUnitBase {

    public TestActionOne tOne = new TestActionOne(1);
    public TestActionTwo tTwo = new TestActionTwo(1);
    public TestActionThree tThree = new TestActionThree(1);
    public GoapState goalS = new GoapState(1, "goal", true);
    public GoapState worldS = new GoapState(0, "goal", false);

    @Override
    public void goapPlanFound(@NonNull final Queue<GoapAction> actions) {

    }

    @Override
    public void goapPlanFailed(@NonNull final Queue<GoapAction> actions) {

    }

    @Override
    public void goapPlanFinished() {

    }

    @Override
    public void update() {

    }

    @Override
    public void moveTo(@NonNull final Object target) {
    }

    // ------------------------------ Custom test functions

    public void addGS(@NonNull final GoapState goal) {
        this.addGoalState(goal);
    }

    public void addWS(@NonNull final GoapState worldState) {
        this.addWorldState(worldState);
    }

    public void addAA(@NonNull final GoapAction action) {
        this.addAvailableAction(action);
    }

}
