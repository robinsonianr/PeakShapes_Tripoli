package org.cirdles.peakShapes_Tripoli.splineBasis;

import jama.Matrix;
import org.cirdles.peakShapes_Tripoli.matlab.MatLab;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SplineBasisModelTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void bBase() {
        Matrix splineTest = MatLab.linspace(204.8507, 205.1165, 1000);
        SplineBasisModel base = SplineBasisModel.initializeSpline(splineTest, 22, 3);


        System.out.println(Arrays.deepToString(base.getBSplineMatrix().getArray()));
    }
}