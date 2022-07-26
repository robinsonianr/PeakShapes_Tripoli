package org.cirdles.peakShapes_Tripoli.matlab;

import jama.Matrix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MatLabTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findTest() {
        Matrix A = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}});
        double[][] firstExpected = {{0.0}, {1.0}, {2.0}, {3.0}};
        Matrix firstActual = MatLab.find(A, 4, "first"); // actual
        double[][] lastExpected = {{5.0}, {4.0}, {3.0}, {2.0}};
        Matrix lastActual = MatLab.find(A, 4, "last"); // actual

        assertArrayEquals(firstActual.getArray(), firstExpected);
        assertArrayEquals(lastActual.getArray(), lastExpected);

    }


    @Test
    void kronTest() {

    }
}