package lib.gecom.observer;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Subject<NOTE_TYPE> implements Subjective<NOTE_TYPE>, AutoCloseable {

    @NonNull
    private final List<Observing<NOTE_TYPE>> observersWatchingMe = new ArrayList<>();


    @Override
    public void beObservedBy(final @NonNull Observing<NOTE_TYPE> observer) {
        observersWatchingMe.add(observer);
    }

    @Override
    public void observationStoppedThrough(final @NonNull Observing<NOTE_TYPE> observer) {
        observersWatchingMe.remove(observer);
    }

    @Override
    public void notifyObserversWith(@NonNull final Note<NOTE_TYPE> note) {
        observersWatchingMe.forEach(observer -> observer.takeNotice(this, note));
    }

    @Override
    public void reportMyDeath() {
        observersWatchingMe.forEach(observer -> observer.takeDeathNoticeFrom(this));
    }

    @Override
    public void close() throws Exception {
        reportMyDeath();
    }

}
