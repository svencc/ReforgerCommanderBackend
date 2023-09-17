package lib.gecom.agent.state;

import lib.gecom.agent.GeFSM;
import lib.gecom.agent.event.RequestTransitionChangeEvent;
import lib.gecom.observer.Note;
import lib.gecom.observer.ObserverTemplate;
import lib.gecom.observer.Subject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransitionRequestObserver extends ObserverTemplate<RequestTransitionChangeEvent> {

    @NonNull
    private final GeFSM fsm;


    @Override
    public void takeNotice(
            @NonNull final Subject<RequestTransitionChangeEvent> subject,
            @NonNull final Note<RequestTransitionChangeEvent> event
    ) {
        if (event instanceof RequestTransitionChangeEvent transitionChangeEvent) {
            fsm.requestTransition(transitionChangeEvent.getTo());
            if (transitionChangeEvent.isFsmReprocess()) {
                System.out.println("... reprocessing FSM");
                fsm.process();
            }
        }
    }

}