package lib.gecom.stuff;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class GeSubgoal implements Comparable<GeSubgoal> {


    @NonNull
    public final HashMap<String, Integer> statesToReach = new HashMap<>();
    public final boolean intendedToRemoveAfterSatisfaction;
    @NonNull
    public Integer priority = 0;

    public GeSubgoal(
            @NonNull final String subgoal,
            final int value,
            final boolean intendedToRemoveAfterSatisfaction
    ) {
        this.statesToReach.put(subgoal, value);
        this.intendedToRemoveAfterSatisfaction = intendedToRemoveAfterSatisfaction;
    }

    public GeSubgoal(
            @NonNull final String subgoal,
            final int value
    ) {
        this.statesToReach.put(subgoal, value);
        this.intendedToRemoveAfterSatisfaction = true;
    }


    @Override
    public int compareTo(@NonNull final GeSubgoal other) {
        final Integer mine = this.priority;
        final Integer theirs = other.priority;

        return mine.compareTo(theirs);
    }

}
