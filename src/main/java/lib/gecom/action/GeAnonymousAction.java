package lib.gecom.action;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;

@SuperBuilder
@RequiredArgsConstructor
public class GeAnonymousAction implements GeActionable {

    @Getter
    @NonNull
    private final String name;
    @Getter
    @NonNull
    @Builder.Default
    private final Float cost = 1.0f;
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

    public boolean isAchievable() {
        return true;
    }

    @Override
    public boolean arePreconditionsMet(@NonNull final HashMap<String, Integer> state) {
        return preconditions.entrySet().stream()
                .allMatch(entry -> state.containsKey(entry.getKey()) && state.get(entry.getKey()).equals(entry.getValue()));
    }

    public boolean prePerform() {
        return true;
    }

    public boolean postPerform() {
        return true;
    }

}
