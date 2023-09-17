package lib.gecom.agent.state;

import lib.gecom.agent.event.RequestTransitionChangeEvent;
import lib.gecom.observer.HasSubject;
import lib.gecom.observer.Subject;
import lib.clipboard.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;

public abstract class FSMState implements HasSubject<RequestTransitionChangeEvent> {

    @Getter
    private final Subject<RequestTransitionChangeEvent> subject = new Subject<>();

    public void performTransition(@NonNull final FSMState toState) {

    }

    public abstract void enter();

    public abstract void exit();

    public abstract void update();

    /**
     * Returning false results in the removing of the implementers instance on
     * the stack of the FSM. True signalizes that the running actions are valid
     * and not finished / obsolete. Catches Exceptions for the FSM State to
     * check if a plan has failed.
     *
     * @param goapUnit the unit which action is being run.
     * @return true or false depending on if the action is still running (true) or
     * is completed (false).
     */
    public abstract boolean isStateStillPerforming(@NonNull final IGoapUnit goapUnit) throws Exception;

}
