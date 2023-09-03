package lib.goap;

/**
 * DefaultGoapAgent.java --- The Default implementation of the GoapAgent. Only
 * the IGoapUnit has to be assigned.
 * 
 * @author P H - 15.03.2017
 *
 */
public class DefaultGoapAgent extends GoapAgent {

	public DefaultGoapAgent(IGoapUnit assignedUnit) {
		super(assignedUnit);
	}

	@Override
	protected IGoapPlanner generatePlannerObject() {
		return new DefaultGoapPlanner();
	}

}
