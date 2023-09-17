package lib.gecom.observer;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ObserverTemplate<NOTE_TYPE> implements Observing<NOTE_TYPE> {

    @NonNull
    private final List<Subject<NOTE_TYPE>> subjects = new ArrayList<>();

    @Override
    public void observe(@NonNull final Subject<NOTE_TYPE> subject) {
        subjects.add(subject);
    }

    @Override
    public abstract void takeNotice(
            @NonNull final Subject<NOTE_TYPE> subject,
            @NonNull final Note<NOTE_TYPE> event
    );

    @Override
    public void takeDeadthNoticeFrom(final @NonNull Subject<NOTE_TYPE> subject) {
        subjects.remove(subject);
    }

}
