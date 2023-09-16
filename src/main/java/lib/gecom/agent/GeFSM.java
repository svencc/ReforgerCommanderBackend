package lib.gecom.agent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GeFSM {

    @NonNull
    @Getter(AccessLevel.PACKAGE)
    private final Set<FSMStateful> states = new HashSet<>();

    @NonNull
    private final GeAgent agent;


    public GeFSM(@NonNull final GeAgent agent) {
        this.agent = agent;
    }

    public void start() {
        final List<FSMStateful> startables = states.stream()
                .filter(state -> state instanceof FSMStartableState)
                .toList();

        if (startables.size() > 1) {
            throw new IllegalStateException("Too many startable states");
        } else if (startables.isEmpty()) {
            throw new IllegalStateException("No startable states");
        } else {
            ((FSMStartableState) startables.get(0)).start();
        }
    }
    
}
