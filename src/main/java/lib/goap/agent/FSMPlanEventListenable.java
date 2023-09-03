package lib.goap.agent;

import lib.goap.GoapAction;
import lombok.NonNull;

import java.util.Queue;

public interface FSMPlanEventListenable {

    /**
     * Gets called when a RunActionState on the FSM throws an exception.
     *
     * @param actions the rest of the action Queue which failed to execute.
     */
    void onPlanFailed(@NonNull final Queue<GoapAction> actions);

    /**
     * Gets called when a RunActionState on the FSM returns true and therefore
     * signals that it is finished.
     */
    void onPlanFinished();

}
