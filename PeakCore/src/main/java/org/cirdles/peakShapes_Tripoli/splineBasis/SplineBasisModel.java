package org.cirdles.peakShapes_Tripoli.splineBasis;

import java.util.Arrays;

public class SplineBasisModel {

    private double[][] x; // vector of x values

    private double basisDegree;

    private double numSegments;

    private double[][] BSplineMatrix;


    private SplineBasisModel(double[][] x, int basisDegree, int numSegments){
        this.x = x;
        this.basisDegree = basisDegree;
        this.numSegments = numSegments;
        this.BSplineMatrix = bBase(x, numSegments, basisDegree);
    }



    public static SplineBasisModel initializeSpline(double[][] x, int basisDegree, int numSegments){
            return new SplineBasisModel(x, basisDegree, numSegments);
    }




    public double[][] bBase(double[][] x, int numSegments, int basisDegree){
        double[][] base = new double[x.length][numSegments+basisDegree];
        double xLower = x[0][0];
        double xUpper = x[x.length-1][x[0].length-1];

        double dx = (xUpper-xLower)/numSegments;
        double[][] knots = {{xLower - (basisDegree*dx)}, {xUpper + (basisDegree*dx)}, {numSegments + (2*basisDegree+1)} };

        int nx = x[0].length;
        int nt = knots.length;

        double[][] X = kron(x, ones(1, nt));
        double[][] T = kron(knots, ones(nx, 1));

        double[][] P = expMatrix(subtract(X,T), basisDegree);


        return base;
    }

    // Kronecker product of 2 arrays
    public double[][] kron(double[][] A, double[][] B){
        int rowA = A.length;
        int colA = A[0].length;
        int rowB = B.length;
        int colB = B[0].length;

        double[][] newKron = new double[rowA*rowB][colA*colB];

            for(int i = 0; i < rowA; i++){
                for (int j = 0; j < rowB; j++ ){
                    for (int k = 0; k < colA; k++){
                        for (int h = 0; h < colB; h++){
                            newKron[i +j][k+ h ] = A[i][k]*B[j][h];
                        }
                    }
                }
            }

        return newKron;
    }

    // Creates matrix of all ones of desired rows and columns
    public double[][] ones(int rows, int cols){
        double[][] newOne = new double[rows][cols];
        for (int i = 0; i < rows; i++){
            for (int j =0; j < cols; j++){
                newOne[i][j] = 1;
            }
        }

        return newOne;
    }

    public double[][] subtract(double[][] A, double[][] B){
            int rowSize = Math.min(A.length, B.length);
            int colSize = Math.min(A[0].length, B[0].length);
            double[][] C = new double[rowSize][colSize];
            for (int i = 0; i < rowSize; i++){
                for (int j =0; j < colSize; j++){
                    C[i][j] = A[i][j] - B[i][j];
                }
            }

            return C;
    }

    public double[][] expMatrix(double[][] matrix, int deg){
            int row = matrix.length;
            int col = matrix[0].length;
            double[][] mat = new double[row][col];

            for (int i = 0; i < row; i++){
                for (int j = 0; j < col; j++){
                    mat[i][j] = Math.pow(matrix[i][j],deg);
                }
            }

            return mat;
    }

    public double[][] multMatrix(double[][] A, double[][] B){
        int rowA = A.length;
        int colA = A[0].length;
        int rowB = B.length;
        int colB = B[0].length;
        if(colA == colB && rowA == rowB && rowB == colB){
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++){
                for (int j = 0; j < colA; j++){
                    C[i][j] = A[i][j]*B[i][j];
                    System.out.print(C[i][j] + " ");
                }
                System.out.println();
            }
            return C;
        } else if (rowA == 1 && rowA == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++){
                for (int j = 0; j < colA; j++){
                    C[i][j] = A[0][j]*B[i][0];
                    System.out.print(C[i][j] + " ");

                }
                System.out.println();
            }
            return C;
        } else if (colA == 1 && colA == rowB ) {
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++){
                for (int j = 0; j < colB; j++){
                    C[i][j] = A[i][0]*B[0][j];
                    System.out.print(C[i][j] + " ");
                }
                System.out.println();
            }
            return C;
        } else if (colA == rowB) {
            double sum;
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++){
                for ( int j = 0; j < colB; j++){
                    sum =0;
                    for ( int l = 0; l < rowB; l++){
                        sum += A[i][l]*B[l][j];
                    }
                    C[i][j] = sum;
                    System.out.print(C[i][j] + " ");
                }
                System.out.println();
            }
            return C;
        }

        return null;
    }


    public double getBasisDegree() {
        return basisDegree;
    }

    public double getNumSegments() {
        return numSegments;
    }


    public double[][] getX() {
        return x;
    }

    public double[][] getBSplineMatrix() {
        return BSplineMatrix;
    }
}
