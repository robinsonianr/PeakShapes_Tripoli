package org.cirdles.peakShapes_Tripoli.splineBasis;


import jama.Matrix;
import org.apache.commons.math3.special.Gamma;
import org.cirdles.peakShapes_Tripoli.matlab.MatLab;

public class SplineBasisModel {

    private final double[][] x; // vector of x values

    private final double basisDegree;

    private final double numSegments;

    private final double[][] BSplineMatrix;


    private SplineBasisModel() {
        this.x = null;
        this.basisDegree = 0;
        this.numSegments = 0;
        this.BSplineMatrix = null;
    }

    private SplineBasisModel(double[][] x, int numSegments, int basisDegree) {
        this.x = x;
        this.basisDegree = basisDegree;
        this.numSegments = numSegments;
        this.BSplineMatrix = bBase(x, numSegments, basisDegree);
    }


    public static SplineBasisModel initializeSpline(double[][] x, int numSegments, int basisDegree) {
        return new SplineBasisModel(x, numSegments, basisDegree);
    }

    public static SplineBasisModel initializeSpline() {
        return new SplineBasisModel();
    }


    public static double[][] bBase(double[][] x, int numSegments, int basisDegree) {
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
        double xUpper = x[x.length - 1][x[0].length - 1];

        double dx = (xUpper - xLower) / numSegments;
        double[][] knots = MatLab.linspace(xLower - (basisDegree * dx), xUpper + (basisDegree * dx), numSegments + ((2 * basisDegree) + 1));

        int nx = x[0].length;
        int nt = knots[0].length;

        X = MatLab.kron(x, MatLab.ones(1, nt));
        T = MatLab.kron(knots, MatLab.ones(nx, 1));

        P = MatLab.multMatrix(MatLab.expMatrix(MatLab.subtract(X, T), basisDegree), MatLab.greatEqual(X, T));
        D = MatLab.divMatrix(MatLab.diff(MatLab.eye(nt), basisDegree + 1), Gamma.gamma((basisDegree + 1) * (Math.pow(dx, basisDegree))));
        base = MatLab.multMatrix(MatLab.multMatrix(P, MatLab.transpose(D)), Math.pow(-1, basisDegree + 1));
        int nb = MatLab.size(base, 2);
        X2 = MatLab.kron(x, MatLab.ones(1, nb));
        sk = new double[1][nb];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < nb; j++) {
                sk[i][j] = knots[i][j] + (basisDegree + 1);
            }
        }
        SK = MatLab.kron(sk, MatLab.ones(nx, 1));
        MASK = MatLab.lessThan(X2, SK);
        base = MatLab.multMatrix(base, MASK);


        return base;
    }

    public static double[][] bBase(double[][] x, double xl, double xr, double numSegments, int basisDegree) {

        Matrix x1, x2, jk, JK, t, p, d, mask, Base;
        double[][] base;
        double[][] X, X2, sk, SK, T, P, D, MASK;
        double xLower;
        double xUpper;


        xLower = ((xl > x[0][0])) ? x[0][0] : xl;

        xUpper = (xr < x[x.length - 1][x[0].length - 1]) ? x[x.length - 1][x[0].length - 1] : xr;


        double dx = (xUpper - xLower) / numSegments;
        double[][] knots = MatLab.linspace(xLower - basisDegree * dx, xUpper + basisDegree * dx, numSegments + 2 * basisDegree + 1);

        int nx = x[0].length;
        int nt = knots[0].length;

        X = MatLab.transpose(MatLab.kron(x, MatLab.transpose(MatLab.ones(1, nt))));
        x1 = new Matrix(X);
        T = MatLab.kron(knots, MatLab.ones(nx, 1));

        t = new Matrix(T);



        //P = MatLab.multMatrix(MatLab.expMatrix(matLab.subtract(X, T), basisDegree), matLab.greatEqual(X, T));

        p = MatLab.expMatrix(x1.minus(t), basisDegree).arrayTimes(new Matrix(MatLab.greatEqual(X, T)));
        //MatLab.expMatrix(x1.minus(t), basisDegree).times(new Matrix(MatLab.greatEqual(X, T)));
        double v = (basisDegree + 1);
        D = MatLab.divMatrix(MatLab.diff(MatLab.eye(nt), basisDegree + 1 ), (Gamma.gamma(v) * (Math.pow(dx, basisDegree))));
        d = new Matrix(D);
        //base = MatLab.multMatrix(MatLab.multMatrix(P, MatLab.transpose(D)), Math.pow(-1, basisDegree + 1));
        Base =  p.times(d.transpose());

        int nb = MatLab.size(Base, 2);
        X2 = MatLab.transpose(MatLab.kron(x, MatLab.transpose(MatLab.ones(1, nb))));
        sk = new double[1][nb];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < nb; j++) {
                sk[i][j] = knots[i][j + basisDegree + 1] ;
            }
        }
        SK = MatLab.kron(sk, MatLab.ones(nx, 1));
        MASK = MatLab.lessThan(X2, SK);
        base = Base.arrayTimes(new Matrix(MASK)).getArray();

        //MatLab.multMatrix(base, MASK)


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
