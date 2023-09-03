package lib.goap.agent;

import lib.goap.GoapAction;

import java.util.Queue;

public interface PlanCreatedEventListenable {

    /**
     * This event is needed to push real action Queues on the FSM-Stack. Has to
     * pop the FSM-Stack, since the event fires before the return value of the
     * state gets checked.
     *
     * @param plan the plan that the Planner has created and is ready to be
     *             executed.
     */
    void onPlanCreated(Queue<GoapAction> plan);

}
