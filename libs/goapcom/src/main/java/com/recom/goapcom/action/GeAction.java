package com.recom.goapcom.action;

import com.recom.goapcom.GeTargetable;
import com.recom.goapcom.stuff.GeNullTarget;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;

@Getter
public abstract class GeAction {

    @NonNull
    private final String name;

    @NonNull
    private final HashMap<String, Integer> preconditions = new HashMap<>();

    @NonNull
    private final HashMap<String, Integer> effects = new HashMap<>();

    @NonNull
    private Float duration = 0.0f;

    @NonNull
    private Float cost = 1.0f;

    @Getter
    @NonNull
    private GeTargetable target = new GeNullTarget();

    @Setter
    private boolean actionRunning = false;


    public GeAction(@NonNull final String name) {
        this.name = name;
    }

    public GeAction(
            @NonNull final String name,
            @NonNull final Float cost
    ) {
        this.name = name;
        this.cost = cost;
    }

    public boolean isAchievable() {
        return true;
    }

    public boolean arePreconditionsMet(@NonNull final HashMap<String, Integer> state) {
        return preconditions.entrySet().stream()
                .allMatch(entry -> state.containsKey(entry.getKey()) && state.get(entry.getKey()).equals(entry.getValue()));
    }

    public abstract boolean prePerform();

    public abstract boolean postPerform();

}
