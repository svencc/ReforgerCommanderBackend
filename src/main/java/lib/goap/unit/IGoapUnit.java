package lib.goap.unit;

import lib.goap.GoapAction;
import lib.goap.GoapState;
import lib.goap.agent.ImportantUnitChangeEventListenable;
import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;

public interface IGoapUnit {

    /**
     * Gets called when a plan was found by the planner.
     *
     * @param actions the actions the unit hat to take in order to archive the goal.
     */
    void goapPlanFound(@NonNull final Queue<GoapAction> actions);

    /**
     * Gets called when a plan failed to execute.
     *
     * @param actions the remaining actions in the action Queue that failed.
     */
    void goapPlanFailed(@NonNull final Queue<GoapAction> actions);

    /**
     * Gets called when a plan was finished.
     */
    void goapPlanFinished();

    /**
     * General update from the Agent. Called in a loop until the program ends.
     */
    void update();

    /**
     * Function to move to a specific location. Gets called by the moveToState
     * when the unit has to move to a certain target.
     */
    void moveTo(@NonNull final Object target);

    HashSet<GoapState> getWorldState();

    List<GoapState> getGoalState();

    HashSet<GoapAction> getAvailableActions();

    void addImportantUnitGoalChangeListener(@NonNull final ImportantUnitChangeEventListenable listener);

    void removeImportantUnitGoalChangeListener(@NonNull final ImportantUnitChangeEventListenable listener);

}
