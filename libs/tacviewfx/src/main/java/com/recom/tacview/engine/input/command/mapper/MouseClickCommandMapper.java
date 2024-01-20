package com.recom.tacview.engine.input.command.mapper;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.MouseClickCommand;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.LinkedList;
import java.util.stream.Stream;

@Slf4j
public class MouseClickCommandMapper implements IsInputCommandMapper {

    @NonNull
    private final LinkedList<NanoTimedEvent<? extends InputEvent>> unprocessedMouseClicks = new LinkedList<>();
    @NonNull
    private final MouseClickMachine fsm = new MouseClickMachine(Duration.ofMillis(250));


    @Override
    public boolean mapEvents(Stream<NanoTimedEvent<? extends InputEvent>> timedMouseEventStream) {
/*
         leerer stream kann hier kommen ...
         wir können als in jedem tick rückwirkend schauen ob es einen unprocessedMouseClickStream eintrag
         dazu schauen wir ob das erste event 200ms alt ist
         wenn ja, dann ist es ein click-kanidat
         wenn es ein zweites, nachfolgendes event gibt welches maximal 200 ms älter ist, dann ist es ein doubleclick-event;
         wird gemapped und die beiden events werden aus der liste entfernt; dann wird der strom rekursiv weiterverarbeitet
         bis er leer ist oder kein klick-kandidadat oder doubleclick-event mehr gefunden wird

         das machen wir mit einem Zustandsautomaten? Ist das nicht zu kompliziert?
         Eignet sich ein Zustandsautomat dafür?
         Zustände: idle/leer, klick-kandidat, doubleclick-kandidat, klick-emitter, doubleclick-emitter
 */
        timedMouseEventStream
                .filter(nanoTimedEvent -> nanoTimedEvent.getEvent().getEventType() == MouseEvent.MOUSE_CLICKED)
                .forEach(unprocessedMouseClicks::add);

        return runMouseClickFSM();
    }

    @SuppressWarnings("unchecked")
    private boolean runMouseClickFSM() {
        while (!unprocessedMouseClicks.isEmpty()) {
            fsm.iterate((NanoTimedEvent<MouseEvent>) unprocessedMouseClicks.poll());
        }
        fsm.iterate();

        return !fsm.getBufferedCommands().isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinkedList<MouseClickCommand> getCreatedCommands() {
        final LinkedList<MouseClickCommand> createdCommands = (LinkedList<MouseClickCommand>) fsm.getBufferedCommands().clone();
        fsm.getBufferedCommands().clear();

        return createdCommands;
    }

}
