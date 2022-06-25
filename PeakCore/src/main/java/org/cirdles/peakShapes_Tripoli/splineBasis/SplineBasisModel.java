package org.cirdles.peakShapes_Tripoli.splineBasis;


import org.apache.commons.math3.special.Gamma;

public class SplineBasisModel {

    private final double[][] x; // vector of x values

    private final double basisDegree;

    private final double numSegments;

    private final double[][] BSplineMatrix;


    private SplineBasisModel(double[][] x, int numSegments, int basisDegree){
        this.x = x;
        this.basisDegree = basisDegree;
        this.numSegments = numSegments;
        this.BSplineMatrix = bBase(x, numSegments, basisDegree);
    }



    public static SplineBasisModel initializeSpline(double[][] x,int numSegments, int basisDegree ){
            return new SplineBasisModel(x, numSegments, basisDegree);
    }




    public double[][] bBase(double[][] x, int numSegments, int basisDegree){
        /*
            x = x(:);
            xl = x(1);
            xr = x(end);

            % Compute the B-splines
            dx = (xr-xl)/nseg;
            knots = linspace(xl-bdeg*dx, xr+bdeg*dx, nseg+2*bdeg+1); % xl-bdeg*dx:dx:x4+bdeg*dx;

            nx = length(x);
            nt = length(knots);
            X = kron(x, ones(1,nt));
            T = kron(knots, ones(nx,1));

            P = (X - T).^bdeg .* (X >= T); % added = to >= to match de Boor definition?
            D = diff(eye(nt), bdeg+1)/(gamma(bdeg+1)*dx^bdeg);
            B = (-1)^(bdeg+1) * P * D';

            % Make B-splines exactly zero beyond their end knots
            nb = size(B,2);
            X = kron(x, ones(1,nb));
            sk = knots((1:nb) + bdeg + 1);
            SK = kron(sk, ones(nx,1));
            Mask = X < SK;
            B = B .* Mask;
         */
        double[][] base;
        double[][] X, X2, sk, SK, T, P, D, MASK;
        double xLower = x[0][0];
        double xUpper = x[x.length-1][x[0].length-1];

        double dx = (xUpper-xLower)/numSegments;
        double[][] knots = linspace(xLower - (basisDegree*dx), xUpper + (basisDegree*dx),numSegments + ((2*basisDegree)+1) );

        int nx = x[0].length;
        int nt = knots[0].length;

        X = kron(x, ones(1, nt));
        T = kron(knots, ones(nx, 1));

        P = multMatrix(expMatrix(subtract(X,T), basisDegree), greatEqual(X, T));
        D = divMatrix(diff(eye(nt), basisDegree+1),Gamma.gamma((basisDegree+1)*(Math.pow(dx, basisDegree))));
        base =  multMatrix(multMatrix(P,transpose(D)), Math.pow(-1, basisDegree+1));
        int nb = size(base, 2);
        X2 = kron(x, ones(1, nb));
        sk = new double[1][nb];
        for (int i = 0; i < 1; i++){
            for (int j = 0; j < nb; j++) {
                sk[i][j] = knots[i][j] + (basisDegree + 1);
            }
        }
        SK = kron(sk, ones(nx, 1));
        MASK = lessThan(X2, SK);
        base = multMatrix(base, MASK);






        return base;
    }

    public double[][] bBase(double[][] x, int xl, int xr, int numSegments, int basisDegree){
        double[][] base;
        double[][] X, X2, sk, SK, T, P, D, MASK;
        double xLower;
        double xUpper;

        if(xl > x[0][0]){
            xLower = x[0][0];
        }else {
            xLower = xl;
        }

        if(xr < x[x.length-1][x[0].length-1]){
            xUpper = x[x.length-1][x[0].length-1];
        }else {
            xUpper = xr;
        }


        double dx = (xUpper-xLower)/numSegments;
        double[][] knots = linspace(xLower - (basisDegree*dx), xUpper + (basisDegree*dx),numSegments + ((2*basisDegree)+1) );

        int nx = x[0].length;
        int nt = knots[0].length;

        X = kron(x, ones(1, nt));
        T = kron(knots, ones(nx, 1));

        P = multMatrix(expMatrix(subtract(X,T), basisDegree), greatEqual(X, T));
        D = divMatrix(diff(eye(nt), basisDegree+1),Gamma.gamma((basisDegree+1)*(Math.pow(dx, basisDegree))));
        base =  multMatrix(multMatrix(P,transpose(D)), Math.pow(-1, basisDegree+1));
        int nb = size(base, 2);
        X2 = kron(x, ones(1, nb));
        sk = new double[1][nb];
        for (int i = 0; i < 1; i++){
            for (int j = 0; j < nb; j++) {
                sk[i][j] = knots[i][j] + (basisDegree + 1);
            }
        }
        SK = kron(sk, ones(nx, 1));
        MASK = lessThan(X2, SK);
        base = multMatrix(base, MASK);






        return base;
    }

    // Kronecker product of 2 arrays
    public double[][] kron(double[][] A, double[][] B){
        int rowA = A.length;
        int colA = A[0].length;
        int rowB = B.length;
        int colB = B[0].length;

        double[][] newKron = new double[rowA*rowB][colA*colB];
        if (rowB == 1){
            int kronRow = 0;
            for(int i = 0; i < rowA; i++){
                kronRow++;
                for (int j = 0; j < rowB; j++ ){

                    int kronCol = 0;
                    for (int k = 0; k < colA; k++) {

                        for (int h = 0; h < colB; h++) {
                            newKron[kronRow-1][kronCol] = A[i][k] * B[j][h];
                            kronCol++;
                        }
                    }
                }
            }

            return newKron;

        }else {
            int kronRow = 0;
            for(int i = 0; i < rowA; i++){
                for (int j = 0; j < rowB; j++ ){
                    kronRow++;
                    int kronCol = 0;
                    for (int k = 0; k < colA; k++) {
                        for (int h = 0; h < colB; h++) {
                            newKron[kronRow-1][kronCol] = A[i][k] * B[j][h];
                            kronCol++;
                        }
                    }
                }
            }
            return newKron;
        }

    }

    // matlab ones creates matrix of all ones of desired rows and columns
    public double[][] ones(int rows, int cols){
        double[][] newOne = new double[rows][cols];
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                newOne[i][j] = 1;
            }
        }

        return newOne;
    }
    // matlab subtract matrices
    public double[][] subtract(double[][] A, double[][] B){
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
        if(colA == colB && rowA == rowB && rowB == colB){
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++){
                for (int j = 0; j < colA; j++){
                    C[i][j] = A[i][j]-B[i][j];
                }
            }
            return C;
        } else if (rowA == 1 && rowA == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++){
                for (int j = 0; j < colA; j++){
                    C[i][j] = A[0][j]-B[i][0];
                }
            }
            return C;
        } else if (colA == 1 && colA == rowB ) {
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++){
                for (int j = 0; j < colB; j++){
                    C[i][j] = A[i][0]-B[0][j];
                }
            }
            return C;
        } else {
            double sum;
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++){
                for ( int j = 0; j < colB; j++){
                    sum =0;
                    for ( int l = 0; l < rowB; l++){
                        sum += A[i][l]-B[l][j];
                    }
                    C[i][j] = sum;
                }
            }
            return C;
        }

    }
    // matlab set matrix to degree
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

    // matlab multiply matrix by matrix
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
                }
            }
            return C;
        } else if (rowA == 1 && rowA == colB) {
            double[][] C = new double[rowB][colA];
            for (int i = 0; i < rowB; i++){
                for (int j = 0; j < colA; j++){
                    C[i][j] = A[0][j]*B[i][0];
                }
            }
            return C;
        } else if (colA == 1 && colA == rowB ) {
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++){
                for (int j = 0; j < colB; j++){
                    C[i][j] = A[i][0]*B[0][j];
                }
            }
            return C;
        } else {
            double sum;
            double[][] C = new double[rowA][colB];
            for (int i = 0; i < rowA; i++){
                for ( int j = 0; j < colB; j++){
                    sum =0;
                    for ( int l = 0; l < rowB; l++){
                        sum += A[i][l]*B[l][j];
                    }
                    C[i][j] = sum;
                }
            }
            return C;
        }
    }

    // matlab multiply matrix by int
    public double[][] multMatrix(double[][] A, double multiply) {
        for (int i = 0; i < A.length; i++){
            for (int j = 0; j < A[0].length; j++){
                A[i][j] *= multiply;
            }
        }

        return A;
    }

    // Divides matrix by double
    public double[][] divMatrix(double[][] A, double divide) {
        for (int i = 0; i < A.length; i++){
            for (int j = 0; j < A[0].length; j++){
                A[i][j] /= divide;
            }
        }

        return A;
    }

    // matlab eye
    public double[][] eye(int size){
        double[][] newEye = new double[size][size];

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if (i == j){
                    newEye[i][j] = 1;
                }else {
                    newEye[i][j] = 0;
                }
            }
        }

        return newEye;
    }

    // matlab diff
    public double[][] diff(double[][] mat) {
        int row = mat.length;
        int col = mat[0].length - 1;
        double[][] newDiff;

        if (row == 1) {
            newDiff = new double[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    newDiff[i][j] = Math.abs(mat[i][j] - mat[i][j + 1]);
                }
            }
            return newDiff;
        }else {
            newDiff = new double[row -1][col + 1];
            for (int i = 0; i < row -1; i++){
                for (int j = 0; j < col + 1; j++){
                    newDiff[i][j] = Math.abs(mat[i][j] - mat[i + 1][j]);
                }
            }
            return newDiff;
        }
    }

    public double[][] diff(double[][] mat, int num) {
        double [][] refDiff;
        refDiff = mat;
        for (int i = 0; i < num; i++){
            refDiff = diff(refDiff);
        }

        return refDiff;
    }

    // Transposes matrix
    public double[][] transpose(double[][] mat){
        int row = mat[0].length;
        int col = mat.length;
        double[][] transP = new double[row][col];

        for (int i = 0; i < row; i++ ){
            for (int j = 0; j < col; j++){
                transP[i][j] = mat[j][i];
            }
        }
        return transP;
    }

    public double[][] greatEqual(double[][] mat1, double[][] mat2){
        int maxRow = Math.min(mat1.length, mat2.length);
        int maxCol = Math.min(mat1[0].length, mat2[0].length);
        int i, j = 0;
        double [][] ge = new double[maxRow][maxCol];
        for (int k = 0; k < maxRow; k++){
            for (int h = 0; h < maxCol; h++){
                if (mat1[k][h] >= mat2[k][h]){
                    ge[k][h] = 1;
                }else {
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


    public double[][] greatEqual(double[][] mat, int num){
        double [][] ge = new double[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++){
            for (int j = 0; j < mat[0].length; j++){
                if (mat[i][j] >= num){
                    ge[i][j] = 1;
                }else {
                    ge[i][j] = 0;
                }
            }
        }

        return ge;
    }

    public double[][] lessThan(double[][] mat1, double[][] mat2){
        int maxRow = Math.min(mat1.length, mat2.length);
        int maxCol = Math.min(mat1[0].length, mat2[0].length);
        int i, j = 0;
        double [][] lt = new double[maxRow][maxCol];
        for (int k = 0; k < maxRow; k++){
            for (int h = 0; h < maxCol; h++){
                if (mat1[k][h] < mat2[k][h]){
                    lt[k][h] = 1;
                }else {
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

    // matlab size
    public int[] size(double[][] mat){
        int[] matDim;
        matDim = new int[]{mat.length, mat[0].length};

        return matDim;
    }
    // matlab size with index
    public int size(double[][] mat, int num){
        if (num > 2){
            return -1;
        }else {
            int[] choice = new int[]{mat.length, mat[0].length};
            int matDim;
            matDim = choice[num - 1];
            return matDim;

        }
    }

    // matlab linspace
    public double[][] linspace(double min, double max, int points) {
        double[][] d = new double[1][points];
        for (int i = 0; i < points; i++){
            d[0][i] = min + i * (max - min) / (points - 1);
        }
        return d;
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
