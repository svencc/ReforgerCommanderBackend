package com.recom.commons.calculator;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForShadedMap;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeAndAspectMap;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.map.rasterizer.mapdesignscheme.ReforgerMapDesignScheme;
import org.junit.jupiter.api.Test;

class D8AlgorithmForShadedMapTest {

    @Test
    void calculateShadedMap() {
        // ARRANGE
        final ReforgerMapDesignScheme mapScheme = new ReforgerMapDesignScheme();
        final D8AlgorithmForSlopeAndAspectMap slopeAndAspectAlgorithm = new D8AlgorithmForSlopeAndAspectMap(1.0);
        final D8AlgorithmForShadedMap shadedMapAlgorithm = new D8AlgorithmForShadedMap();
        final float[][] dem = {
                {400, 400, 400, 400, 400},
                {400, 450, 430, 400, 400},
                {400, 450, 430, 400, 400},
                {400, 400, 400, 400, 400},
                {400, 400, 400, 400, 400}
        };

        // ACT
        final SlopeAndAspect[][] slopeAndAspectsMap = slopeAndAspectAlgorithm.generateSlopeAndAspectMap(dem);
        shadedMapAlgorithm.generateShadedMap(slopeAndAspectsMap, new ReforgerMapDesignScheme());
        int[][] shadedMapToTest = shadedMapAlgorithm.generateShadedMap(slopeAndAspectsMap, mapScheme);

        // ASSERT
        for (int x = 0; x < dem.length; x++) {
            System.out.print("{");
            for (int y = 0; y < dem[0].length; y++) {
                System.out.print(shadedMapToTest[x][y] + ", ");
            }
            System.out.print("}");
            System.out.println();
        }
    }

}