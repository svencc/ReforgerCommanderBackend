package lib.goap.unit;

import lib.goap.GoapAction;
import lib.goap.GoapState;
import lib.goap.agent.ImportantUnitChangeEventListenable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public abstract class GoapUnitBase implements IGoapUnit {

    @NonNull
    private final List<ImportantUnitChangeEventListenable> importantUnitGoalChangeListeners = new ArrayList<>();
    @Getter
    private List<GoapState> goalState = new ArrayList<>();
    private HashSet<GoapState> worldState = new HashSet<>();
    @Getter
    private HashSet<GoapAction> availableActions = new HashSet<>();


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
    protected final void changeGoalImmediatly(GoapState newGoapState) {
        this.goalState.add(newGoapState);

        this.dispatchNewImportantUnitGoalChangeEvent(newGoapState);
    }

    private synchronized void dispatchNewImportantUnitGoalChangeEvent(GoapState newGoalState) {
        for (Object listener : this.importantUnitGoalChangeListeners) {
            ((ImportantUnitChangeEventListenable) listener).onImportantUnitGoalChange(newGoalState);
        }
    }


    // ------------------------------ Getter / Setter

    /**
     * Can be called to remove any existing GoapActions and start fresh.
     */
    protected void resetActions() {
        this.dispatchNewImportantUnitStackResetEvent();
    }

    private synchronized void dispatchNewImportantUnitStackResetEvent() {
        for (Object listener : this.importantUnitGoalChangeListeners) {
            ((ImportantUnitChangeEventListenable) listener).onImportantUnitStackResetChange();
        }
    }

    protected void addWorldState(GoapState newWorldState) {
        boolean missing = true;

        for (GoapState state : this.worldState) {
            if (newWorldState.effect.equals(state.effect)) {
                missing = false;

                break;
            }
        }

        if (missing) {
            this.worldState.add(newWorldState);
        }
    }

    protected void removeWorldState(String effect) {
        GoapState marked = null;

        for (GoapState state : this.worldState) {
            if (effect.equals(state.effect)) {
                marked = state;

                break;
            }
        }

        if (marked != null) {
            this.worldState.remove(marked);
        }
    }

    protected void removeWorldState(GoapState goapState) {
        this.worldState.remove(goapState);
    }

    public HashSet<GoapState> getWorldState() {
        return this.worldState;
    }

    // ---------------------------------------- WorldState
    protected void setWorldState(HashSet<GoapState> worldState) {
        this.worldState = worldState;
    }

    public synchronized void addImportantUnitGoalChangeListener(@NonNull final ImportantUnitChangeEventListenable listener) {
        importantUnitGoalChangeListeners.add(listener);
    }

    public synchronized void removeImportantUnitGoalChangeListener(@NonNull final ImportantUnitChangeEventListenable listener) {
        importantUnitGoalChangeListeners.remove(listener);
    }

    public void setGoalState(List<GoapState> list) {
        this.goalState = list;
    }

    protected void setAvailableActions(HashSet<GoapAction> availableActions) {
        this.availableActions = availableActions;
    }

    protected void addGoalState(GoapState newGoalState) {
        boolean missing = true;

        for (GoapState state : this.goalState) {
            if (newGoalState.equals(state.effect)) {
                missing = false;

                break;
            }
        }

        if (missing) {
            this.goalState.add(newGoalState);
        }
    }

    protected void removeGoalState(String effect) {
        GoapState marked = null;

        for (GoapState state : this.goalState) {
            if (effect.equals(state.effect)) {
                marked = state;

                break;
            }
        }

        if (marked != null) {
            this.goalState.remove(marked);
        }
    }

    protected void removeGoalStat(GoapState goapState) {
        this.goalState.remove(goapState);
    }

    protected void addAvailableAction(GoapAction action) {
        if (!this.availableActions.contains(action)) {
            this.availableActions.add(action);
        }
    }

    protected void removeAvailableAction(GoapAction action) {
        this.availableActions.remove(action);
    }

}
