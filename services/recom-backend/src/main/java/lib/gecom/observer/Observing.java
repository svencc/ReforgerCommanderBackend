package lib.gecom.observer;

import lombok.NonNull;

public interface Observing<NOTE_TYPE> extends AutoCloseable {

    void observe(@NonNull final Subject<NOTE_TYPE> subject);

    void takeNotice(
            @NonNull final Subject<NOTE_TYPE> subject,
            @NonNull final Note<NOTE_TYPE> note
    );

    void takeDeathNoticeFrom(@NonNull final Subject<NOTE_TYPE> subject);

}
