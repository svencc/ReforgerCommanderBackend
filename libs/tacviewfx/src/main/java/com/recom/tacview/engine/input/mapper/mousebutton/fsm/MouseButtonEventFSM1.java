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

    private final long doubleClickThresholdNanos;
    private final long dragThresholdNanos;
    @NonNull
    private final MouseButton responsibleForMouseButton;
    @NonNull
    private FSMStates1 currentMachineState = FSMStates1.NEW;
    @NonNull
    private final MouseEventBuffer mouseEventBuffer = new MouseEventBuffer();
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
        currentMachineState = FSMStates1.IDLE;
    }

    @Override
    public void stop() {
        currentMachineState = FSMStates1.STOPPED;
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

        boolean reEvaluate = false;
        do {
            reEvaluate = false;
            switch (currentMachineState) {
                case IDLE -> {
                    switch (idleAlphabet(nextEvent)) {
                        case IDLEING -> {
                            // do nothing
                        }
                        case MOUSE_PRESSED -> {
                            assert nextEvent != null;

                            mouseEventBuffer.lastMouseEventBuffer = nextEvent;
                            currentMachineState = FSMStates1.CLICK_CANDIDATE;
                            if (dragThresholdNanos < 0) {
                                reEvaluate = true;
                            }
                        }
                    }
                }
                case CLICK_CANDIDATE -> {
                    assert mouseEventBuffer.lastMouseEventBuffer != null;
                    switch (clickCandidateAlphabet(nextEvent, mouseEventBuffer.lastMouseEventBuffer)) {
                        case MOUSE_RELEASED -> {
                            assert nextEvent != null;

                            mouseEventBuffer.lastMouseEventBuffer = nextEvent;
                        }
                        case CLICK -> {
                            assert nextEvent != null;

                            bufferedCommands.add(MouseButtonCommand.singleClickCommand(mouseEventBuffer.lastMouseEventBuffer, 0, 0));
                            mouseEventBuffer.clearLastMouseEventBuffer();
                            currentMachineState = FSMStates1.IDLE;
                        }
                        case DOUBLECLICK -> {
                            assert nextEvent != null;

                            bufferedCommands.add(MouseButtonCommand.doubleClickCommand(nextEvent));
                            mouseEventBuffer.clearLastMouseEventBuffer();
                            currentMachineState = FSMStates1.IDLE;
                        }
                        case MOUSE_DRAG_STARTED -> {
                            assert nextEvent == null;
                            assert mouseEventBuffer.lastMouseEventBuffer != null;
                            assert mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType().equals(MouseEvent.MOUSE_PRESSED);

                            mouseEventBuffer.mouseDragOriginEventBuffer = mouseEventBuffer.lastMouseEventBuffer;
                            bufferedCommands.add(MouseDragCommand.dragStartCommand(mouseEventBuffer.mouseDragOriginEventBuffer));
                            currentMachineState = FSMStates1.MOUSE_DRAGGING;
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
                            assert mouseEventBuffer.lastMouseEventBuffer != null;
                            assert mouseEventBuffer.mouseDragOriginEventBuffer != null;

                            // emit last known mouse drag command
                            bufferedCommands.add(MouseDragCommand.draggingCommand(mouseEventBuffer.mouseDragOriginEventBuffer, mouseEventBuffer.lastMouseEventBuffer));
                        }
                        case MOUSE_DRAGGING -> {
                            assert nextEvent != null;
                            assert mouseEventBuffer.lastMouseEventBuffer != null;
                            assert mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED || mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_DRAGGED;
                            assert mouseEventBuffer.mouseDragOriginEventBuffer != null;
                            assert mouseEventBuffer.mouseDragOriginEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED;

                            mouseEventBuffer.lastMouseEventBuffer = nextEvent;
                            bufferedCommands.add(MouseDragCommand.draggingCommand(mouseEventBuffer.mouseDragOriginEventBuffer, nextEvent));
                        }
                        case MOUSE_RELEASED -> {
                            assert nextEvent != null;
                            assert nextEvent.getEvent().getEventType().equals(MouseEvent.MOUSE_RELEASED);
                            assert mouseEventBuffer.lastMouseEventBuffer != null;
                            assert mouseEventBuffer.mouseDragOriginEventBuffer != null;

                            bufferedCommands.add(MouseDragCommand.dragStopCommand(mouseEventBuffer.mouseDragOriginEventBuffer, nextEvent));
                            mouseEventBuffer.clearAllBuffers();
                            currentMachineState = FSMStates1.IDLE;
                        }
                    }
                }
                default -> {
                    throw new IllegalStateException(String.format("Unexpected Transition via State:%1s -> Input:%2s", currentMachineState, nextEvent));
                }
            }
        } while (reEvaluate);
    }

    private boolean fsmIsNotResponsibleForMouseButtonRelatedEvents(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        return nextEvent != null && (nextEvent.getEvent().getButton() != responsibleForMouseButton);
    }

    @NonNull
    private InputAlphabet1 idleAlphabet(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        if (nextEvent == null) {
            return InputAlphabet1.IDLEING;
        } else if (nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
            return InputAlphabet1.MOUSE_PRESSED;
        } else {
            return InputAlphabet1.IDLEING;
        }
    }

    @NonNull
    private InputAlphabet1 clickCandidateAlphabet(
            @Nullable final NanoTimedEvent<MouseEvent> nextEvent,
            @NonNull final NanoTimedEvent<MouseEvent> clickCandidateBuffer
    ) {
        if (clickThresholdExceeded(clickCandidateBuffer)) {
            return InputAlphabet1.CLICK;
        } else if (dragThresholdExceeded(clickCandidateBuffer) && clickCandidateBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
            return InputAlphabet1.MOUSE_DRAG_STARTED;
        } else if (nextEvent != null && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
            return InputAlphabet1.DOUBLECLICK;
        } else if (nextEvent != null && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_RELEASED) {
            return InputAlphabet1.MOUSE_RELEASED;
        } else {
            return InputAlphabet1.IDLEING;
        }
    }

    @NonNull
    private InputAlphabet1 mouseDraggingAlphabet(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        if (nextEvent == null) {
            return InputAlphabet1.IDLEING;
        } else if (nextEvent != null && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_RELEASED) {
            return InputAlphabet1.MOUSE_RELEASED;
        } else if (nextEvent != null && nextEvent.getEvent() instanceof MouseEvent && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_DRAGGED) {
            return InputAlphabet1.MOUSE_DRAGGING;
        } else {
            return InputAlphabet1.IDLEING;
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

    private class MouseEventBuffer {

        @Nullable
        NanoTimedEvent<MouseEvent> lastMouseEventBuffer;
        @Nullable
        NanoTimedEvent<MouseEvent> mouseDragOriginEventBuffer;

        protected void clearAllBuffers() {
            lastMouseEventBuffer = null;
            mouseDragOriginEventBuffer = null;
        }

        protected void clearLastMouseEventBuffer() {
            lastMouseEventBuffer = null;
        }

    }

}
