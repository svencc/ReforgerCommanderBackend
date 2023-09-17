package lib.clipboard.goap.agent;

import lib.clipboard.goap.action.GoapActionBase;
import lombok.NonNull;

import java.util.Queue;

public interface FSMPlanEventListenable {

    /**
     * Gets called when a RunActionState on the FSM throws an exception.
     *
     * @param actions the rest of the action Queue which failed to execute.
     */
    void onPlanFailed(@NonNull final Queue<GoapActionBase> actions);

    /**
     * Gets called when a RunActionState on the FSM returns true and therefore
     * signals that it is finished.
     */
    void onPlanFinished();

}
