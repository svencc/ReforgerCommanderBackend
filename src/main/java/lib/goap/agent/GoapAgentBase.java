package lib.goap.agent;

import lib.goap.action.GoapActionBase;
import lib.goap.state.GoapState;
import lib.goap.fsm.FSM;
import lib.goap.fsm.states.IdleState;
import lib.goap.fsm.states.PerformActionState;
import lib.goap.planner.GoapPlannerable;
import lib.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;

import java.util.Queue;

public abstract class GoapAgentBase implements ImportantUnitChangeEventListenable, PlanCreatedEventListenable, FSMPlanEventListenable {

    @NonNull
    private final FSM fsm = new FSM();
    @NonNull
    private final IdleState idleState;
    @Getter
    @NonNull
    private final IGoapUnit assignedGoapUnit;

    public GoapAgentBase(@NonNull final IGoapUnit assignedUnit) {
        this.assignedGoapUnit = assignedUnit;

        // Initialize the FSM and the IdleState.
        idleState = new IdleState(generatePlannerObject());
        idleState.addPlanCreatedListener(this);
        fsm.pushStack(idleState);

        assignedGoapUnit.addImportantUnitGoalChangeListener(this);
        fsm.addPlanEventListener(this);
    }

    protected abstract GoapPlannerable generatePlannerObject();

    public void update() {
        assignedGoapUnit.update();
        fsm.update(assignedGoapUnit);
    }

    @Override
    public void onPlanCreated(@NonNull final Queue<GoapActionBase> plan) {
        assignedGoapUnit.goapPlanFound(plan);
        fsm.popStack();
        fsm.pushStack(new PerformActionState(fsm, plan));
    }

    @Override
    public void onImportantUnitGoalChange(@NonNull final GoapState newGoalState) {
        newGoalState.setImportance(Integer.MAX_VALUE);
        fsm.pushStack(idleState);
    }

    @Override
    public void onImportantUnitStackResetChange() {
        assignedGoapUnit.getAvailableActions().forEach(GoapActionBase::reset);
        fsm.clearStack();
        fsm.pushStack(idleState);
    }

    @Override
    public void onPlanFailed(@NonNull final Queue<GoapActionBase> actions) {
        assignedGoapUnit.goapPlanFailed(actions);
    }

    @Override
    public void onPlanFinished() {
        assignedGoapUnit.goapPlanFinished();
    }

}
