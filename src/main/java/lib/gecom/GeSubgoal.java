package lib.gecom;

import lombok.NonNull;

import java.util.HashMap;

public class GeSubgoal {

    @NonNull
    public final HashMap<String, Integer> subgoals = new HashMap<>();
    public final boolean removeAfterSatisfaction;

    public GeSubgoal(
            @NonNull final String subgoal,
            final int value,
            final boolean removeAfterSatisfaction
    ) {
        this.subgoals.put(subgoal, value);
        this.removeAfterSatisfaction = removeAfterSatisfaction;
    }

    public GeSubgoal(
            @NonNull final String subgoal,
            final int value
    ) {
        this.subgoals.put(subgoal, value);
        this.removeAfterSatisfaction = true;
    }


}
