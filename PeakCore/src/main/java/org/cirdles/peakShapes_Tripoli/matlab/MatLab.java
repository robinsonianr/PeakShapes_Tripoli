package org.cirdles.peakShapes_Tripoli.matlab;

import jama.Matrix;

import java.util.ArrayList;
import java.util.List;

public class MatLab {

    // Kronecker product of 2 arrays
    public static double[][] kron(double[][] A, double[][] B) {
        int rowA = A.length;
        int colA = A[0].length;
        int rowB = B.length;
        int colB = B[0].length;

        double[][] newKron = new double[rowA * rowB][colA * colB];
        if (rowB == 1) {
            int kronRow = 0;
            for (int i = 0; i < rowA; i++) {
                kronRow++;
                for (int j = 0; j < rowB; j++) {

                    int kronCol = 0;
                    for (int k = 0; k < colA; k++) {

                        for (int h = 0; h < colB; h++) {
                            newKron[kronRow - 1][kronCol] = A[i][k] * B[j][h];
                            kronCol++;
                        }
                    }
                }
            }

            return newKron;

        } else {
            int kronRow = 0;
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < rowB; j++) {
                    kronRow++;
                    int kronCol = 0;
                    for (int k = 0; k < colA; k++) {
                        for (int h = 0; h < colB; h++) {
                            newKron[kronRow - 1][kronCol] = A[i][k] * B[j][h];
                            kronCol++;
                        }
                    }
                }
            }
            return newKron;
        }

    }

    // matlab ones creates matrix of all ones of desired rows and columns
    public static double[][] ones(int rows, int cols) {
        double[][] newOne = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newOne[i][j] = 1;
            }
        }

        return newOne;
    }

    public static double[][] ones(int dim) {
        double[][] newOne = new double[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                newOne[i][j] = 1;
            }
        }

        return newOne;
    }

    // matlab subtract matrices
    public static double[][] subtract(double[][] A, double[][] B) {
//            int rowSize = Math.min(A.length, B.length);
//            int colSize = Math.min(A[0].length, B[0].length);
//            double[][] C = new double[A.length][A[0].length];
//            for (int i = 0; i < rowSize; i++){
//                for (int j =0; j < colSize; j++){
//                    C[i][j] = A[i][j] - B[i][j];
//                }
//            }
        int rowA = A.length;
        int colA = A[0].length;
        int rowB = B.length;
        int colB = B[0].length;
        if (colA == colB && rowA == rowB && rowB == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    C[i][j] = A[i][j] - B[i][j];
                }
            }
            return C;
        } else if (rowA == 1 && rowA == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    C[i][j] = A[0][j] - B[i][0];
                }
            }
            return C;
        } else if (colA == 1 && colA == rowB) {
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    C[i][j] = A[i][0] - B[0][j];
                }
            }
            return C;
        } else {
            double sum;
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    sum = 0;
                    for (int l = 0; l < rowB; l++) {
                        sum += A[i][l] - B[l][j];
                    }
                    C[i][j] = sum;
                }
            }
            return C;
        }

    }

    // matlab set matrix to degree
    public static double[][] expMatrix(double[][] matrix, int deg) {
        int row = matrix.length;
        int col = matrix[0].length;
        double[][] mat = new double[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                mat[i][j] = Math.pow(matrix[i][j], deg);
            }
        }

        return mat;
    }

    public static Matrix expMatrix(Matrix A, int deg) {
        double[][] matrix = A.getArray();
        int row = matrix.length;
        int col = matrix[0].length;
        double[][] mat = new double[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                mat[i][j] = Math.pow(matrix[i][j], deg);
            }
        }

        return new Matrix(mat);
    }

    // matlab multiply matrix by matrix
    public static double[][] multMatrix(double[][] A, double[][] B) {
        int rowA = A.length;
        int colA = A[0].length;
        int rowB = B.length;
        int colB = B[0].length;
        if (colA == colB && rowA == rowB && rowB == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    C[i][j] = A[i][j] * B[i][j];
                }
            }
            return C;
        } else if (rowA == 1 && rowA == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    C[i][j] = A[0][j] * B[i][0];
                }
            }
            return C;
        } else if (colA == 1 && colA == rowB) {
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    C[i][j] = A[i][0] * B[0][j];
                }
            }
            return C;
        } else {
            double sum;
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    sum = 0;
                    for (int l = 0; l < rowB; l++) {
                        sum += A[i][l] * B[l][j];
                    }
                    C[i][j] = sum;
                }
            }
            return C;
        }
    }

    // matlab multiply matrix by int
    public static double[][] multMatrix(double[][] A, double multiply) {
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                A[i][j] *= multiply;
            }
        }

        return A;
    }

    // Divides matrix by double
    public static double[][] divMatrix(double[][] A, double divide) {
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                A[i][j] /= divide;
            }
        }

        return A;
    }

    public static Matrix divMatrix(Matrix mat, double divide) {
        double[][] A = mat.getArray();
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                A[i][j] /= divide;
            }
        }

        return new Matrix(A);
    }

    // matlab eye
    public static double[][] eye(int size) {
        double[][] newEye = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    newEye[i][j] = 1;
                } else {
                    newEye[i][j] = 0;
                }
            }
        }

        return newEye;
    }

    // matlab diff
    public static double[][] diff(double[][] mat) {
        int row = mat.length;
        int col = mat[0].length - 1;
        double[][] newDiff;

        if (row == 1) {
            newDiff = new double[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    newDiff[i][j] = (mat[i][j] - mat[i][j + 1]);
                }
            }
            return newDiff;
        } else {
            newDiff = new double[row - 1][col + 1];
            for (int i = 0; i < row - 1; i++) {
                for (int j = 0; j < col + 1; j++) {
                    newDiff[i][j] = mat[i][j] - mat[i + 1][j];
                }
            }
            return newDiff;
        }
    }

    public static double[][] diff(double[][] mat, int num) {
        double[][] refDiff;
        refDiff = mat;
        for (int i = 0; i < num; i++) {
            refDiff = diff(refDiff);
        }

        return refDiff;
    }

    // Transposes matrix
    public static double[][] transpose(double[][] mat) {
        int row = mat[0].length;
        int col = mat.length;
        double[][] transP = new double[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                transP[i][j] = mat[j][i];
            }
        }
        return transP;
    }

    public static double[][] greatEqual(double[][] mat1, double[][] mat2) {
        int maxRow = Math.min(mat1.length, mat2.length);
        int maxCol = Math.min(mat1[0].length, mat2[0].length);
        int i, j = 0;
        double[][] ge = new double[maxRow][maxCol];
        for (int k = 0; k < maxRow; k++) {
            for (int h = 0; h < maxCol; h++) {
                if (mat1[k][h] >= mat2[k][h]) {
                    ge[k][h] = 1;
                } else {
                    ge[k][h] = 0;
                }
            }
        }
//        for (i = 0; i < maxRow; i++){
//            while (j < (Math.min(mat1[0].length, mat2[0].length))) {
//                if (mat1[i][j] >= mat2[i][j]){
//                    ge[i][j] = 1;
//                }else {
//                    ge[i][j] = 0;
//                }
//                j++;
//            }
//
//            for (j = (Math.min(mat1[0].length, mat2[0].length)); j < maxCol; j++ ){
//                ge[i][j] = 0;
//            }
//
//        }

        return ge;
    }


    public static double[][] greatEqual(double[][] mat, double num) {
        double[][] ge = new double[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                if (mat[i][j] >= num) {
                    ge[i][j] = 1;
                } else {
                    ge[i][j] = 0;
                }
            }
        }

        return ge;
    }

    public static double[][] greaterThan(double[][] mat, double num) {
        double[][] ge = new double[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                if (mat[i][j] > num) {
                    ge[i][j] = 1;
                } else {
                    ge[i][j] = 0;
                }
            }
        }

        return ge;
    }

    public static double[][] lessEqual(double[][] mat, double num) {
        double[][] le = new double[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                if (mat[i][j] <= num) {
                    le[i][j] = 1;
                } else {
                    le[i][j] = 0;
                }
            }
        }

        return le;
    }

    public static double[][] lessThan(double[][] mat1, double[][] mat2) {
        int maxRow = Math.min(mat1.length, mat2.length);
        int maxCol = Math.min(mat1[0].length, mat2[0].length);
        int i, j = 0;
        double[][] lt = new double[maxRow][maxCol];
        for (int k = 0; k < maxRow; k++) {
            for (int h = 0; h < maxCol; h++) {
                if (mat1[k][h] < mat2[k][h]) {
                    lt[k][h] = 1;
                } else {
                    lt[k][h] = 0;
                }
            }
        }
//        for (i = 0; i < maxRow; i++){
//            while (i < (Math.min(mat1.length, mat2.length)) && j < (Math.min(mat1[0].length, mat2[0].length))) {
//                if (mat1[i][j] >= mat2[i][j]){
//                    lt[i][j] = 1;
//                }else {
//                    lt[i][j] = 0;
//                }
//                j++;
//            }
//
//            for (j = (Math.min(mat1[0].length, mat2[0].length)); j < maxCol; j++ ){
//                lt[i][j] = 0;
//            }
//        }

        return lt;
    }

    // matlab size only works on 2d arrays
    public static int[] size(double[][] mat) {
        int[] matDim;
        matDim = new int[]{mat.length, mat[0].length};

        return matDim;
    }

    // matlab size with index
    public static int size(double[][] mat, int num) {
        if (num > 2) {
            return -1;
        } else {
            int[] choice = new int[]{mat.length, mat[0].length};
            int matDim;
            matDim = choice[num - 1];
            return matDim;

        }
    }

    public static int size(Matrix A, int num) {
        double[][] mat = A.getArray();
        if (num > 2) {
            return -1;
        } else {
            int[] choice = new int[]{mat.length, mat[0].length};
            int matDim;
            matDim = choice[num - 1];
            return matDim;

        }
    }

    // matlab linspace
    public static double[][] linspace(double min, double max, double points) {
        double[][] d = new double[1][(int) points];
        for (int i = 0; i < points; i++) {
            d[0][i] = min + i * (max - min) / (points - 1);
        }
        return d;
    }

    public static double[][] zeros(int rows, int cols) {
        double[][] zeroMat = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                zeroMat[i][j] = 0;
            }
        }

        return zeroMat;
    }

    public static double[][] zeros(int size) {
        double[][] zeroMat = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                zeroMat[i][j] = 0;
            }
        }

        return zeroMat;
    }

    public static double[][] andMatrix(double[][] A, double[][] B) {
        int rowA = A.length;
        int colA = A[0].length;
        int rowB = B.length;
        int colB = B[0].length;
        if (colA == colB && rowA == rowB && rowB == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    if (A[i][j] > 0 && B[i][j] > 0) {
                        C[i][j] = 1;
                    } else {
                        C[i][j] = 0;
                    }

                }
            }
            return C;
        } else if (rowA == 1 && rowA == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    if (A[0][j] > 0 && B[i][0] > 0) {
                        C[i][j] = 1;
                    } else {
                        C[i][j] = 0;
                    }
                }
            }
            return C;
        } else if (colA == 1 && colA == rowB) {
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    if (A[i][0] > 0 && B[0][j] > 0) {
                        C[i][j] = 1;
                    } else {
                        C[i][j] = 0;
                    }

                }
            }
            return C;
        } else {
            double sum;
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    sum = 0;
                    for (int l = 0; l < rowB; l++) {
                        sum += A[i][l] * B[l][j];
                    }
                    C[i][j] = sum;
                }
            }
            return C;
        }
    }

    public static double[][] find(double[][] mat, int num, String dir) {
        double[][] found = new double[num][1];
        int numCheck = 0;
        int i = 0;
        int index;
        if (dir.equalsIgnoreCase("first")) {
            index = 0;
            for (int startCol = 0; startCol < mat[0].length; startCol++) {
                for (int startRow = 0; startRow < mat.length; startRow++) {
                    index++;
                    if (numCheck != num) {
                        if (mat[startRow][startCol] > 0) {
                            found[i][0] = index - 1;
                            numCheck++;
                            i++;
                        }

                    } else {
                        break;
                    }
                }

            }

        } else if (dir.equalsIgnoreCase("last")) {
            index = (mat.length * mat[0].length) - 1;
            for (int startCol = mat[0].length - 1; startCol >= 0; startCol--) {
                for (int startRow = mat.length - 1; startRow >= 0; startRow--) {
                    index--;
                    if (numCheck != num) {
                        if (mat[startRow][startCol] > 0) {
                            found[i][0] = index + 1;
                            numCheck++;
                            i++;
                        }
                    } else {
                        break;
                    }
                }

            }
        }
        return found;
    }

    public static double[][] any(double[][] matrix, int dim) {
        double[][] anyMat = null;
        double sum = 0;
        if (dim == 1) {
            anyMat = new double[1][matrix[0].length];

            for (int i = 0; i < matrix[0].length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    sum += matrix[j][i];

                }
                if (sum > 0) {
                    anyMat[0][i] = 1;
                } else {
                    anyMat[0][i] = 0;
                }
                sum = 0;

            }


        } else if (dim == 2) {
            anyMat = new double[matrix.length][1];

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    sum += matrix[i][j];

                }
                if (sum > 0) {
                    anyMat[i][0] = 1;
                } else {
                    anyMat[i][0] = 0;
                }
                sum = 0;

            }
        }


        return anyMat;
    }

    public static double[][] rDivide(double[][] A, double div) {
        double[][] divMat = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                divMat[i][j] = div / A[i][j];
            }
        }

        return divMat;
    }

    public static Matrix rDivide(Matrix A, double div) {
        double[][] divMat = new double[A.getRowDimension()][A.getColumnDimension()];
        for (int i = 0; i < A.getArray().length; i++) {
            for (int j = 0; j < A.getArray()[0].length; j++) {
                divMat[i][j] = div / A.get(i, j);
            }
        }

        return new Matrix(divMat);
    }

    public static double[][] max(double[][] matrix, int dim) {
        double[][] maxMat = new double[matrix.length][matrix[0].length];
        double max = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                maxMat[i][j] = (dim > matrix[i][j]) ? dim : matrix[i][j];
            }
        }
        return maxMat;
    }

    public static double[][] diag(double[][] mat) {
        double[][] diagMat = new double[mat.length][mat.length];
        int dag = 0;

        for (int i = 0; i < diagMat.length; i++) {
            for (int j = 0; j < diagMat[0].length; j++) {
                if (i == j) {
                    diagMat[i][j] = mat[dag][0];
                    dag++;
                } else {
                    diagMat[i][j] = 0;
                }
            }
        }

        return diagMat;
    }

    public static double[][] mLDivide(double[][] A, double[][] B) {
        int rowA = A.length;
        int colA = A[0].length;
        int rowB = B.length;
        int colB = B[0].length;
        if (colA == colB && rowA == rowB && rowB == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    C[i][j] = A[i][j] / B[i][j];
                }
            }
            return C;
        } else if (rowA == 1 && rowA == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    C[i][j] = A[0][j] / B[i][0];
                }
            }
            return C;
        } else if (colA == 1 && colA == rowB) {
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    C[i][j] = A[i][0] / B[0][j];
                }
            }
            return C;
        } else {
            double sum;
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    sum = 0;
                    for (int l = 0; l < rowB; l++) {
                        sum += A[i][l] / B[l][j];
                    }
                    C[i][j] = sum;
                }
            }
            return C;
        }
    }

    public static Matrix mLDivide(Matrix matA, Matrix matB) {
        double[][] A = matA.getArray();
        double[][] B = matB.getArray();

        int rowA = A.length;
        int colA = A[0].length;
        int rowB = B.length;
        int colB = B[0].length;
        if (colA == colB && rowA == rowB && rowB == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    C[i][j] = A[i][j] / B[i][j];
                }
            }
            return new Matrix(C);
        } else if (rowA == 1 && rowA == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++) {
                for (int j = 0; j < colA; j++) {
                    C[i][j] = A[0][j] / B[i][0];
                }
            }
            return new Matrix(C);
        } else if (colA == 1 && colA == rowB) {
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    C[i][j] = A[i][0] / B[0][j];
                }
            }
            return new Matrix(C);
        } else {
            double sum;
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++) {
                for (int j = 0; j < colB; j++) {
                    sum = 0;
                    for (int l = 0; l < rowB; l++) {
                        sum += A[i][l] / B[l][j];
                    }
                    C[i][j] = sum;
                }
            }
            return new Matrix(C);
        }
    }

    public static Matrix concatMatrix(Matrix A, Matrix B) {
        Matrix concated = new Matrix(A.getRowDimension() + B.getRowDimension(), A.getColumnDimension());
        int indexBRow = 0;


        for (int i = 0; i < concated.getRowDimension() - B.getRowDimension(); i++) {
            for (int j = 0; j < concated.getColumnDimension(); j++) {
                concated.set(i, j, A.get(i, j));
            }
        }

        for (int i = A.getRowDimension(); i < concated.getRowDimension(); i++) {
            indexBRow++;
            int indexBCol = 0;
            for (int j = 0; j < concated.getColumnDimension(); j++) {
                concated.set(i, j, B.get(indexBRow - 1, indexBCol));
                indexBCol++;
            }

        }

        return concated;
    }


    public static Matrix blkDiag(Matrix A, Matrix B) {
        Matrix diag = new Matrix(A.getRowDimension() + B.getRowDimension(), A.getRowDimension() + B.getRowDimension());
        int indexBRow = 0;
        int indexBCol = 0;

        for (int i = 0; i < diag.getRowDimension() - B.getRowDimension(); i++) {
            for (int j = 0; j < diag.getColumnDimension() - B.getColumnDimension(); j++) {
                if (i == j) {
                    diag.set(i, j, A.get(i, j));
                } else {
                    diag.set(i, j, 0);
                }

            }
        }

        for (int i = A.getRowDimension(); i < diag.getRowDimension(); i++) {


            for (int j = A.getColumnDimension(); j < diag.getColumnDimension(); j++) {
                if (i == j) {
                    diag.set(i, j, B.get(indexBRow, indexBCol));
                    indexBCol++;
                    indexBRow++;
                }


            }

        }


        return diag;

    }

    // * Copyright 2008 Josh Vermaas, except he's nice and instead prefers
// * this to be licensed under the LGPL. Since the license itself is longer
// * than the code, if this truly worries you, you can look up the text at
// * http://www.gnu.org/licenses/
    public static Matrix solveNNLS(Matrix A, Matrix b) {
        List<Integer> p = new ArrayList<Integer>();
        List<Integer> z = new ArrayList<Integer>();
        int i = 0;
        int xm = A.getColumnDimension();
        int xn = 1;
        while (i < A.getColumnDimension())
            z.add(i++);
        Matrix x = new Matrix(xm, xn);
        /*
         * You need a finite number of iterations. Without this condition, the finite precision nature
         * of the math being done almost makes certain that the <1e-15 conditions won't ever hold up.
         * However, after so many iterations, it should at least be close to the correct answer.
         * For the intrepid coder, however, one could replace this again with an infinite while
         * loop and make the <1e-15 conditions into something like c*norm(A) or c*norm(b).
         */
        for (int iterations = 0; iterations < 300 * A.getColumnDimension() * A.getRowDimension(); iterations++) {
            //System.out.println(z.size() + " " + p.size());
            Matrix w = A.transpose().times(b.minus(A.times(x)));
            //w.print(7, 5);
            if (z.size() == 0 || isAllNegative(w)) {
                //System.out.println("Computation should break");
                //We are done with the computation. Break here!
                break;//Should break out of the outer while loop.
            }
            //Step 4
            int t = z.get(0);
            double max = w.get(t, 0);
            for (i = 1; i < z.size(); i++) {
                if (w.get(z.get(i), 0) > max) {
                    t = z.get(i);
                    max = w.get(z.get(i), 0);
                }
            }
            //Step 5
            p.add(t);
            z.remove((Integer) t);
            boolean allPositive = false;
            while (!allPositive) {
                //Step 6
                Matrix Ep = new Matrix(b.getRowDimension(), p.size());
                for (i = 0; i < p.size(); i++)
                    for (int j = 0; j < Ep.getRowDimension(); j++)
                        Ep.set(j, i, A.get(j, p.get(i)));
                Matrix Zprime = Ep.solve(b);
                Ep = null;
                Matrix Z = new Matrix(xm, xn);
                for (i = 0; i < p.size(); i++)
                    Z.set(p.get(i), 0, Zprime.get(i, 0));
                //Step 7
                allPositive = true;
                for (i = 0; i < p.size(); i++)
                    allPositive &= Z.get(p.get(i), 0) > 0;
                if (allPositive)
                    x = Z;
                else {
                    double alpha = Double.MAX_VALUE;
                    for (i = 0; i < p.size(); i++) {
                        int q = p.get(i);
                        if (Z.get(q, 0) <= 0) {
                            double xq = x.get(q, 0);
                            if (xq / (xq - Z.get(q, 0)) < alpha)
                                alpha = xq / (xq - Z.get(q, 0));
                        }
                    }
                    //Finished getting alpha. Onto step 10
                    x = x.plus(Z.minus(x).times(alpha));
                    for (i = p.size() - 1; i >= 0; i--)
                        if (Math.abs(x.get(p.get(i), 0)) < 1e-15)//Close enough to zero, no?
                            z.add(p.remove(i));
                }
            }
        }
        return x;
    }

    private static boolean isAllNegative(Matrix w) {
        boolean result = true;
        int m = w.getRowDimension();
        for (int i = 0; i < m; i++)
            result &= w.get(i, 0) <= 1e-15;
        return result;
    }
}
