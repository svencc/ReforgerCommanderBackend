package com.recom.goapcom.agent.state;

import com.recom.goapcom.agent.GeFSM;
import com.recom.goapcom.event.RequestTransitionChangeEvent;
import com.recom.observer.Notification;
import com.recom.observer.ObserverTemplate;
import com.recom.observer.Subjective;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransitionRequestObserver extends ObserverTemplate<RequestTransitionChangeEvent> {

    @NonNull
    private final GeFSM fsm;


    @Override
    public void takeNotice(
            @NonNull final Subjective<RequestTransitionChangeEvent> subject,
            @NonNull final Notification<RequestTransitionChangeEvent> event
    ) {
        if (event.getPayload() instanceof RequestTransitionChangeEvent transitionChangeEvent) {
            fsm.requestTransition(transitionChangeEvent.getTo());
            if (transitionChangeEvent.isFsmReprocess()) {
                System.out.println("... reprocessing FSM");
                fsm.process();
            }
        }
    }

}