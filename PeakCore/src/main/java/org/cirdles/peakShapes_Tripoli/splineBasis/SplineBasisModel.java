package org.cirdles.peakShapes_Tripoli.splineBasis;


import org.apache.commons.math3.special.Gamma;
import org.cirdles.peakShapes_Tripoli.matlab.MatLab;

public class SplineBasisModel {

    private final double[][] x; // vector of x values

    private final double basisDegree;

    private final double numSegments;

    private final double[][] BSplineMatrix;


    private SplineBasisModel(double[][] x, int numSegments, int basisDegree) {
        this.x = x;
        this.basisDegree = basisDegree;
        this.numSegments = numSegments;
        this.BSplineMatrix = bBase(x, numSegments, basisDegree);
    }


    public static SplineBasisModel initializeSpline(double[][] x, int numSegments, int basisDegree) {
        return new SplineBasisModel(x, numSegments, basisDegree);
    }


    public double[][] bBase(double[][] x, int numSegments, int basisDegree) {
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
        MatLab matLab = new MatLab();

        double[][] base;
        double[][] X, X2, sk, SK, T, P, D, MASK;
        double xLower = x[0][0];
        double xUpper = x[x.length - 1][x[0].length - 1];

        double dx = (xUpper - xLower) / numSegments;
        double[][] knots = matLab.linspace(xLower - (basisDegree * dx), xUpper + (basisDegree * dx), numSegments + ((2 * basisDegree) + 1));

        int nx = x[0].length;
        int nt = knots[0].length;

        X = matLab.kron(x, matLab.ones(1, nt));
        T = matLab.kron(knots, matLab.ones(nx, 1));

        P = matLab.multMatrix(matLab.expMatrix(matLab.subtract(X, T), basisDegree), matLab.greatEqual(X, T));
        D = matLab.divMatrix(matLab.diff(matLab.eye(nt), basisDegree + 1), Gamma.gamma((basisDegree + 1) * (Math.pow(dx, basisDegree))));
        base = matLab.multMatrix(matLab.multMatrix(P, matLab.transpose(D)), Math.pow(-1, basisDegree + 1));
        int nb = matLab.size(base, 2);
        X2 = matLab.kron(x, matLab.ones(1, nb));
        sk = new double[1][nb];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < nb; j++) {
                sk[i][j] = knots[i][j] + (basisDegree + 1);
            }
        }
        SK = matLab.kron(sk, matLab.ones(nx, 1));
        MASK = matLab.lessThan(X2, SK);
        base = matLab.multMatrix(base, MASK);


        return base;
    }

    public double[][] bBase(double[][] x, int xl, int xr, int numSegments, int basisDegree) {
        MatLab matLab = new MatLab();
        double[][] base;
        double[][] X, X2, sk, SK, T, P, D, MASK;
        double xLower;
        double xUpper;

        if (xl > x[0][0]) {
            xLower = x[0][0];
        } else {
            xLower = xl;
        }

        if (xr < x[x.length - 1][x[0].length - 1]) {
            xUpper = x[x.length - 1][x[0].length - 1];
        } else {
            xUpper = xr;
        }


        double dx = (xUpper - xLower) / numSegments;
        double[][] knots = matLab.linspace(xLower - (basisDegree * dx), xUpper + (basisDegree * dx), numSegments + ((2 * basisDegree) + 1));

        int nx = x[0].length;
        int nt = knots[0].length;

        X = matLab.kron(x, matLab.ones(1, nt));
        T = matLab.kron(knots, matLab.ones(nx, 1));

        P = matLab.multMatrix(matLab.expMatrix(matLab.subtract(X, T), basisDegree), matLab.greatEqual(X, T));
        D = matLab.divMatrix(matLab.diff(matLab.eye(nt), basisDegree + 1), Gamma.gamma((basisDegree + 1) * (Math.pow(dx, basisDegree))));
        base = matLab.multMatrix(matLab.multMatrix(P, matLab.transpose(D)), Math.pow(-1, basisDegree + 1));
        int nb = matLab.size(base, 2);
        X2 = matLab.kron(x, matLab.ones(1, nb));
        sk = new double[1][nb];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < nb; j++) {
                sk[i][j] = knots[i][j] + (basisDegree + 1);
            }
        }
        SK = matLab.kron(sk, matLab.ones(nx, 1));
        MASK = matLab.lessThan(X2, SK);
        base = matLab.multMatrix(base, MASK);


        return base;
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
