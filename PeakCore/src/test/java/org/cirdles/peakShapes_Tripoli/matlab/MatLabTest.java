package org.cirdles.peakShapes_Tripoli.matlab;

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
        MatLab matLab = new MatLab();
        double[][] A = {{1, 2, 3}, {4, 5, 6}};
        double[][] B = matLab.ones(2);
        double[][] anyMat = {{0, 0, 3}, {0, 0, 3}, {0, 0, 3}};
        double[][] found = matLab.find(A, 4, "last");
        double[][] any = matLab.any(anyMat, 2);
        double[][] testKron = matLab.kron(A, B);

        System.out.println(Arrays.deepToString(testKron));

    }
}