package lib.gecom.agent.event;


import lib.gecom.agent.state.FSMState;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestTransitionChangeEvent {

    @NonNull
    private final FSMState from;
    @NonNull
    private final FSMState to;

}
