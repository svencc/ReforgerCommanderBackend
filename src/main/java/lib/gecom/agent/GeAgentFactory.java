package lib.gecom.agent;

import lib.gecom.agent.state.FSMState;
import lib.gecom.agent.state.IdleState;
import lib.gecom.agent.state.MoveToState;
import lib.gecom.agent.state.PerformActionState;
import lib.gecom.plan.GePlanner;
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
