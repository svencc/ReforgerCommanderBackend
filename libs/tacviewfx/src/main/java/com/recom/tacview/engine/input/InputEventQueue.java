package com.recom.tacview.engine.input;

import javafx.scene.input.InputEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Slf4j
@Service
@RequiredArgsConstructor
public class InputEventQueue {

    @NonNull
    private final LinkedList<InputEvent> inputQueue = new LinkedList<>();


    @NonNull
    public InputEvent dequeueEvent() {
        if (inputQueue.isEmpty()) {
            throw new IllegalStateException("Input queue is empty");
        } else {
            return inputQueue.poll();
        }
    }

    public void enqueueEvent(@NonNull final InputEvent inputEvent) {
        inputQueue.add(inputEvent);
    }

    public void clearEvents() {
        inputQueue.clear();
    }

    public boolean hasEvents() {
        return !inputQueue.isEmpty();
    }

    public long queueSize() {
        return inputQueue.size();
    }

}
