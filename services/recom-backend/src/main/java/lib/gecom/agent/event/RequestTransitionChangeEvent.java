package lib.gecom.agent.event;


import lib.gecom.agent.state.FSMStates;
import lib.gecom.observer.Note;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestTransitionChangeEvent implements Note<RequestTransitionChangeEvent> {

    @NonNull
    private final FSMStates to;

    private final boolean fsmReprocess;

}
