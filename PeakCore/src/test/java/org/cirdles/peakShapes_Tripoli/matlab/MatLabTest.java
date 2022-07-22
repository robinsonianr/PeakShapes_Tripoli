package org.cirdles.peakShapes_Tripoli.matlab;

import jama.Matrix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class MatLabTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void find() {
        Matrix A = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}});
        Matrix B = new Matrix(2, 2, 1);
        double[][] anyMat = {{0, 0, 3}, {0, 0, 3}, {0, 0, 3}};
        //double[][] found = MatLab.find(A, 4, "last");
        Matrix any = MatLab.any(new Matrix(anyMat), 2);
        Matrix testKron = MatLab.kron(A, B);
        Boolean isNeg = MatLab.isAllNegative(new Matrix(5, 5, 0));


        System.out.println(isNeg);

    }
}