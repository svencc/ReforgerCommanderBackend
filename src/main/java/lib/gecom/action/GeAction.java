package lib.gecom.action;

import lib.gecom.GeAgent;
import lib.gecom.GeNullTarget;
import lib.gecom.GeTargetable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;

@SuperBuilder
@RequiredArgsConstructor
public abstract class GeAction implements GeActionable {

    @Getter
    @NonNull
    private final GeAgent agent;
    @Getter
    @NonNull
    private final String name;
    @Getter
    @NonNull
    @Builder.Default
    private final Float cost = 1.0f;
    @Getter
    @NonNull
    @Builder.Default
    private final GeTargetable target = new GeNullTarget();
    @NonNull
    @Builder.Default
    private final Float duration = 0.0f;
    @Getter
    @NonNull
    @Builder.Default
    private final HashMap<String, Integer> preconditions = new HashMap<>();
    @Getter
    @NonNull
    @Builder.Default
    private final HashMap<String, Integer> afterEffects = new HashMap<>();
    //    @NonNull
//    @Builder.Default
//    private final GeWorldStates worldState = new GeWorldStates(); // required to calculate isAchievable!
    @Setter
    @Getter
    @Builder.Default
    private boolean moving = false;

    public boolean isAchievable() {
        return true;
    }

    public boolean arePreconditionsMet(@NonNull final HashMap<String, Integer> state) {
        return preconditions.entrySet().stream()
                .allMatch(entry -> state.containsKey(entry.getKey()) && state.get(entry.getKey()).equals(entry.getValue()));

    }


    public abstract boolean prePerform();

    public abstract boolean postPerform();


}
