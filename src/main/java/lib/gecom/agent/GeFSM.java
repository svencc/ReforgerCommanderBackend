package lib.gecom.agent;

import lib.gecom.agent.state.FSMState;
import lib.gecom.agent.state.Startable;
import lib.gecom.agent.state.Stoppable;
import lib.gecom.agent.state.TransitionRequestObserver;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeFSM {

    @Getter
    @NonNull
    private final Set<FSMState> states = new HashSet<>();
    @NonNull
    private final TransitionRequestObserver transitionRequestObserver = new TransitionRequestObserver(this);
    @NonNull
    private final GeAgent agent;
    @Nullable
    private FSMState currentState;

    @Getter
    private boolean hasStarted = false;

    @Getter
    private boolean hasStopped = false;

    public GeFSM(@NonNull final GeAgent agent) {
        this.agent = agent;
    }

    public void start() throws IllegalStateException {
        final List<FSMState> startables = states.stream()
                .filter(state -> state instanceof Startable)
                .toList();


        if (startables.size() > 1) {
            throw new IllegalStateException("Too many startable states");
        } else if (startables.isEmpty()) {
            throw new IllegalStateException("No startable states");
        } else {
            currentState = startables.get(0);

            prepareStates();

            ((Startable) currentState).start();
            currentState.enter();
            hasStarted = true;
        }
    }

    private void prepareStates() {
        states.forEach(state -> transitionRequestObserver.observe(state.getSubject()));
    }

    public void stop() throws IllegalStateException {
        if (currentState != null) {
            if (currentState instanceof Stoppable) {
                currentState.exit();
                ((Stoppable) currentState).stop();
                hasStopped = true;
            }
        } else {
            final List<FSMState> stoppableCandidates = states.stream()
                    .filter(state -> state instanceof Stoppable)
                    .toList();

            if (stoppableCandidates.size() > 1) {
                throw new IllegalStateException("Too many stopable states");
            } else if (stoppableCandidates.isEmpty()) {
                throw new IllegalStateException("No stopable states");
            } else {
                final FSMState stopableState = stoppableCandidates.get(0);
                currentState = stopableState;
                currentState.exit();
                ((Stoppable) currentState).stop();
                hasStopped = true;
            }
        }
    }

    public void process() {
        if (currentState != null) {
            currentState.update();
        }
    }

    public void requestTransition(
            @NonNull final FSMState from,
            @NonNull final FSMState to
    ) {
        transitionToState(to);
    }

    // check if transition is possible -> throw exception if not
    public void transitionToState(@NonNull final FSMState toState) {
        if (currentState != null) {
            currentState.performTransition(toState);
            currentState.exit();

            currentState = toState;
            currentState.enter();
        }
    }

}
