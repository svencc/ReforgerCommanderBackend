package lib.gecom;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@RequiredArgsConstructor
public abstract class GeAction {

    @NonNull
    private final String name;
    @NonNull
    @Builder.Default
    private final Float cost = 1.0f;
    @NonNull
    @Builder.Default
    private final GeTargetable target = new GeNullTarget();
    @NonNull
    @Builder.Default
    private final Float duration = 0.0f;
    @NonNull
    @Builder.Default
    private final List<GeWorldState> preconditions = new ArrayList<>();
    @NonNull
    @Builder.Default
    private final List<GeWorldState> afterEffects = new ArrayList<>();
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

    public boolean isAchievableGiven(@NonNull final GeWorldStates worldStates) {
        return preconditions.stream()
                .allMatch(
                        precondition -> worldStates.getState(precondition.getKey())
                                .orElse(0) >= precondition.getValue()
                );
    }

    public abstract boolean prePerform();

    public abstract boolean postPerform();


}
