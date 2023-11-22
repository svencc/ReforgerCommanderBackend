package lib.clipboard.goap.unit;

import lib.clipboard.goap.action.GoapActionBase;
import lib.clipboard.goap.state.GoapState;
import lib.clipboard.goap.agent.ImportantUnitChangeEventListenable;
import lib.clipboard.goap.state.WorldAspect;
import lombok.NonNull;

import java.util.List;
import java.util.Queue;
import java.util.Set;

public interface IGoapUnit {

    /**
     * Gets called when a plan was found by the planner.
     *
     * @param actions the actions the unit hat to take in order to archive the goal.
     */
    void goapPlanFound(@NonNull final Queue<GoapActionBase> actions);

    /**
     * Gets called when a plan failed to execute.
     *
     * @param actions the remaining actions in the action Queue that failed.
     */
    void goapPlanFailed(@NonNull final Queue<GoapActionBase> actions);

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

    @NonNull
    Set<WorldAspect> getWorldState();

    @NonNull
    List<GoapState> getGoalState();

    @NonNull
    Set<GoapActionBase> getAvailableActions();

    void addImportantUnitGoalChangeListener(@NonNull final ImportantUnitChangeEventListenable listener);

    void removeImportantUnitGoalChangeListener(@NonNull final ImportantUnitChangeEventListenable listener);

}
