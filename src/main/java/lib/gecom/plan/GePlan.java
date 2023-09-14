package lib.gecom.plan;

import lib.gecom.action.GeAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Queue;

@Getter
@Builder
@AllArgsConstructor
public class GePlan implements Comparable<GePlan> {

    @NonNull
    private final Float cost;

    @NonNull
    private final Queue<GeAction> actions;

    @Override
    public int compareTo(@NonNull final GePlan other) {
        final Float mine = cost;
        final Float theirs = other.cost;

        return mine.compareTo(theirs);
    }

}
