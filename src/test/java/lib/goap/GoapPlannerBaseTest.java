package lib.goap;

import lib.goap.action.GoapActionBase;
import lib.goap.planner.GoapPlanner;
import lib.goap.planner.GoapPlannerable;
import lib.goap.testimplementations.TestUnit;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GoapPlannerBaseTest {

    // No Connections equals a null pointer.
    @Test
    public void noConnectionsNullPointer() {
        // Act & Assert
        assertEquals(null, getTestingGoapPlanner().planActions(new TestUnit()));
    }

    /**
     * @return the IGoapPlanner that is currently being tested.
     */
    public static GoapPlannerable getTestingGoapPlanner() {
        return new GoapPlanner();
    }

    // Connection between start and nodeOne leads to no goal
    @Test
    public void connectionAndNoGoal() {
        // Arrange
        final TestUnit tUnit = generateBaseTestUnit();
        tUnit.addAA(tUnit.tOne);

        // Act & Assert
        assertEquals(null, getTestingGoapPlanner().planActions(tUnit));
    }

    public static TestUnit generateBaseTestUnit() {
        final TestUnit tUnit = new TestUnit();
        tUnit.addWS(tUnit.worldS);
        tUnit.addGS(tUnit.goalS);

        return tUnit;
    }

    // No connection between node two and the start
    @Test
    public void noConnectionAndNoGoal() {
        // Arrange
        final TestUnit tUnit = generateBaseTestUnit();
        tUnit.addAA(tUnit.tTwo);

        // Act & Assert
        assertEquals(null, getTestingGoapPlanner().planActions(tUnit));
    }

    // Single node connected from start to goal
    @Test
    public void singleConnectionStartAndGoal() {
        // Arrange
        final TestUnit tUnit = generateBaseTestUnit();
        tUnit.addAA(tUnit.tThree);

        // Act & Assert
        assertNotEquals(null, getTestingGoapPlanner().planActions(tUnit));
        assertEquals(1, getTestingGoapPlanner().planActions(tUnit).size());
    }

    // Connection between two nodes from start to goal
    @Test
    public void doubleConnectionStartAndGoal() {
        // Arrange
        final TestUnit tUnit = generateBaseTestUnit();
        tUnit.addAA(tUnit.tOne);
        tUnit.addAA(tUnit.tTwo);

        // Act & Assert
        assertNotEquals(null, getTestingGoapPlanner().planActions(tUnit));
        assertEquals(2, getTestingGoapPlanner().planActions(tUnit).size());
    }

    // Shortest path with all three nodes
    @Test
    public void tripleVertexSingleConnectionStartAndGoal() {
        // Arrange
        final TestUnit tUnit = generateBaseTestUnit();
        tUnit.addAA(tUnit.tOne);
        tUnit.addAA(tUnit.tTwo);
        tUnit.addAA(tUnit.tThree);

        // Act & Assert
        assertNotEquals(null, getTestingGoapPlanner().planActions(tUnit));
        assertEquals(1, getTestingGoapPlanner().planActions(tUnit).size());
    }

    // Connection Test
    @Test
    public void correctActionsUsedDoubleConnection() {
        // Arrange
        final TestUnit tUnit = generateBaseTestUnit();
        tUnit.addAA(tUnit.tOne);
        tUnit.addAA(tUnit.tTwo);

        // Act
        final Queue<GoapActionBase> q = getTestingGoapPlanner().planActions(tUnit);

        // Assert
        // tOne -> tTwo -> end
        assertEquals(tUnit.tOne, q.poll());
        assertEquals(tUnit.tTwo, q.poll());
        assertEquals(0, q.size());
    }

    // Connection Test
    @Test
    public void correctActionsUsedSingleConnection() {
        // Arrange
        final TestUnit tUnit = generateBaseTestUnit();
        tUnit.addAA(tUnit.tOne);
        tUnit.addAA(tUnit.tTwo);
        tUnit.addAA(tUnit.tThree);

        // Act
        final Queue<GoapActionBase> q = getTestingGoapPlanner().planActions(tUnit);

        // Assert
        // tOne -> tTwo -> end
        // tThree -> end
        assertEquals(tUnit.tThree, q.poll());
        assertEquals(0, q.size());
    }
}
