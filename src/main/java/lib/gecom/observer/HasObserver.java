package lib.gecom.observer;

public interface HasObserver<NOTE_TYPE> {

    Observing<NOTE_TYPE> getObserver();

}
