package lib.gecom;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;

@SuperBuilder
@RequiredArgsConstructor
public abstract class GeAction {

    @Getter
    @NonNull
    private final String name;
    @Getter
    @NonNull
    @Builder.Default
    private final Float cost = 1.0f;
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
    @NonNull
    private final GeWorldStates agentBelieves;

    @Builder.Default
    public boolean actionRunning = false;

    public boolean isAchievable() {
        return true;
        /*
        return preconditions.stream()
                .allMatch(
                        precondition -> agentBelieves.getState(precondition.getKey())
                                .orElse(0) >= precondition.getValue()
                );
         */
    }

    // @TODO: I guess it would be better to pass a Predicate or Provider here instead of a state;
    // One implementation could be to pass a CheckState Predicate here!
    public boolean isAchievableGiven(@NonNull final HashMap<String, Integer> state) {
        return preconditions.entrySet().stream()
                .allMatch(
                        precondition -> state.getOrDefault(precondition.getKey(), 0) >= precondition.getValue()
                );
    }


    public abstract boolean prePerform();

    public abstract boolean postPerform();


}
