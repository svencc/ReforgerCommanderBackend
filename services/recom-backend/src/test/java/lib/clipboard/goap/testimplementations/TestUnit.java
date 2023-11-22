package lib.clipboard.goap.testimplementations;

import lib.clipboard.goap.action.GoapActionBase;
import lib.clipboard.goap.state.GoapState;
import lib.clipboard.goap.state.WorldAspect;
import lib.clipboard.goap.target.NullTarget;
import lib.clipboard.goap.unit.GoapUnitBase;
import lombok.NonNull;

import java.util.Queue;

public class TestUnit extends GoapUnitBase {

    public TestActionOne tOne = new TestActionOne(new NullTarget());
    public TestActionTwo tTwo = new TestActionTwo(new NullTarget());
    public TestActionThree tThree = new TestActionThree(new NullTarget());
    public GoapState goalS = new GoapState(1, "goal", true);
    public WorldAspect worldS = (WorldAspect) new GoapState("goal", false);

    @Override
    public void goapPlanFound(@NonNull final Queue<GoapActionBase> actions) {

    }

    @Override
    public void goapPlanFailed(@NonNull final Queue<GoapActionBase> actions) {

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

    public void addWS(@NonNull final WorldAspect worldAspect) {
        this.addWorldStateAspect(worldAspect);
    }

    public void addAA(@NonNull final GoapActionBase action) {
        this.addAvailableAction(action);
    }

}
