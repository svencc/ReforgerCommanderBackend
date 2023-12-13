package com.recom.dynamicproperties;

import com.recom.observer.HasSubject;
import com.recom.observer.Notification;
import com.recom.observer.Subject;
import lombok.Getter;
import lombok.NonNull;

public abstract class ObservableDynamicUserProperties<T extends ObservableDynamicUserProperties> extends DynamicUserProperties implements HasSubject<T> {

    @Getter
    @NonNull
    private final Subject<T> subject = new Subject<>();

    @Override
    public void persist() {
        super.persist();
        subject.notifyObserversWith(new Notification<>((T) this));
    }

    @Override
    public void load() {
        super.load();
        subject.notifyObserversWith(new Notification<>((T) this));
    }

}
