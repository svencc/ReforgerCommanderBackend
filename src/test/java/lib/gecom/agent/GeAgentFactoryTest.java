package lib.gecom.agent;

import lib.gecom.plan.GePlanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeAgentFactoryTest {

    @Test
    void createAgent_simpleTest() {
        // Arrange
        final GeAgentFactory geAgentFactoryToTest = new GeAgentFactory();
        final GePlanner gePlanner = new GePlanner();

        // Act
        final GeAgent geAgent = geAgentFactoryToTest.createAgent(gePlanner);

        // Assert
        assertNotNull(geAgent);
    }

}