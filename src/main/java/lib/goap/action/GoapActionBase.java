package lib.goap.action;

import lib.goap.state.GoapState;
import lib.goap.target.GoatTargetable;
import lib.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;

@Getter
public abstract class GoapActionBase {

    @NonNull
    protected final GoatTargetable target;
    @NonNull
    private final HashSet<GoapState> preconditions = new HashSet<>();
    @NonNull
    private final HashSet<GoapState> effects = new HashSet<>();

    /**
     * @param target the target of the action. Can be null.
     */
    public GoapActionBase(@NonNull final GoatTargetable target) {
        this.target = target;
    }

    /**
     * Checks if the current action of the GoapAction Queue is finished. Gets
     * called until it returns true.
     *
     * @param goapUnit the unit the action is checked for.
     * @return true or false depending on the success of the action. Returning
     * true causes the swap to the next action in the Queue.
     */
    public abstract boolean isDone(@NonNull final IGoapUnit goapUnit);

    /**
     * Gets called when the action is going to be executed by the Unit.
     *
     * @param goapUnit the GoapUnit that is trying to execute the action.
     * @return true or false depending on if the action was successful.
     */
    public abstract boolean performAction(@NonNull final IGoapUnit goapUnit);

    /**
     * This function will be called for each GoapAction in the generation of
     * each Graph to determine the cost for each node in the graph. The two
     * functions called in this function have to be implemented by the Subclass
     * to get the sum of both costs. Differentiating between the base cost and
     * the cost relative to the target gives a proper representation of the work
     * the unit has to do i.e. if it has to travel a large distance to reach its
     * target.
     *
     * @param goapUnit the unit whose action cost is being calculated.
     * @return the calculated action cost.
     */
    public float generateCost(@NonNull final IGoapUnit goapUnit) {
        return generateBaseCost(goapUnit) + generateCostRelativeToTarget(goapUnit);
    }

    /**
     * Defines the base cost of the action.
     *
     * @param goapUnit the unit the action is being executed from.
     * @return the base cost of the action which is added to the cost relative
     * to the target.
     */
    public abstract float generateBaseCost(@NonNull final IGoapUnit goapUnit);

    /**
     * Defines the relative cost of the action.
     *
     * @param goapUnit the unit the action is being executed from.
     * @return the relative cost of the action in relation to the current
     * target, which is added to the base cost.
     */
    public abstract float generateCostRelativeToTarget(@NonNull final IGoapUnit goapUnit);

    /**
     * Gets called to determine if the preconditions of an action are met. If
     * they are not, the action will not be taken in consideration for the
     * generation of the action graph.
     *
     * @param goapUnit the unit the action is being executed from.
     * @return true or false depending on if the action can be taken in the first
     * place.
     */
    public abstract boolean checkProceduralPrecondition(@NonNull final IGoapUnit goapUnit);

    /**
     * Defines if the unit needs to be in a certain range in relation to the
     * target to execute the action.
     *
     * @param goapUnit the unit the action is being executed from.
     * @return true or false depending on if the action requires the unit to be in
     * a certain range near the target.
     */
    public abstract boolean requiresInRange(@NonNull final IGoapUnit goapUnit);

    /**
     * Function to determine if the unit is in a certain range. Only gets called
     * if the action requires to be in range relative to the target.
     *
     * @param goapUnit the unit the action is being executed from.
     * @return true or false depending on if the unit is in range to execute the
     * action.
     * @see #requiresInRange(IGoapUnit goapUnit)
     */
    public abstract boolean isInRange(@NonNull final IGoapUnit goapUnit);

    /**
     * Function used to reset an action. Gets called once the Action finishes
     * or, if the GoapUnit class was used, when the Stack on the FSM gets
     * reset.
     */
    public abstract void reset();

    /**
     * Add a precondition, which is not already in the HashSet.
     *
     * @param precondition which is going to be added to the action.
     */
    public void addPrecondition(@NonNull final GoapState precondition) {
        boolean alreadyInList = false;

        for (final GoapState goapState : preconditions) {
            if (goapState.equals(precondition)) {
                alreadyInList = true;
                break;
            }
        }

        if (!alreadyInList) {
            preconditions.add(precondition);
        }
    }

    /**
     * Remove a precondition from the HashSet.
     *
     * @param preconditionEffect the effect which is going to be removed.
     * @return true or false depending on if the precondition was removed.
     */
    public boolean removePrecondition(@NonNull final String preconditionEffect) {
        GoapState stateToBeRemoved = null;

        for (final GoapState goapState : effects) {
            if (goapState.getEffect().equals(preconditionEffect)) {
                stateToBeRemoved = goapState;
            }
        }

        if (stateToBeRemoved != null) {
            preconditions.remove(stateToBeRemoved);
            return true;
        } else {
            return false;
        }
    }


    /**
     * Add an effect, which is not already in the HashSet
     *
     * @param effect the effect which is going to be added to the action.
     */
    public void addEffect(@NonNull final GoapState effect) {
        boolean alreadyInList = false;

        for (final GoapState goapState : effects) {
            if (goapState.equals(effect)) {
                alreadyInList = true;
                break;
            }
        }

        if (!alreadyInList) {
            effects.add(effect);
        }
    }

    /**
     * Overloaded function for convenience.
     *
     * @param effect the effect that is being removed.
     * @return true or false depending on if the effect was removed successfully.
     * @see #removeEffect(String effectEffect)
     */
    public boolean removeEffect(@NonNull final GoapState effect) {
        return removeEffect(effect.getEffect());
    }

    /**
     * Remove an effect from the HashSet.
     *
     * @param effectEffect the effect which is going to be removed.
     * @return true or false depending on if the effect was removed.
     */
    public boolean removeEffect(@NonNull final String effectEffect) {
        GoapState stateToBeRemoved = null;

        for (final GoapState goapState : effects) {
            if (goapState.getEffect().equals(effectEffect)) {
                stateToBeRemoved = goapState;
            }
        }

        if (stateToBeRemoved != null) {
            effects.remove(stateToBeRemoved);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public abstract int hashCode();

}
