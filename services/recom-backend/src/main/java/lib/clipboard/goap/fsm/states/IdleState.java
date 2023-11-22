package lib.clipboard.goap.fsm.states;

import lib.clipboard.goap.action.GoapActionBase;
import lib.clipboard.goap.agent.PlanCreatedEventListenable;
import lib.clipboard.goap.planner.GoapPlannerable;
import lib.clipboard.goap.unit.IGoapUnit;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class IdleState implements FSMStateful {

    @NonNull
    private final GoapPlannerable goapPlanner;
    @NonNull
    private final List<PlanCreatedEventListenable> planCreatedListeners = new ArrayList<>();

    public IdleState(@NonNull final GoapPlannerable goapPlanner) {
        this.goapPlanner = goapPlanner;
    }

    /**
     * Unit is in IDLE, so a new plan is being created.
     *
     * @param goapUnit the unit which action is being run.
     * @return always true.
     */
    @Override
    public boolean isStateStillPerforming(@NonNull final IGoapUnit goapUnit) {
        final Queue<GoapActionBase> plannedQueue = goapPlanner.planActions(goapUnit);

        if (plannedQueue != null) {
            dispatchNewPlanCreatedEvent(plannedQueue);
        }

        // Returning false would result in the RunActionState, which gets added
        // to the Stack by the Agent, to be removed.
        return true;
    }

    private synchronized void dispatchNewPlanCreatedEvent(@NonNull final Queue<GoapActionBase> plan) {
        planCreatedListeners.forEach(listener -> listener.onPlanCreated(plan));
    }

    public synchronized void addPlanCreatedListener(@NonNull final PlanCreatedEventListenable listener) {
        planCreatedListeners.add(listener);
    }

    synchronized void removePlanCreatedListener(@NonNull final PlanCreatedEventListenable listener) {
        planCreatedListeners.remove(listener);
    }

}
