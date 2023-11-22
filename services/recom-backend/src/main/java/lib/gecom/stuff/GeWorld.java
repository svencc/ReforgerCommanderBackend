package lib.gecom.stuff;

import lombok.Getter;

public final class GeWorld {

    @Getter
    private static final GeWorld instance = new GeWorld();

    @Getter
    private final GeWorldStates worldStates;

    private GeWorld() {
        worldStates = new GeWorldStates();
    }

}
