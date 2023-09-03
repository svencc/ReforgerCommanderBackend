package lib.goap.planner;

import lib.goap.GoapAction;
import lib.goap.unit.IGoapUnit;
import lombok.NonNull;

import java.util.Queue;

public interface GoapPlannerable {

    /**
     * Function for planning an GoapAction Queue.
     *
     * @param goapUnit the GoapUnit for which an Action Queue is being created.
     * @return a created GoapAction Queue or null, if no Actions and goals
     * match.
     */
    Queue<GoapAction> plan(@NonNull final IGoapUnit goapUnit);

}
