package com.recom.tacview.engine.input.command.mapper.mouse.fsm;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.mouse.IsMouseCommand;
import com.recom.tacview.engine.input.command.mouse.MouseButtonCommand;
import com.recom.tacview.engine.input.command.mouse.MouseDragCommand;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.LinkedList;

@Slf4j
public class MouseButtonEventFSM1 implements IsMouseButtonEventFSM {

    @NonNull
    private final long doubleClickThresholdNanos;
    @NonNull
    private final long dragThresholdNanos;
    @NonNull
    private final MouseButton responsibleForMouseButton;

    @NonNull
    private FSMStates currentMachineState = FSMStates.NEW;

    @Nullable
    private NanoTimedEvent<MouseEvent> clickCandidateBuffer;

    @Getter
    @NonNull
    private final LinkedList<IsMouseCommand> bufferedCommands = new LinkedList<>();


    public MouseButtonEventFSM1(
            @NonNull final Duration doubleClickThreshold,
            @NonNull final Duration dragThreshold,
            @NonNull final MouseButton responsibleForMouseButton
    ) {
        this.doubleClickThresholdNanos = doubleClickThreshold.toNanos();
        this.dragThresholdNanos = dragThreshold.toNanos();
        this.responsibleForMouseButton = responsibleForMouseButton;
    }

    public void iterate() {
        machineRevolution(null);
    }

    public void iterate(@NonNull final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent) {
        machineRevolution(nextNanoTimedMouseEvent);
    }

    @Override
    public void start() {
        currentMachineState = FSMStates.IDLE;
    }

    @Override
    public void stop() {
        currentMachineState = FSMStates.STOPPED;
    }

    private void machineRevolution(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        if (fsmIsNotResponsibleForMouseButtonRelatedEvents(nextEvent)) {
            return;
        }

        switch (currentMachineState) {
            case IDLE -> {
                switch (idleAlphabet(nextEvent)) {
                    case IDLEING -> {
                        // do nothing
                    }
                    case MOUSE_PRESSED -> {
                        assert nextEvent != null;

                        clickCandidateBuffer = nextEvent;
                        currentMachineState = FSMStates.CLICK_CANDIDATE;
                    }
                }
            }
            case CLICK_CANDIDATE -> {
                assert clickCandidateBuffer != null;
                switch (clickCandidateAlphabet(nextEvent, clickCandidateBuffer)) {
                    case CLICK_THRESHOLD_EXCEEDED -> {
                        bufferedCommands.add(MouseButtonCommand.singleClickCommand(clickCandidateBuffer));
                        clickCandidateBuffer = null;
                        currentMachineState = FSMStates.IDLE;
                    }
                    case MOUSE_PRESSED -> {
                        assert nextEvent != null;

                        bufferedCommands.add(MouseButtonCommand.doubleClickCommand(nextEvent));
                        clickCandidateBuffer = null;
                        currentMachineState = FSMStates.IDLE;
                    }
                    case MOUSE_DRAG_STARTED -> {
                        assert clickCandidateBuffer != null;
                        assert clickCandidateBuffer.getEvent() instanceof MouseDragEvent;

                        bufferedCommands.add(MouseDragCommand.dragStartCommand(clickCandidateBuffer.cast()));
                        clickCandidateBuffer = null;
                        currentMachineState = FSMStates.MOUSE_DRAGGING;
                    }
                    case IDLEING -> {
                        // do nothing
                    }
                }
            }
            case MOUSE_DRAGGING -> {
                switch (clickCandidateAlphabet(nextEvent, clickCandidateBuffer)) {
                    case IDLEING -> {
                        // do nothing
                    }
                    case MOUSE_DRAGGING -> {

                    }
                    case MOUSE_RELEASED -> {
                        assert clickCandidateBuffer.getEvent() instanceof MouseDragEvent;

                        bufferedCommands.add(MouseDragCommand.dragStopCommand(clickCandidateBuffer.cast()));
                        clickCandidateBuffer = null;
                        currentMachineState = FSMStates.IDLE;
                    }
                }
            }
            default -> {
                throw new IllegalStateException(String.format("Unexpected Transition via State:%1s -> Input:%2s", currentMachineState, nextEvent));
            }
        }
    }

    private boolean fsmIsNotResponsibleForMouseButtonRelatedEvents(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        return nextEvent != null && (nextEvent.getEvent().getButton() != responsibleForMouseButton);
    }

    @NonNull
    private InputAlphabet idleAlphabet(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        if (nextEvent == null) {
            return InputAlphabet.IDLEING;
        } else if (nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
            return InputAlphabet.MOUSE_PRESSED;
        } else {
            return InputAlphabet.UNHANDLED;
        }
    }

    @NonNull
    private InputAlphabet clickCandidateAlphabet(
            @Nullable final NanoTimedEvent<MouseEvent> nextEvent,
            @NonNull NanoTimedEvent<MouseEvent> clickCandidateBuffer
    ) {
        if (nextEvent == null && clickThresholdExceeded(clickCandidateBuffer)) {
            return InputAlphabet.CLICK_THRESHOLD_EXCEEDED;
        } else if (nextEvent == null && dragThresholdExceeded(clickCandidateBuffer) && clickCandidateBuffer.getEvent() instanceof MouseDragEvent) {
            return InputAlphabet.MOUSE_DRAG_STARTED;
        } else if (nextEvent != null && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
            return InputAlphabet.MOUSE_PRESSED;
        } else {
            return InputAlphabet.IDLEING;
        }
    }

    private boolean clickThresholdExceeded(@NonNull final NanoTimedEvent<MouseEvent> event) {
        return (System.nanoTime() - event.getNanos()) > doubleClickThresholdNanos;
    }

    private boolean dragThresholdExceeded(@NonNull final NanoTimedEvent<MouseEvent> event) {
        return (System.nanoTime() - event.getNanos()) > dragThresholdNanos;
    }

}
