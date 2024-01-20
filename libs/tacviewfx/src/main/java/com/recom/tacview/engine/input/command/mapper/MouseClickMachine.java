package com.recom.tacview.engine.input.command.mapper;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.MouseClickCommand;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.LinkedList;

@Slf4j
public class MouseClickMachine {

    @NonNull
    private final long doubleClickThresholdNanos;

    @NonNull
    private STATE currentMachineState = STATE.IDLE;

    @Nullable
    private NanoTimedEvent<MouseEvent> eventCandidateBuffer;

    @Getter
    @NonNull
    private final LinkedList<MouseClickCommand> bufferedCommands = new LinkedList<>();


    public MouseClickMachine(@NonNull final Duration doubleClickThreshold) {
        this.doubleClickThresholdNanos = doubleClickThreshold.toNanos();
    }

    public void iterate() {
        update(null);
    }

    public void iterate(@NonNull final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent) {
        update(nextNanoTimedMouseEvent);
    }

    private void update(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        final INPUT_ALPHABET input = determineInputAlphabet(nextEvent);
        switch (currentMachineState) {
            case IDLE -> {
                if (input == INPUT_ALPHABET.NEW_CLICK_CANDIDATE) {
                    assert nextEvent != null;
                    eventCandidateBuffer = nextEvent;
                    currentMachineState = STATE.CLICK_CANDIDATE;
                }
            }
            case CLICK_CANDIDATE -> {
                if (input == INPUT_ALPHABET.EMPTY) {
                    assert nextEvent == null;
                    assert eventCandidateBuffer != null;
                    if (doubleClickThresholdExpired(eventCandidateBuffer)) {
                        bufferedCommands.add(MouseClickCommand.singleClickCommand(eventCandidateBuffer));
                        eventCandidateBuffer = null;
                        currentMachineState = STATE.IDLE;
                    }
                } else if (input == INPUT_ALPHABET.NEW_CLICK_CANDIDATE) {
                    assert nextEvent != null;
                    assert eventCandidateBuffer != null;
                    if (doubleClickThresholdExpired(eventCandidateBuffer)) {
                        bufferedCommands.add(MouseClickCommand.singleClickCommand(eventCandidateBuffer));
                        eventCandidateBuffer = nextEvent;
                        currentMachineState = STATE.CLICK_CANDIDATE;
                    } else {
                        eventCandidateBuffer = null;
                        bufferedCommands.add(MouseClickCommand.doubleClickCommand(nextEvent));
                        currentMachineState = STATE.IDLE;
                    }
                }
            }
            default -> {
                throw new IllegalStateException(String.format("Unexpected Transition via State:%1s -> Input:%2s", currentMachineState, input));
            }
        }
    }

    private boolean doubleClickThresholdExpired(@NonNull final NanoTimedEvent<MouseEvent> event) {
        return (System.nanoTime() - event.getNanos()) > doubleClickThresholdNanos;
    }

    @NonNull
    private INPUT_ALPHABET determineInputAlphabet(@Nullable final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent) {
        if (nextNanoTimedMouseEvent == null) {
            return INPUT_ALPHABET.EMPTY;
        } else {
            return INPUT_ALPHABET.NEW_CLICK_CANDIDATE;
        }
    }

    enum STATE {
        IDLE,
        CLICK_CANDIDATE
    }

    enum INPUT_ALPHABET {
        EMPTY,
        NEW_CLICK_CANDIDATE
    }

}
