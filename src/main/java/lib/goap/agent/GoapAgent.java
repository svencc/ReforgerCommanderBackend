package lib.goap.agent;

import lib.goap.unit.IGoapUnit;
import lib.goap.planner.GoapPlanner;
import lib.goap.planner.GoapPlannerable;
import lombok.NonNull;

public class GoapAgent extends GoapAgentBase {

	public GoapAgent(@NonNull final IGoapUnit assignedUnit) {
		super(assignedUnit);
	}

	@Override
	protected GoapPlannerable generatePlannerObject() {
		return new GoapPlanner();
	}

}
