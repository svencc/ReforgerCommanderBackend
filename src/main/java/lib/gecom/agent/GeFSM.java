package lib.gecom.agent;

import lib.gecom.agent.state.FSMState;
import lib.gecom.agent.state.Startable;
import lib.gecom.agent.state.Stoppable;
import lib.gecom.agent.state.TransitionRequestObserver;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GeFSM {

    @Getter
    @NonNull
    private final Set<FSMState> states = new HashSet<>();
    @NonNull
    private final TransitionRequestObserver transitionRequestObserver = new TransitionRequestObserver(this);
    @NonNull
    private final GeAgent agent;
    @Getter
    @NonNull
    private Optional<FSMState> maybeCurrentState = Optional.empty();

    @Getter
    private boolean running = false;

    @Getter
    private boolean stopped = false;

    @Getter
    private long lastUpdate = 0;

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
            maybeCurrentState = Optional.of(startables.get(0));

            prepareStates();

            ((Startable) maybeCurrentState.get()).start();
            maybeCurrentState.get().enter();
            running = true;
        }
    }

    private void prepareStates() {
        states.forEach(state -> transitionRequestObserver.observe(state.getSubject()));
    }

    public void stop() throws IllegalStateException {
        if (maybeCurrentState.isPresent()) {
            final FSMState currentState = maybeCurrentState.get();
            if (currentState instanceof Stoppable) {
                currentState.exit();
                ((Stoppable) currentState).stop();
                running = false;
                stopped = true;
            } else {
                final List<FSMState> stoppableCandidates = states.stream()
                        .filter(state -> state instanceof Stoppable)
                        .toList();

                if (stoppableCandidates.size() > 1) {
                    throw new IllegalStateException("Too many stoppable states");
                } else if (stoppableCandidates.isEmpty()) {
                    throw new IllegalStateException("No stoppable states");
                } else {
                    final FSMState stopableState = stoppableCandidates.get(0);
                    maybeCurrentState = Optional.of(stopableState);
                    transitionToState(stopableState);

                    final FSMState newStoppableCurrentState = maybeCurrentState.get();
                    newStoppableCurrentState.exit();
                    ((Stoppable) newStoppableCurrentState).stop();
                    running = false;
                    stopped = true;
                }
            }
        }
    }

    public void transitionToState(@NonNull final FSMState toState) throws IllegalStateException {
        if (maybeCurrentState.isEmpty()) {
            throw new IllegalStateException("Current state is null");
        }
        if (isTransitionable(maybeCurrentState.get(), toState)) {
            transitionFromToState(maybeCurrentState.get(), toState);
        } else {
            throw new IllegalStateException("Transition not possible");
        }
    }

    private boolean isTransitionable(
            @NonNull final FSMState fromState,
            @NonNull final FSMState toState
    ) throws IllegalStateException {
        return fromState.isTransitionToPerformable(toState) && toState.transitionFromPerformable(fromState);
    }

    private void transitionFromToState(
            @NonNull final FSMState fromState,
            @NonNull final FSMState toState
    ) throws IllegalStateException {
        fromState.exit();
        toState.transitionFrom(fromState);

        maybeCurrentState = Optional.of(toState);
        toState.enter();
    }

    public void process() {
        if (maybeCurrentState.isPresent()) {
            final FSMState currentState = maybeCurrentState.get();
            currentState.update(agent);
            lastUpdate = System.currentTimeMillis();
        }
    }

    public void requestTransition(
            @NonNull final FSMState from,
            @NonNull final FSMState to
    ) {
        transitionToState(to);
    }

}