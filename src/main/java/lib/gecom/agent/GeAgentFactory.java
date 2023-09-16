package lib.gecom.agent;

import lib.gecom.plan.GePlanner;
import lombok.NonNull;

import java.util.List;

public class GeAgentFactory {

    @NonNull
    public static GeAgent createAgent(@NonNull final GePlanner planner) {
        final GeAgent agent = new GeAgent();
        agent.setPlanner(planner);

        final GeFSM fsm = new GeFSM(agent);

        final List<FSMStateful> states = List.of(
                new IdleState(agent),
                new PerformActionState(agent),
                new MoveToState(agent)
        );
        fsm.getStates().addAll(states);
        agent.setFsm(fsm);
        agent.start();

        return agent;
    }

}
