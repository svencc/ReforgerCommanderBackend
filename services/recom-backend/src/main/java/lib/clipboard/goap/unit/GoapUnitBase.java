package lib.clipboard.goap.unit;

import lib.clipboard.goap.action.GoapActionBase;
import lib.clipboard.goap.state.GoapState;
import lib.clipboard.goap.agent.ImportantUnitChangeEventListenable;
import lib.clipboard.goap.state.WorldAspect;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.*;

@NoArgsConstructor
public abstract class GoapUnitBase implements IGoapUnit {

    @NonNull
    private final List<ImportantUnitChangeEventListenable> importantUnitGoalChangeListeners = new ArrayList<>();
    @Getter
    @NonNull
    private final List<GoapState> goalState = new ArrayList<>();
    @Getter
    @NonNull
    private final Set<WorldAspect> worldState = new HashSet<>();
    @Getter
    @NonNull
    private final Set<GoapActionBase> availableActions = new HashSet<>();


    /**
     * This function can be called by a subclass if the efforts of the unit
     * trying to archive a specific goal should be paused to fulfill a more
     * urgent goal. The GoapState is added to the main goal HashSet and changed
     * by the GoapAgent.
     * <p>
     * IMPORTANT:
     * <p>
     * The function must only be called once per two update cycles. The reason
     * for this is that the function pushes an IdleState on the FSM Stack which
     * is transformed into an GoapAction Queue in the current cycle. Calling the
     * function again in the next one pushes a new IdleState on top of this
     * generated action Queue, which renders the Queue obsolete and causes the
     * Unit to not perform any action since the RunActionState is now beneath
     * the newly pushed IdleState.
     *
     * @param newGoapState the new goal the unit tries to archive.
     */
    protected final void changeGoalImmediatly(@NonNull final GoapState newGoapState) {
        goalState.add(newGoapState);
        dispatchNewImportantUnitGoalChangeEvent(newGoapState);
    }

    private synchronized void dispatchNewImportantUnitGoalChangeEvent(@NonNull final GoapState newGoalState) {
        importantUnitGoalChangeListeners.forEach(listener -> listener.onImportantUnitGoalChange(newGoalState));
    }


    /**
     * Can be called to remove any existing GoapActions and start fresh.
     */
    protected void resetActions() {
        dispatchNewImportantUnitStackResetEvent();
    }

    private synchronized void dispatchNewImportantUnitStackResetEvent() {
        importantUnitGoalChangeListeners.forEach(ImportantUnitChangeEventListenable::onImportantUnitStackResetChange);
    }

    protected void addWorldStateAspect(@NonNull final WorldAspect newWorldState) {
        worldState.add(newWorldState);
    }

    protected void removeWorldStateAspect(@NonNull final String aspect) {
        worldState.remove(aspect);
    }

    protected void removeWorldStateAspects(@NonNull final GoapState goapState) {
        worldState.removeIf(worldAspect -> worldAspect.getEffect().equals(goapState.getEffect()));
    }

    public synchronized void addImportantUnitGoalChangeListener(@NonNull final ImportantUnitChangeEventListenable listener) {
        importantUnitGoalChangeListeners.add(listener);
    }

    public synchronized void removeImportantUnitGoalChangeListener(@NonNull final ImportantUnitChangeEventListenable listener) {
        importantUnitGoalChangeListeners.remove(listener);
    }

    protected void addGoalState(@NonNull final GoapState newGoalState) {
        goalState.add(newGoalState);
    }

    protected void removeGoalStates(@NonNull final String property) {
        goalState.removeIf(goapState -> goapState.getEffect().equals(property));
    }

    protected void removeGoalState(@NonNull final GoapState goapState) {
        this.goalState.remove(goapState);
    }

    protected void addAvailableAction(@NonNull final GoapActionBase action) {
        availableActions.add(action);
    }

    protected void removeAvailableAction(GoapActionBase action) {
        availableActions.remove(action);
    }

}
