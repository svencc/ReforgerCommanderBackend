package com.recom.tacview.engine.input.command.mapper.mouse.fsm;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.mouse.IsMouseCommand;
import com.recom.tacview.engine.input.command.mouse.MouseButtonCommand;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.LinkedList;

@Slf4j
public class MouseButtonEventFSM0 implements IsMouseButtonEventFSM {

    @NonNull
    private final long doubleClickThresholdNanos;
    @NonNull
    private final long dragThresholdNanos;

    @NonNull
    private FSMStates currentMachineState = FSMStates.IDLE;

    @Nullable
    private NanoTimedEvent<MouseEvent> eventCandidateBuffer;

    @Getter
    @NonNull
    private final LinkedList<IsMouseCommand> bufferedCommands = new LinkedList<>();


    public MouseButtonEventFSM0(@NonNull final Duration doubleClickThreshold) {
        this.doubleClickThresholdNanos = doubleClickThreshold.toNanos();
        this.dragThresholdNanos = Duration.ofMillis(200).toNanos();
    }

    @Override
    public void start() {
        currentMachineState = FSMStates.IDLE;
    }

    @Override
    public void stop() {
        currentMachineState = FSMStates.STOPPED;
    }

    public void iterate() {
        engineRevolution(null);
    }

    public void iterate(@NonNull final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent) {
        engineRevolution(nextNanoTimedMouseEvent);
    }

    private void engineRevolution(@Nullable final NanoTimedEvent<MouseEvent> nextEvent) {
        final InputAlphabet input = determineInputAlphabet(nextEvent);
//        if (nextEvent != null && !input.equals(InputAlphabet.UNHANDLED)) {
//            log.info("Next Event: {}", nextEvent.getEvent().toString());
//        }
        switch (currentMachineState) {
            case IDLE -> {
                if (input == InputAlphabet.MOUSE_PRESSED) {
                    assert nextEvent != null;
                    eventCandidateBuffer = nextEvent;
                    currentMachineState = FSMStates.CLICK_CANDIDATE;
                }
            }
            case CLICK_CANDIDATE -> {
                if (input == InputAlphabet.IDLEING) {
                    assert nextEvent == null;
                    assert eventCandidateBuffer != null;
                    if (clickThresholdExpired(eventCandidateBuffer)) {
                        bufferedCommands.add(MouseButtonCommand.singleClickCommand(eventCandidateBuffer));
                        eventCandidateBuffer = null;
                        currentMachineState = FSMStates.MOUSE_DRAGGING;
                    }
                } else if (input == InputAlphabet.MOUSE_RELEASED) {
                    assert nextEvent != null;
                    if (eventCandidateBuffer != null && !clickThresholdExpired(eventCandidateBuffer)) {
                        if (eventCandidateBuffer.getEvent().getButton().equals(nextEvent.getEvent().getButton())) {
                            bufferedCommands.add(MouseButtonCommand.singleClickCommand(eventCandidateBuffer));
                            eventCandidateBuffer = null;
                            currentMachineState = FSMStates.IDLE;
                        } else {
                            // @TODO: ?!
                            // anderer Button gedrückt -> ignorieren ! ?
                            // ansonsten bräuchte buffers für jeden button!!!!
                        }
                    }
                } else if (input == InputAlphabet.MOUSE_PRESSED) {
                    assert nextEvent != null;
                    assert eventCandidateBuffer != null;
                    if (clickThresholdExpired(eventCandidateBuffer)) {
                        bufferedCommands.add(MouseButtonCommand.singleClickCommand(eventCandidateBuffer));
                        eventCandidateBuffer = nextEvent;
                        currentMachineState = FSMStates.CLICK_CANDIDATE;
                    } else {
                        if (eventCandidateBuffer.getEvent().getButton().equals(nextEvent.getEvent().getButton())) {
                            bufferedCommands.add(MouseButtonCommand.doubleClickCommand(nextEvent));
                            eventCandidateBuffer = null;
                            currentMachineState = FSMStates.IDLE;
                        } else {
                            bufferedCommands.add(MouseButtonCommand.singleClickCommand(eventCandidateBuffer));
                            eventCandidateBuffer = nextEvent;
                            currentMachineState = FSMStates.CLICK_CANDIDATE;
                        }
                    }
                }
            }
            case MOUSE_DRAGGING -> {
                log.info("Mouse drag entered");
            }
            default -> {
                throw new IllegalStateException(String.format("Unexpected Transition via State:%1s -> Input:%2s", currentMachineState, input));
            }
        }
    }

    private boolean clickThresholdExpired(@NonNull final NanoTimedEvent<MouseEvent> event) {
        return (System.nanoTime() - event.getNanos()) > doubleClickThresholdNanos;
    }

    private boolean dragThresholdExpired(@NonNull final NanoTimedEvent<MouseEvent> event) {
        return (System.nanoTime() - event.getNanos()) > doubleClickThresholdNanos;
    }

    @NonNull
    private InputAlphabet determineInputAlphabet(@Nullable final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent) {
        if (nextNanoTimedMouseEvent == null) {
            return InputAlphabet.IDLEING;
        } else {
            final EventType<? extends MouseEvent> eventType = nextNanoTimedMouseEvent.getEvent().getEventType();

            if (eventType == MouseEvent.MOUSE_PRESSED) {
                return InputAlphabet.MOUSE_PRESSED;
            }
        }

        return InputAlphabet.UNHANDLED;
    }

}
