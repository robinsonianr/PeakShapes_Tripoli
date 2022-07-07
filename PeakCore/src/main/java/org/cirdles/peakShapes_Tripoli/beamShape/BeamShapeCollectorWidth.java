package org.cirdles.peakShapes_Tripoli.beamShape;


import jama.Matrix;
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

    private double maxBeam, maxBeamIndex, thesholdIntensity, leftBoundary, rightBoundary, measBeamWidthAMU, measBeamWidthMM;
    private Matrix peakLeft, leftAboveTheshold, leftThesholdChange, peakRight, rightAboveThreshold, rightThesholdChange;


    public BeamShapeCollectorWidth(Path fileName, MassSpecModel massSpec) throws IOException {
        data = new DataModel(fileName);
        this.massSpec = massSpec;
        data.calcCollectorWidthAMU(massSpec);
        data.calcBeamWidthAMU(massSpec);
        peakMeas = PeakMeas.initializePeakMeas(data, massSpec);


    }

    public void calcBeamShapeCollectorWidth() {
        Matrix Basis, gb, WData, BeamWLS, TrimGMatrix, TrimMagnetMasses, peakMassIntensity, magnetMasses, matrixD, beamShape, BeamWNNLS, test1, test2;

        // Spline basis Basis

        int basisDegree = 3;
        int orderDiff = 2;
        double beamKnots = Math.ceil(peakMeas.getBeamWindow() / peakMeas.getDeltaMagnetMass()) - (2 * basisDegree);
        int nInterp = 1000;

        double xLower = data.getPeakCenterMass() - peakMeas.getBeamWindow() / 2;
        double xUpper = data.getPeakCenterMass() + peakMeas.getBeamWindow() / 2;


        Matrix beamMassInterp = new Matrix(MatLab.linspace(xLower, xUpper, nInterp));
        Basis = SplineBasisModel.bBase(beamMassInterp, xLower, xUpper, beamKnots, basisDegree);
        double deltaBeamMassInterp = beamMassInterp.get(0, 1) - beamMassInterp.get(0, 0);


        // Calculate integration matrix G, depends on matrix B and data


        int numMagnetMasses = data.getMagnetMasses().getRowDimension();
        Matrix gMatrix = new Matrix(MatLab.zeros(numMagnetMasses, nInterp));

        for (int iMass = 0; iMass < numMagnetMasses; iMass++) {
            Matrix massesInCollector = new Matrix(MatLab.greatEqual(beamMassInterp.getArray(), peakMeas.getCollectorLimits().get(iMass, 0))).arrayTimes(new Matrix(MatLab.lessEqual(beamMassInterp.getArray(), peakMeas.getCollectorLimits().get(iMass, 1))));
            Matrix firstMassIndexInside;
            Matrix lastMassIndexInside;
            if (!(MatLab.find(massesInCollector.getArray(), 1, "first")[0][0] == 0 && MatLab.find(massesInCollector.getArray(), 1, "last")[0][0] == 0)) {
                firstMassIndexInside = new Matrix(MatLab.find(massesInCollector.getArray(), 1, "first"));
                lastMassIndexInside = new Matrix(MatLab.find(massesInCollector.getArray(), 1, "last"));
                for (int i = (int) firstMassIndexInside.get(0, 0) + 1; i < (int) lastMassIndexInside.get(0, 0) - 1; i++) {
                    gMatrix.set(iMass, i, deltaBeamMassInterp);

                }

                gMatrix.set(iMass, (int)firstMassIndexInside.get(0, 0),  deltaBeamMassInterp /2);
                gMatrix.set(iMass, (int)lastMassIndexInside.get(0, 0),  deltaBeamMassInterp /2);
            }


        }

        // Trim data
        int newDataSet = 0;
        Matrix hasModelBeam = new Matrix(MatLab.any(gMatrix.getArray(), 2));
        for (int i = 0; i < hasModelBeam.getRowDimension(); i++) {
            for (int j = 0; j < hasModelBeam.getColumnDimension(); j++) {
                if (hasModelBeam.get(i, 0) == 1) {
                    newDataSet++;
                }
            }
        }
        double[][] trimGMatrix = new double[newDataSet][gMatrix.getColumnDimension()];
        int j = 0;
        for (int i = 0; i < gMatrix.getRowDimension(); i++) {
            if (hasModelBeam.get(i, 0) > 0) {
                trimGMatrix[j] = gMatrix.getArray()[i];
                j++;
            }
        }
        TrimGMatrix = new Matrix(trimGMatrix);

        double[][] trimMagnetMasses = new double[newDataSet][data.getMagnetMasses().getRowDimension()];
        int h = 0;

        for (int i = 0; i < data.getMagnetMasses().getRowDimension(); i++) {
            if (hasModelBeam.get(i, 0) > 0) {
                trimMagnetMasses[h] = data.getMagnetMasses().getArray()[i];
                h++;
            }
        }

        double[][] trimPeakIntensity = new double[newDataSet][data.getMagnetMasses().getRowDimension()];
        int k = 0;
        for (int i = 0; i < data.getMeasPeakIntensity().getRowDimension(); i++) {
            if (hasModelBeam.get(i, 0) > 0) {
                trimPeakIntensity[k] = data.getMeasPeakIntensity().getArray()[i];
                k++;
            }
        }

        data.setMagnetMasses(new Matrix(trimMagnetMasses));
        data.setMeasPeakIntensity(new Matrix(trimPeakIntensity));


        peakMassIntensity = data.getMeasPeakIntensity();
        magnetMasses = data.getMagnetMasses();


        // WLS and NNLS
        gb = TrimGMatrix.times(Basis);
        WData = new Matrix(MatLab.diag(MatLab.rDivide(MatLab.max(data.getMeasPeakIntensity().getArray(), 1), 1)));
        BeamWLS = (gb.transpose().times(WData.times(gb))).inverse().times((gb.transpose().times(WData.times(peakMassIntensity))));
        test1 = new Matrix(WData.chol().getL().getArray()).times(gb);
        test2 = new Matrix(WData.chol().getL().getArray()).times(peakMassIntensity);
        BeamWNNLS = solveNNLS(test1, test2);

        // Smoothing spline
        double lambda = 1e-11;
        matrixD = new Matrix(MatLab.diff(MatLab.eye((int) (beamKnots + basisDegree)), orderDiff));
        beamShape = Basis.times(BeamWNNLS);

        // Determine peak width

        this.maxBeam = beamShape.normInf();
        int index = 0;
        for (int i = 0; i < beamShape.getRowDimension(); i++) {
            for (int l = 0; l < beamShape.getColumnDimension(); l++) {
                if (beamShape.get(i, l) == maxBeam) {
                    this.maxBeamIndex = index;
                    break;
                }
                index++;
            }
        }
        this.thesholdIntensity = maxBeam * (0.01);

        this.peakLeft = beamShape.getMatrix(0, (int) maxBeamIndex - 1, 0, 0);
        this.leftAboveTheshold = new Matrix(MatLab.greatEqual(peakLeft.getArray(), thesholdIntensity));
        this.leftThesholdChange = leftAboveTheshold.getMatrix(1, leftAboveTheshold.getRowDimension() - 1, 0, 0).minus(leftAboveTheshold.getMatrix(0, leftAboveTheshold.getRowDimension() - 2, 0, 0));
        this.leftBoundary = MatLab.find(leftThesholdChange.getArray(), 1, "last")[0][0] + 1;


        this.peakRight = beamShape.getMatrix((int) maxBeamIndex, beamShape.getRowDimension() - 1, 0, 0);
        this.rightAboveThreshold = new Matrix(MatLab.greatEqual(peakRight.getArray(), thesholdIntensity));
        this.rightThesholdChange = rightAboveThreshold.getMatrix(0, rightAboveThreshold.getRowDimension() - 2, 0, 0).minus(rightAboveThreshold.getMatrix(1, rightAboveThreshold.getRowDimension() - 1, 0, 0));
        this.rightBoundary = MatLab.find(rightThesholdChange.getArray(), 1, "first")[0][0] + maxBeamIndex - 1;

        this.measBeamWidthAMU = beamMassInterp.get(0, (int)rightBoundary) - beamMassInterp.get(0, (int)leftBoundary);
        this.measBeamWidthMM = measBeamWidthAMU * massSpec.getEffectiveRadiusMagnetMM() / data.getPeakCenterMass();
        
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
