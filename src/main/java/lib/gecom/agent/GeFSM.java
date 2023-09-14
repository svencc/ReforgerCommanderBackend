package lib.gecom.agent;

import lombok.NonNull;

class GeFSM {

    @NonNull
    private final GeAgent agent;

    @NonNull
    private final IdleState idleState;

    @NonNull
    private final MoveToState moveToState;

    @NonNull
    private final PerformActionState performActionState;

    public GeFSM(@NonNull final GeAgent agent) {
        this.agent = agent;
        this.idleState = new IdleState(agent);
        this.moveToState = new MoveToState(agent);
        this.performActionState = new PerformActionState(agent);
    }

}
