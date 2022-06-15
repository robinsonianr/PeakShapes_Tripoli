package org.cirdles.peakShapes_Tripoli.splineBasis;

public class SplineBasisModel {

    private double[] x; // vector of x values

    private double basisDegree;

    private double numSegments;

    private double[][] BSplineMatrix;


    private SplineBasisModel(double[] x, int basisDegree, int numSegments){
        this.x = x;
        this.basisDegree = basisDegree;
        this.numSegments = numSegments;
        this.BSplineMatrix = bBase(x, numSegments, basisDegree);
    }




    public static double[][] bBase(double[] x, int numSegments, int basisDegree){
        double[][] base = new double[x.length][numSegments+basisDegree];
        double xLower = x[0];
        double xUpper = x[x.length-1];

        double dx = (xUpper-xLower)/numSegments;
        double[] knots = {xLower - (basisDegree*dx), xUpper + (basisDegree*dx), numSegments + (2*basisDegree+1) };

        int nx = x.length;
        int nt = knots.length;

        double[] X = kron(x, knots);


        return base;
    }

    // Kronecker product of 2 arrays
    private static double[] kron(double[] A, double[] B){
        double[] newKron = new double[A.length * B.length];
        int c = 0;

            for(int i = 0; i < A.length; i++){
                for (int j = 0; j < B.length; j++ ){
                    newKron[c] = A[i]*B[j];
                    c++;
                }
            }

        return newKron;
    }

    private static int[][] ones(int rows, int cols){
        int[][] newOne = new int[rows][cols];

        return newOne;
    }


    public double getBasisDegree() {
        return basisDegree;
    }

    public double getNumSegments() {
        return numSegments;
    }


    public double[] getX() {
        return x;
    }

    public double[][] getBSplineMatrix() {
        return BSplineMatrix;
    }
}
