package lib.clipboard.goap.fsm;

import lib.clipboard.goap.agent.FSMPlanEventListenable;
import lib.clipboard.goap.action.GoapActionBase;
import lib.clipboard.goap.fsm.states.FSMStateful;
import lib.clipboard.goap.fsm.states.PerformActionState;
import lib.clipboard.goap.unit.IGoapUnit;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

@NoArgsConstructor
public final class FSM {

    @NonNull
    private final Stack<FSMStateful> states = new Stack<>();
    @NonNull
    private final List<FSMPlanEventListenable> planEventListeners = new ArrayList<>();


    /**
     * Run through all action in the specific states. If an Exception occurs
     * (mainly in RunActionState) the FSM assumes the plan failed. If an action
     * state returns false the FSM assumes the plan finished.
     *
     * @param goapUnit unit whose actions are getting cycled.
     */
    public void update(@NonNull final IGoapUnit goapUnit) {
        try {
            if (!states.isEmpty() && !states.peek().isStateStillPerforming(goapUnit)) {
                if (states.pop() instanceof PerformActionState) {
                    dispatchPlanFinishedEvent();
                }
            }
        } catch (Exception e) {
            final FSMStateful state = states.pop();
            if (state instanceof PerformActionState) {
                dispatchPlanFailedEvent(((PerformActionState) state).getCurrentActions());
            }
            e.printStackTrace();
        }
    }

    private synchronized void dispatchPlanFinishedEvent() {
        planEventListeners.forEach(FSMPlanEventListenable::onPlanFinished);
    }

    private synchronized void dispatchPlanFailedEvent(@NonNull final Queue<GoapActionBase> actions) {
        planEventListeners.forEach(listener -> listener.onPlanFailed(actions));
    }

    public void pushStack(@NonNull final FSMStateful state) {
        states.push(state);
    }

    public void popStack() {
        states.pop();
    }

    public void clearStack() {
        states.clear();
    }

    public boolean hasStates() {
        return !states.isEmpty();
    }

    public synchronized void addPlanEventListener(@NonNull final FSMPlanEventListenable listener) {
        planEventListeners.add(listener);
    }

    synchronized void removePlanEventListener(@NonNull final FSMPlanEventListenable listener) {
        planEventListeners.remove(listener);
    }

}
