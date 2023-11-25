package com.recom.goapcom.event;


import com.recom.goapcom.agent.state.FSMStates;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestTransitionChangeEvent implements GOAPCOMEvent {

    @NonNull
    private final FSMStates to;

    private final boolean fsmReprocess;

}
