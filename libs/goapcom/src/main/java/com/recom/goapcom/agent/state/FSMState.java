package com.recom.goapcom.agent.state;

import com.recom.goapcom.agent.GeAgent;
import com.recom.goapcom.event.RequestTransitionChangeEvent;
import com.recom.observer.HasSubject;
import com.recom.observer.Subject;
import lombok.Getter;
import lombok.NonNull;

public abstract class FSMState implements HasSubject<RequestTransitionChangeEvent> {

    @Getter
    private final Subject<RequestTransitionChangeEvent> subject = new Subject<>();

    public boolean transitionFromPerformable(@NonNull final FSMState fromState) {
        return true;
    }

    public boolean isTransitionToPerformable(@NonNull final FSMState toState) {
        return true;
    }

    public abstract void enter();

    public abstract void exit();

    public abstract void execute(@NonNull final GeAgent agent);

    public void transitionFrom(
            @NonNull final FSMState fromState,
            @NonNull final GeAgent agent
    ) {

    }

}
