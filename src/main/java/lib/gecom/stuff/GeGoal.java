package lib.gecom.stuff;

import lombok.*;

import java.util.HashMap;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeGoal implements Comparable<GeGoal> {

    @NonNull
    @Builder.Default
    private final HashMap<String, Integer> statesToReach = new HashMap<>();

    @Setter
    @Builder.Default
    public boolean intendedToRemoveAfterSatisfaction = true;

    @Setter
    @NonNull
    @Builder.Default
    private Integer priority = 0;


    @Override
    public int compareTo(@NonNull final GeGoal other) {
        final Integer mine = this.priority;
        final Integer theirs = other.priority;

        return mine.compareTo(theirs);
    }

}
