package org.cirdles.peakShapes_Tripoli.splineBasis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SplineBasisModelTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void bBase() {
        double[][] vector = { {4}, {1}, {3}, {2}};
        double[][] vector2 = { {3.0, 1.0, 3}};
        SplineBasisModel base = SplineBasisModel.initializeSpline(vector, 2, 3);
        double[][] array = base.multMatrix(vector, vector2);

    }
}