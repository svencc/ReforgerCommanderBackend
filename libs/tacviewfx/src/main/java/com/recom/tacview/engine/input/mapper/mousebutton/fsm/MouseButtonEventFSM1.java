package com.recom.tacview.engine.input.mapper.mousebutton.fsm;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.mousebutton.IsMouseCommand;
import com.recom.tacview.engine.input.command.mousebutton.MouseButtonCommand;
import com.recom.tacview.engine.input.command.mousebutton.MouseDragCommand;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.LinkedList;
import java.util.stream.Stream;

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
    private NanoTimedEvent<MouseEvent> lastMouseEventBuffer;
    @Nullable
    private NanoTimedEvent<MouseEvent> mouseDragOriginEventBuffer;

    @NonNull
    private final LinkedList<IsMouseCommand<MouseEvent>> bufferedCommands = new LinkedList<>();


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

    @Override
    public boolean hasBufferedCommands() {
        return !bufferedCommands.isEmpty();
    }


    @NonNull
    @Override
    public Stream<IsMouseCommand<MouseEvent>> popBufferedCommands() {
        final LinkedList<IsMouseCommand<MouseEvent>> bufferedCommandsCopy = new LinkedList<>(bufferedCommands);
        bufferedCommands.clear();

        return bufferedCommandsCopy.stream();
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

                        lastMouseEventBuffer = nextEvent;
                        currentMachineState = FSMStates.CLICK_CANDIDATE;
                    }
                }
            }
            case CLICK_CANDIDATE -> {
                assert lastMouseEventBuffer != null;
                switch (clickCandidateAlphabet(nextEvent, lastMouseEventBuffer)) {
                    case MOUSE_RELEASED -> {
                        assert nextEvent != null;

                        lastMouseEventBuffer = nextEvent;
                    }
                    case CLICK -> {
                        bufferedCommands.add(MouseButtonCommand.singleClickCommand(lastMouseEventBuffer));
                        lastMouseEventBuffer = null;
                        currentMachineState = FSMStates.IDLE;
                    }
                    case DOUBLECLICK -> {
                        assert nextEvent != null;

                        bufferedCommands.add(MouseButtonCommand.doubleClickCommand(nextEvent));
                        lastMouseEventBuffer = null;
                        currentMachineState = FSMStates.IDLE;
                    }
                    case MOUSE_DRAG_STARTED -> {
                        assert nextEvent == null;
                        assert lastMouseEventBuffer != null;
                        assert lastMouseEventBuffer.getEvent().getEventType().equals(MouseEvent.MOUSE_PRESSED);

                        mouseDragOriginEventBuffer = lastMouseEventBuffer;
                        bufferedCommands.add(MouseDragCommand.dragStartCommand(mouseDragOriginEventBuffer));
                        currentMachineState = FSMStates.MOUSE_DRAGGING;
                    }
                    case IDLEING -> {
                        // do nothing
                    }
                }
            }
            case MOUSE_DRAGGING -> {
                switch (mouseDraggingAlphabet(nextEvent)) {
                    case IDLEING -> {
                        assert nextEvent == null;
                        assert lastMouseEventBuffer != null;
                        assert mouseDragOriginEventBuffer != null;

                        // emit last known mouse drag command
                        bufferedCommands.add(MouseDragCommand.dragginCommand(lastMouseEventBuffer));
                    }
                    case MOUSE_DRAGGING -> {
                        assert nextEvent != null;
                        assert lastMouseEventBuffer != null;
                        assert lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED || lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_DRAGGED;
                        assert mouseDragOriginEventBuffer != null;
                        assert mouseDragOriginEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED;

                        lastMouseEventBuffer = nextEvent;
                        bufferedCommands.add(MouseDragCommand.dragginCommand(lastMouseEventBuffer));
                    }
                    case MOUSE_RELEASED -> {
                        assert nextEvent != null;
                        assert nextEvent.getEvent().getEventType().equals(MouseEvent.MOUSE_RELEASED);
                        assert lastMouseEventBuffer != null;
                        assert mouseDragOriginEventBuffer != null;

                        bufferedCommands.add(MouseDragCommand.dragStopCommand(nextEvent));
                        lastMouseEventBuffer = null;
                        mouseDragOriginEventBuffer = null;
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
            return InputAlphabet.IDLEING;
        }
    }

    @NonNull
    private InputAlphabet clickCandidateAlphabet(
            @Nullable final NanoTimedEvent<MouseEvent> nextEvent,
            @NonNull final NanoTimedEvent<MouseEvent> clickCandidateBuffer
    ) {
        if (nextEvent == null && clickThresholdExceeded(clickCandidateBuffer)) {
            return InputAlphabet.CLICK;
        } else if (nextEvent == null && dragThresholdExceeded(clickCandidateBuffer) && clickCandidateBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
            return InputAlphabet.MOUSE_DRAG_STARTED;
        } else if (nextEvent != null && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
            return InputAlphabet.DOUBLECLICK;
        } else if (nextEvent != null && nextEvent.getEvent() instanceof MouseEvent && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_RELEASED) {
            return InputAlphabet.MOUSE_RELEASED;
        } else {
            return InputAlphabet.IDLEING;
        }
    }

    @NonNull
    private InputAlphabet mouseDraggingAlphabet(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        if (nextEvent == null) {
            return InputAlphabet.IDLEING;
        } else if (nextEvent != null && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_RELEASED) {
            return InputAlphabet.MOUSE_RELEASED;
        } else if (nextEvent != null && nextEvent.getEvent() instanceof MouseEvent && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_DRAGGED) {
            return InputAlphabet.MOUSE_DRAGGING;
        } else {
            return InputAlphabet.IDLEING;
        }
    }

    private boolean clickThresholdExceeded(@NonNull final NanoTimedEvent<MouseEvent> clickCandidateBuffer) {
        if (clickCandidateBuffer.getEvent().getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            return (System.nanoTime() - clickCandidateBuffer.getNanos()) > doubleClickThresholdNanos;
        } else {
            return false;
        }
    }

    private boolean dragThresholdExceeded(@NonNull final NanoTimedEvent<MouseEvent> event) {
        return (System.nanoTime() - event.getNanos()) > dragThresholdNanos;
    }

}
