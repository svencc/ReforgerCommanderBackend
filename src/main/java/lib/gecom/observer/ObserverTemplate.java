package lib.gecom.observer;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ObserverTemplate<NOTE_TYPE> implements Observing<NOTE_TYPE> {

    @NonNull
    protected final List<Subject<NOTE_TYPE>> subjects = new ArrayList<>();

    @Override
    public void observe(@NonNull final Subject<NOTE_TYPE> subject) {
        subject.beObservedBy(this);
        subjects.add(subject);
    }

    @Override
    public abstract void takeNotice(
            @NonNull final Subject<NOTE_TYPE> subject,
            @NonNull final Note<NOTE_TYPE> event
    );

    @Override
    public void takeDeathNoticeFrom(final @NonNull Subject<NOTE_TYPE> subject) {
        subjects.remove(subject);
    }

    public void close() {
        subjects.forEach(subject -> subject.observationStoppedThrough(this));
        subjects.clear();
    }

}
