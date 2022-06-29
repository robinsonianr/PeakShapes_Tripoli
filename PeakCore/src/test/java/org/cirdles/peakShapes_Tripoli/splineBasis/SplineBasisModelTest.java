package org.cirdles.peakShapes_Tripoli.splineBasis;

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
        double[][] A = {{1, 2}, {3, 4}, {1, 0}};
        double[][] B = {{0, 5, 2}, {6, 7, 3}};
        double[][] vector = {{4}, {1}, {3}, {2}};
        double[][] vector2 = {{3.0, 1.0, 3}, {3, 2, 8}};
        double[][] testArr = {{1, 3, 5}, {7, 11, 13}, {17, 19, 23}};
        SplineBasisModel base = SplineBasisModel.initializeSpline(B, 500, 3);


        System.out.println(Arrays.deepToString(base.getBSplineMatrix()));
    }
}