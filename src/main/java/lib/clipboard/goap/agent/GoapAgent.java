package lib.clipboard.goap.agent;

import lib.clipboard.goap.planner.GoapPlanner;
import lib.clipboard.goap.unit.IGoapUnit;
import lib.clipboard.goap.planner.GoapPlannerable;
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
