package com.recom.goapcom.agent;

import com.recom.goapcom.agent.state.FSMState;
import com.recom.goapcom.agent.state.IdleState;
import com.recom.goapcom.agent.state.MoveToState;
import com.recom.goapcom.agent.state.PerformActionState;
import com.recom.goapcom.plan.GePlanner;
import lombok.NonNull;

import java.util.List;

public class GeAgentFactory {

    @NonNull
    public static GeAgent createAgent(@NonNull final GePlanner planner) {
        final GeAgent agent = new GeAgent();
        agent.setPlanner(planner);

        final List<FSMState> states = List.of(
                new IdleState(),
                new PerformActionState(),
                new MoveToState()
        );
        final GeFSM fsm = new GeFSM(agent, states);

        agent.setFsm(fsm);

        return agent;
    }

}
