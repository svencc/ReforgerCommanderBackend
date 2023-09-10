package lib.gecom;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Optional;

@NoArgsConstructor
public class GeWorldStates {

    @Getter
    @NonNull
    private final HashMap<String, Integer> states = new HashMap<>();

    public void modifyState(
            @NonNull final String key,
            @NonNull final Integer value
    ) {
        if (hasState(key)) {
            states.replace(key, Math.max(states.get(key) + value, 0));
        } else {
            addState(key, value);
        }
    }

    public boolean hasState(@NonNull final String key) {
        return states.containsKey(key);
    }

    void addState(
            @NonNull final String key,
            @NonNull final Integer value
    ) {
        states.put(key, value);
    }

    public void removeState(@NonNull final String key) {
        states.remove(key);
    }

    public void setState(
            @NonNull final String key,
            @NonNull final Integer value
    ) {
        states.put(key, value);
    }

    @NonNull
    public Optional<Integer> getState(@NonNull final String key) {
        return Optional.ofNullable(states.get(key));
    }

}
