package com.recom.goapcom.agent;

import com.recom.goapcom.agent.state.*;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

public class GeFSM {

    @NonNull
    private final Set<FSMState> states = new HashSet<>();
    @Getter
    @NonNull
    private final Map<FSMStates, FSMState> statesDictionary = new HashMap<>();
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

    public GeFSM(
            @NonNull final GeAgent agent,
            @NonNull final Collection<FSMState> states
    ) {
        this.agent = agent;
        this.states.addAll(states);
        bakeStates();
    }

    private void bakeStates() {
        final FSMState startableState = states.stream().filter(state -> state instanceof Startable).findFirst().orElseThrow(() -> new IllegalStateException("No STARTABLE state"));
        final FSMState stoppable = states.stream().filter(state -> state instanceof Stoppable).findFirst().orElseThrow(() -> new IllegalStateException("No STOPPABLE state"));
        final FSMState idleState = states.stream().filter(state -> state instanceof IdleState).findFirst().orElseThrow(() -> new IllegalStateException("No IDLE state"));
        final FSMState performable = states.stream().filter(state -> state instanceof Performable).findFirst().orElseThrow(() -> new IllegalStateException("No PERFORMABLE state"));
        final FSMState movable = states.stream().filter(state -> state instanceof Movable).findFirst().orElseThrow(() -> new IllegalStateException("No MOVABLE state"));

        statesDictionary.put(FSMStates.STARTABLE, startableState);
        statesDictionary.put(FSMStates.STOPPABLE, stoppable);
        statesDictionary.put(FSMStates.IDLE, idleState);
        statesDictionary.put(FSMStates.PERFORMABLE, performable);
        statesDictionary.put(FSMStates.MOVABLE, movable);
    }

    public void start() throws IllegalStateException {
        prepareStates();

        ((Startable) provideState(FSMStates.STARTABLE)).start();
        provideState(FSMStates.STARTABLE).enter();
        maybeCurrentState = Optional.of(provideState(FSMStates.STARTABLE));
        running = true;
    }

    private void prepareStates() {
        states.forEach(state -> transitionRequestObserver.observe(state.getSubject()));
    }

    @NonNull
    public FSMState provideState(@NonNull final FSMStates state) {
        return statesDictionary.get(state);
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
                final FSMState stopableState = provideState(FSMStates.STOPPABLE);
                maybeCurrentState = Optional.of(stopableState);
                transitionToState(FSMStates.STOPPABLE);

                final FSMState newStoppableCurrentState = maybeCurrentState.get();
                newStoppableCurrentState.exit();
                ((Stoppable) newStoppableCurrentState).stop();
                stopped = true;
                running = false;
                maybeCurrentState = Optional.empty();
            }
        }
    }

    private void transitionToState(@NonNull final FSMStates toState) {
        if (maybeCurrentState.isEmpty()) {
            throw new IllegalStateException("Current state is null");
        }
        if (isTransitionable(maybeCurrentState.get(), provideState(toState))) {
            executeTransitionFromToState(maybeCurrentState.get(), provideState(toState));
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

    private void executeTransitionFromToState(
            @NonNull final FSMState fromState,
            @NonNull final FSMState toState
    ) throws IllegalStateException {
        fromState.exit();
        maybeCurrentState = Optional.of(toState);

        toState.enter();
        maybeCurrentState.get().transitionFrom(fromState, agent);
    }

    public void process() {
        if (maybeCurrentState.isPresent()) {
            final FSMState currentState = maybeCurrentState.get();
            currentState.execute(agent);
            lastUpdate = System.currentTimeMillis();
        }
    }

    public void requestTransition(@NonNull final FSMStates to) {
        transitionToState(to);
    }

}