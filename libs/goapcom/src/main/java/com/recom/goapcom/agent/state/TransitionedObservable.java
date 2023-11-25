package com.recom.goapcom.agent.state;

import lombok.NonNull;

public interface TransitionedObservable {

    void onTransitionChange(
            @NonNull final FSMState from,
            @NonNull final FSMState to
    );

}
