package com.recom.tacview.engine.input.command.mapper.mouse.fsm;

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
public class MouseEventMachine {

    @NonNull
    private final long doubleClickThresholdNanos;

    @NonNull
    private FSMStates currentMachineState = FSMStates.IDLE;

    @Nullable
    private NanoTimedEvent<MouseEvent> eventCandidateBuffer;

    @Getter
    @NonNull
    private final LinkedList<MouseClickCommand> bufferedCommands = new LinkedList<>();


    public MouseEventMachine(@NonNull final Duration doubleClickThreshold) {
        this.doubleClickThresholdNanos = doubleClickThreshold.toNanos();
    }

    public void iterate() {
        updateMachine(null);
    }

    public void iterate(@NonNull final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent) {
        updateMachine(nextNanoTimedMouseEvent);
    }

    private void updateMachine(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        final InputAlphabet input = determineInputAlphabet(nextEvent);
        switch (currentMachineState) {
            case IDLE -> {
                if (input == InputAlphabet.NEW_CLICK_CANDIDATE) {
                    assert nextEvent != null;
                    eventCandidateBuffer = nextEvent;
                    currentMachineState = FSMStates.CLICK_CANDIDATE;
                }
            }
            case CLICK_CANDIDATE -> {
                if (input == InputAlphabet.EMPTY) {
                    assert nextEvent == null;
                    assert eventCandidateBuffer != null;
                    if (doubleClickThresholdExpired(eventCandidateBuffer)) {
                        bufferedCommands.add(MouseClickCommand.singleClickCommand(eventCandidateBuffer));
                        eventCandidateBuffer = null;
                        currentMachineState = FSMStates.IDLE;
                    }
                } else if (input == InputAlphabet.NEW_CLICK_CANDIDATE) {
                    assert nextEvent != null;
                    assert eventCandidateBuffer != null;
                    if (doubleClickThresholdExpired(eventCandidateBuffer)) {
                        bufferedCommands.add(MouseClickCommand.singleClickCommand(eventCandidateBuffer));
                        eventCandidateBuffer = nextEvent;
                        currentMachineState = FSMStates.CLICK_CANDIDATE;
                    } else {
                        if (eventCandidateBuffer.getEvent().getButton().equals(nextEvent.getEvent().getButton())) {
                            bufferedCommands.add(MouseClickCommand.doubleClickCommand(nextEvent));
                            eventCandidateBuffer = null;
                            currentMachineState = FSMStates.IDLE;
                        } else {
                            bufferedCommands.add(MouseClickCommand.singleClickCommand(eventCandidateBuffer));
                            eventCandidateBuffer = nextEvent;
                            currentMachineState = FSMStates.CLICK_CANDIDATE;
                        }
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
    private InputAlphabet determineInputAlphabet(@Nullable final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent) {
        if (nextNanoTimedMouseEvent == null) {
            return InputAlphabet.EMPTY;
        } else {
            return InputAlphabet.NEW_CLICK_CANDIDATE;
        }
    }

}
