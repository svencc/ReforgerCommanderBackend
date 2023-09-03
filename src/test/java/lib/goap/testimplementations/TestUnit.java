package lib.goap.testimplementations;

import lib.goap.GoapAction;
import lib.goap.GoapState;
import lib.goap.GoapUnit;

import java.util.Queue;

public class TestUnit extends GoapUnit {
    public TestActionOne tOne = new TestActionOne(1);
    public TestActionTwo tTwo = new TestActionTwo(1);
    public TestActionThree tThree = new TestActionThree(1);
    public GoapState goalS = new GoapState(1, "goal", true);
    public GoapState worldS = new GoapState(0, "goal", false);

    public TestUnit() {

    }

    @Override
    public void goapPlanFound(Queue<GoapAction> actions) {

    }

    @Override
    public void goapPlanFailed(Queue<GoapAction> actions) {

    }

    @Override
    public void goapPlanFinished() {

    }

    @Override
    public void update() {

    }

    @Override
    public boolean moveTo(Object target) {
        return false;
    }

    // ------------------------------ Custom test functions

    public void addGS(GoapState goal) {
        this.addGoalState(goal);
    }

    public void addWS(GoapState worldState) {
        this.addWorldState(worldState);
    }

    public void addAA(GoapAction action) {
        this.addAvailableAction(action);
    }
}
