package lib.gecom;

import lombok.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GePlannerTest {

    @Test
    void plan() {
        // Arrange
        final GePlanner planner = new GePlanner();
        @NonNull final List<GeAction> possibleActions = new ArrayList<>();
        @NonNull final HashMap<String, Integer> goal = new HashMap<>();
        @NonNull final GeWorldStates worldStates = new GeWorldStates();

        // Act
        final Optional<Queue<GeAction>> planToTest = planner.plan(possibleActions, goal, worldStates);

        // Assert
        assertTrue(planToTest.isEmpty());
    }

}