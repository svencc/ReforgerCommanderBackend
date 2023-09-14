package lib.gecom.agent;

import lib.gecom.plan.GePlanner;
import lombok.NonNull;

public class GeAgentFactory {

    @NonNull
    public static GeAgent createAgent(@NonNull final GePlanner planner) {
        final GeAgent agent = new GeAgent(planner);
        agent.fsm = new GeFSM(agent);

        return agent;
    }

}
