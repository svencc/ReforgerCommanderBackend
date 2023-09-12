package lib.gecom.action;

import lombok.NonNull;

import java.util.HashMap;

public interface GeActionable {

    String getName();

    Float getCost();

    boolean isAchievable();

    boolean arePreconditionsMet(@NonNull final HashMap<String, Integer> state);

}
