package org.cirdles.peakShapes_Tripoli.beamShape;


import jama.CholeskyDecomposition;
import jama.LUDecomposition;
import jama.Matrix;
import jama.QRDecomposition;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.PeakMeas;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel.DataModel;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec.MassSpecModel;
import org.cirdles.peakShapes_Tripoli.matlab.MatLab;
import org.cirdles.peakShapes_Tripoli.splineBasis.SplineBasisModel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BeamShapeCollectorWidth {
    DataModel data;
    PeakMeas peakMeas;

    MassSpecModel massSpec;

    MatLab matLab = new MatLab();


    public BeamShapeCollectorWidth(Path fileName, MassSpecModel massSpec) throws IOException {
        data = new DataModel(fileName);
        this.massSpec = massSpec;
        data.calcCollectorWidthAMU(massSpec);
        data.calcBeamWidthAMU(massSpec);
        peakMeas = PeakMeas.initializePeakMeas(data, massSpec);


    }


    public Matrix getBeamShape() {
        Matrix Basis, gb, WData, BeamWLS, TrimGMatrix, TrimMagnetMasses, peakMassIntensity, magnetMasses, D, beamShape, BeamWNNLS, test1, test2;


        // spline basis Basis

        int basisDegree = 3;
        int orderDiff = 2;
        double beamKnots = Math.ceil(peakMeas.getBeamWindow() / peakMeas.getDeltaMagnetMass()) - (2 * basisDegree);
        int nInterp = 1000;

        double xLower = data.getPeakCenterMass() - peakMeas.getBeamWindow() / 2;
        double xUpper = data.getPeakCenterMass() + peakMeas.getBeamWindow() / 2;


        double[][] beamMassInterp = matLab.linspace(xLower, xUpper, nInterp);
        SplineBasisModel base = SplineBasisModel.initializeSpline();
        double[][] basis = base.bBase(beamMassInterp, xLower, xUpper, beamKnots, basisDegree);
        Basis = new Matrix(basis);
        double deltaBeamMassInterp = beamMassInterp[0][1] - beamMassInterp[0][0];


        // calculate integration matrix G, depends on B, data


        int numMagnetMasses = data.getMagnetMasses().length;
        double[][] gMatrix = matLab.zeros(numMagnetMasses, nInterp);

        for (int iMass = 0; iMass < numMagnetMasses; iMass++) {
            double[][] massesInCollector = new Matrix(matLab.greatEqual(beamMassInterp, peakMeas.getCollectorLimits()[iMass][0])).arrayTimes(new Matrix(matLab.lessEqual(beamMassInterp, peakMeas.getCollectorLimits()[iMass][1]))).getArray();
            double[][] firstMassIndexInside;
            double[][] lastMassIndexInside;
            if (!(matLab.find(massesInCollector, 1, "first")[0][0] == 0 && matLab.find(massesInCollector, 1, "last")[0][0] == 0)) {
                firstMassIndexInside = matLab.find(massesInCollector, 1, "first");
                lastMassIndexInside = matLab.find(massesInCollector, 1, "last");
                for (int i = (int) firstMassIndexInside[0][0] + 1; i < (int) lastMassIndexInside[0][0] - 1; i++) {
                    gMatrix[iMass][i] = deltaBeamMassInterp;

                }

                gMatrix[iMass][(int) firstMassIndexInside[0][0]] = deltaBeamMassInterp / 2;
                gMatrix[iMass][(int) lastMassIndexInside[0][0]] = deltaBeamMassInterp / 2;
            }


        }

        // trim data
        int newDataSet = 0;
        double[][] hasModelBeam = matLab.any(gMatrix, 2);
        for (int i = 0; i < hasModelBeam.length; i++) {
            for (int j = 0; j < hasModelBeam[0].length; j++) {
                if (hasModelBeam[i][j] == 1) {
                    newDataSet++;
                }
            }
        }
        double[][] trimGMatrix = new double[newDataSet][gMatrix[0].length];
        int j = 0;
        for (int i = 0; i < gMatrix.length; i++) {
            if (hasModelBeam[i][0] > 0) {
                trimGMatrix[j] = gMatrix[i];
                j++;
            }
        }
        TrimGMatrix = new Matrix(trimGMatrix);

        double[][] trimMagnetMasses = new double[newDataSet][data.getMagnetMasses().length];
        int h = 0;

        for (int i = 0; i < data.getMagnetMasses().length; i++) {
            if (hasModelBeam[i][0] > 0) {
                trimMagnetMasses[h] = data.getMagnetMasses()[i];
                h++;
            }
        }

        double[][] trimPeakIntensity = new double[newDataSet][data.getMagnetMasses().length];
        int k = 0;
        for (int i = 0; i < data.getMeasPeakIntensity().length; i++) {
            if (hasModelBeam[i][0] > 0) {
                trimPeakIntensity[k] = data.getMeasPeakIntensity()[i];
                k++;
            }
        }

        data.setMagnetMasses(trimMagnetMasses);
        data.setMeasPeakIntensity(trimPeakIntensity);


        peakMassIntensity = new Matrix(data.getMeasPeakIntensity());
        magnetMasses = new Matrix(data.getMagnetMasses());


        // WLS and NNLS
        //TODO fix BeamWNNLS matrix
        double[][] GB = matLab.multMatrix(trimGMatrix, basis);
        gb = TrimGMatrix.times(Basis);
        double[][] wData = matLab.diag(matLab.rDivide(matLab.max(data.getMeasPeakIntensity(), 1), 1));
        WData = new Matrix(wData);
        //double[][] beamWLS = matLab.mLDivide(matLab.multMatrix(matLab.transpose(GB), matLab.multMatrix(wData, GB)) , matLab.multMatrix(matLab.transpose(GB), matLab.multMatrix(wData, data.getMeasPeakIntensity())));
        BeamWLS = (gb.transpose().times(WData.times(gb))).inverse().times((gb.transpose().times(WData.times(peakMassIntensity))));
        test1 = new Matrix(WData.chol().getL().getArray()).times(gb);
        test2 = new Matrix(WData.chol().getL().getArray()).times(peakMassIntensity);
        BeamWNNLS = solveNNLS(test1, test2);

        // smoothing spline
        double lambda = 1e-11;
        D = new Matrix(matLab.diff(matLab.eye((int) (beamKnots + basisDegree)), orderDiff));

        beamShape = Basis.times(BeamWNNLS);


        return beamShape;
    }


    public static Matrix solveNNLS(Matrix A,Matrix b)
    {
        List<Integer> p = new ArrayList<Integer>();
        List<Integer> z = new ArrayList<Integer>();
        int i = 0;
        int xm = A.getColumnDimension();
        int xn = 1;
        while (i < A.getColumnDimension())
            z.add(i++);
        Matrix x = new Matrix(xm,xn);
        /*
         * You need a finite number of iterations. Without this condition, the finite precision nature
         * of the math being done almost makes certain that the <1e-15 conditions won't ever hold up.
         * However, after so many iterations, it should at least be close to the correct answer.
         * For the intrepid coder, however, one could replace this again with an infinite while
         * loop and make the <1e-15 conditions into something like c*norm(A) or c*norm(b).
         */
        for(int iterations = 0; iterations < 300*A.getColumnDimension()*A.getRowDimension(); iterations++)
        {
            //System.out.println(z.size() + " " + p.size());
            Matrix w = A.transpose().times(b.minus(A.times(x)));
            //w.print(7, 5);
            if(z.size() == 0 || isAllNegative(w))
            {
                //System.out.println("Computation should break");
                //We are done with the computation. Break here!
                break;//Should break out of the outer while loop.
            }
            //Step 4
            int t = z.get(0);
            double max = w.get(t, 0);
            for (i = 1; i < z.size(); i++)
            {
                if (w.get(z.get(i), 0) > max)
                {
                    t = z.get(i);
                    max = w.get(z.get(i), 0);
                }
            }
            //Step 5
            p.add(t);
            z.remove((Integer)t);
            boolean allPositive = false;
            while(!allPositive)
            {
                //Step 6
                Matrix Ep = new Matrix(b.getRowDimension(),p.size());
                for (i = 0; i < p.size(); i++)
                    for (int j = 0; j < Ep.getRowDimension(); j++)
                        Ep.set(j, i, A.get(j, p.get(i)));
                Matrix Zprime = Ep.solve(b);
                Ep = null;
                Matrix Z = new Matrix(xm,xn);
                for (i = 0; i < p.size(); i++)
                    Z.set(p.get(i), 0, Zprime.get(i, 0));
                //Step 7
                allPositive = true;
                for (i = 0; i < p.size(); i++)
                    allPositive &= Z.get(p.get(i), 0) > 0;
                if (allPositive)
                    x = Z;
                else
                {
                    double alpha = Double.MAX_VALUE;
                    for (i = 0; i < p.size(); i++)
                    {
                        int q = p.get(i);
                        if (Z.get(q,0) <= 0)
                        {
                            double xq = x.get(q, 0);
                            if (xq / (xq - Z.get(q,0)) < alpha)
                                alpha = xq / (xq - Z.get(q,0));
                        }
                    }
                    //Finished getting alpha. Onto step 10
                    x = x.plus(Z.minus(x).times(alpha));
                    for (i = p.size() - 1; i >= 0; i--)
                        if (Math.abs(x.get(p.get(i),0)) < 1e-15)//Close enough to zero, no?
                            z.add(p.remove(i));
                }
            }
        }
        return x;
    }
    private static boolean isAllNegative(Matrix w)
    {
        boolean result = true;
        int m = w.getRowDimension();
        for (int i = 0; i < m; i++)
            result &= w.get(i, 0) <= 1e-15;
        return result;
    }
}
