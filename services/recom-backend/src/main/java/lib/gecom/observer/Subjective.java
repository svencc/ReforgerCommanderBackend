package lib.gecom.observer;

import lombok.NonNull;

public interface Subjective<NOTE_TYPE> {

    void beObservedBy(@NonNull final Observing<NOTE_TYPE> observer);

    void observationStoppedThrough(@NonNull final Observing<NOTE_TYPE> observer);

    void notifyObserversWith(@NonNull final Note<NOTE_TYPE> note);

    void reportMyDeath();

}
