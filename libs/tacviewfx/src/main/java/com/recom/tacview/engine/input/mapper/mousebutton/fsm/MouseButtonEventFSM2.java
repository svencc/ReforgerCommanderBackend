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
public class MouseButtonEventFSM2 implements IsMouseButtonEventFSM {

    @NonNull
    private final long doubleClickThresholdNanos;
    @NonNull
    private final long dragToClickThresholdNanos;
    @NonNull
    private final MouseButton responsibleForMouseButton;
    @NonNull
    private FSMStates2 currentMachineState = FSMStates2.NEW;
    @NonNull
    private final MouseEventBuffer mouseEventBuffer = new MouseEventBuffer();
    @NonNull
    private final LinkedList<IsMouseCommand<MouseEvent>> bufferedCommands = new LinkedList<>();


    public MouseButtonEventFSM2(
            @NonNull final Duration doubleClickThresholdDuration,
            @NonNull final Duration dragToClickThresholdDuration,
            @NonNull final MouseButton responsibleForMouseButton
    ) {
        this.doubleClickThresholdNanos = doubleClickThresholdDuration.toNanos();
        this.dragToClickThresholdNanos = dragToClickThresholdDuration.toNanos();
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
        currentMachineState = FSMStates2.IDLE;
    }

    @Override
    public void stop() {
        currentMachineState = FSMStates2.STOPPED;
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
                        assert nextEvent == null;
                        // do nothing
                    }
                    case MOUSE_PRESSED__DRAG_START -> {
                        assert nextEvent != null;

                        mouseEventBuffer.lastMouseEventBuffer = nextEvent;
                        mouseEventBuffer.mouseDragOriginEventBuffer = nextEvent;
                        currentMachineState = FSMStates2.MOUSE_DRAGGING;

                        bufferedCommands.add(MouseDragCommand.dragStartCommand(mouseEventBuffer.mouseDragOriginEventBuffer));
                    }
                }
            }
            case MOUSE_DRAGGING -> {
                switch (mouseDraggingAlphabet(nextEvent)) {
                    case MOUSE_DRAGGING -> {
                        assert mouseEventBuffer.lastMouseEventBuffer != null;
                        assert mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED || mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_DRAGGED;
                        assert mouseEventBuffer.mouseDragOriginEventBuffer != null;
                        assert mouseEventBuffer.mouseDragOriginEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED;

                        if (nextEvent == null) {
                            assert nextEvent == null;

                            bufferedCommands.add(MouseDragCommand.dragginCommand(mouseEventBuffer.mouseDragOriginEventBuffer, mouseEventBuffer.lastMouseEventBuffer));
                        } else if (mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
                            assert nextEvent != null;
                            assert mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED;

                            mouseEventBuffer.lastMouseEventBuffer = nextEvent;
                            bufferedCommands.add(MouseDragCommand.dragginCommand(mouseEventBuffer.mouseDragOriginEventBuffer, mouseEventBuffer.lastMouseEventBuffer));
                        } else if (mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_DRAGGED) {
                            assert nextEvent != null;
                            assert mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType() == MouseEvent.MOUSE_DRAGGED;

                            mouseEventBuffer.lastMouseEventBuffer = nextEvent;
                            bufferedCommands.add(MouseDragCommand.dragginCommand(mouseEventBuffer.mouseDragOriginEventBuffer, mouseEventBuffer.lastMouseEventBuffer));
                        }
                    }
                    case MOUSE_RELEASED__DRAG_STOPPED -> {
                        assert nextEvent != null;
                        assert nextEvent.getEvent().getEventType().equals(MouseEvent.MOUSE_RELEASED);
                        assert mouseEventBuffer.lastMouseEventBuffer != null;
                        assert mouseEventBuffer.mouseDragOriginEventBuffer != null;

                        final MouseDragCommand draggedStopCommand = MouseDragCommand.dragStopCommand(mouseEventBuffer.mouseDragOriginEventBuffer, nextEvent);
                        bufferedCommands.add(draggedStopCommand);

                        mouseEventBuffer.timeBetweenDragStartAndDragStop = draggedStopCommand.timeBetweenDragStartAndDragStop();
                        mouseEventBuffer.clearAllBuffers();
                        mouseEventBuffer.lastMouseEventBuffer = nextEvent;

                        currentMachineState = FSMStates2.CLICK_CANDIDATE;
                    }
                }
            }
            case CLICK_CANDIDATE -> {
                assert mouseEventBuffer.lastMouseEventBuffer != null;
                switch (clickCandidateAlphabet(nextEvent, mouseEventBuffer.lastMouseEventBuffer)) {
                    case CLICK -> {
                        assert nextEvent != null;
                        assert nextEvent.getEvent().getEventType().equals(MouseEvent.MOUSE_RELEASED);
                        assert mouseEventBuffer.lastMouseEventBuffer != null;
                        assert mouseEventBuffer.lastMouseEventBuffer.getEvent().getEventType().equals(MouseEvent.MOUSE_RELEASED);

                        long timeBetweenDragStartAndDragStop = mouseEventBuffer.timeBetweenDragStartAndDragStop;
                        bufferedCommands.add(MouseButtonCommand.singleClickCommand(mouseEventBuffer.lastMouseEventBuffer, timeBetweenDragStartAndDragStop, dragToClickThresholdNanos));

                        currentMachineState = FSMStates2.IDLE;
                    }
                    case DOUBLECLICK -> {
                        assert nextEvent != null;

                        bufferedCommands.add(MouseButtonCommand.doubleClickCommand(nextEvent));

                        currentMachineState = FSMStates2.IDLE;
                    }
                    case IDLEING -> {
                        // do nothing
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
    private InputAlphabet2 idleAlphabet(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        // ich muss hier unterscheiden - für einen click muss ich auf ein mouse release warten
        // das mouse dragging muss mit mouse_pressed arbeiten! der threshold kann dann eigentlich weg!
        if (nextEvent == null) {
            return InputAlphabet2.IDLEING;
        } else if (nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
            return InputAlphabet2.MOUSE_PRESSED__DRAG_START;
        } else {
            return InputAlphabet2.IDLEING;
        }
    }

    @NonNull
    private InputAlphabet2 clickCandidateAlphabet(
            @Nullable final NanoTimedEvent<MouseEvent> nextEvent,
            @NonNull final NanoTimedEvent<MouseEvent> clickCandidateBuffer
    ) {
        if (clickThresholdExceeded(clickCandidateBuffer)) {
            return InputAlphabet2.CLICK;
        } else if (nextEvent != null && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
            return InputAlphabet2.DOUBLECLICK;
        } else {
            return InputAlphabet2.IDLEING;
        }
    }

    @NonNull
    private InputAlphabet2 mouseDraggingAlphabet(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        if (nextEvent != null && nextEvent.getEvent().getEventType() == MouseEvent.MOUSE_RELEASED) {
            return InputAlphabet2.MOUSE_RELEASED__DRAG_STOPPED;
        } else {
            return InputAlphabet2.MOUSE_DRAGGING;
        }
    }

    private boolean clickThresholdExceeded(@NonNull final NanoTimedEvent<MouseEvent> clickCandidateBuffer) {
        if (clickCandidateBuffer.getEvent().getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            return (System.nanoTime() - clickCandidateBuffer.getNanos()) > doubleClickThresholdNanos;
        } else {
            return false;
        }
    }

    private class MouseEventBuffer {

        public long timeBetweenDragStartAndDragStop;
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

        public void clearMouseDragOriginEventBuffer() {
            mouseDragOriginEventBuffer = null;
        }
    }

}
