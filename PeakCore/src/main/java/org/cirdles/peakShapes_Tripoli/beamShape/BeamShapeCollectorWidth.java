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
        Matrix Basis, GB, WData, BeamWLS, TrimGMatrix, TrimMagnetMasses, peakMassIntensity, magnetMasses, matrixD,
                beamShape, BeamWNNLS, test1, test2, test3, test4, Gaugmented, measAugmented, wtsAugmented, beamPSpline, beamNNPspl, gAugmentedTest;

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
                for (int i = (int) firstMassIndexInside.get(0, 0) + 1; i < (int) lastMassIndexInside.get(0, 0); i++) {
                    gMatrix.set(iMass, i, deltaBeamMassInterp);

                }

                gMatrix.set(iMass, (int) firstMassIndexInside.get(0, 0), deltaBeamMassInterp / 2);
                gMatrix.set(iMass, (int) lastMassIndexInside.get(0, 0), deltaBeamMassInterp / 2);
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
        GB = TrimGMatrix.times(Basis);
        WData = new Matrix(MatLab.diag(MatLab.rDivide(MatLab.max(data.getMeasPeakIntensity().getArray(), 1), 1)));
        BeamWLS = (GB.transpose().times(WData.times(GB))).inverse().times((GB.transpose().times(WData.times(peakMassIntensity))));
        test1 = new Matrix(WData.chol().getL().getArray()).times(GB);
        test2 = new Matrix(WData.chol().getL().getArray()).times(peakMassIntensity);
        BeamWNNLS = MatLab.solveNNLS(test1, test2);

        // Smoothing spline
        double lambda = 1e-11;
        matrixD = new Matrix(MatLab.diff(MatLab.eye((int) (beamKnots + basisDegree)), orderDiff));

        Matrix lamdaD = matrixD.times(Math.sqrt(lambda));
        Gaugmented = MatLab.concatMatrix(GB, lamdaD);
        measAugmented = MatLab.concatMatrix(data.getMeasPeakIntensity(), new Matrix(MatLab.zeros((int) beamKnots + basisDegree - orderDiff, 1)));
        wtsAugmented = MatLab.blkDiag(WData, new Matrix(MatLab.eye((int) beamKnots + basisDegree - orderDiff)));
        beamPSpline = Gaugmented.transpose().times(wtsAugmented.times(Gaugmented)).inverse().times(Gaugmented.transpose().times(wtsAugmented.times(measAugmented)));
        test3 = new Matrix(wtsAugmented.chol().getL().getArray()).times(Gaugmented);
        test4 = new Matrix(wtsAugmented.chol().getL().getArray()).times(measAugmented);
        beamNNPspl = MatLab.solveNNLS(test3, test4);

        // Determine peak width
        beamShape = Basis.times(BeamWNNLS);
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
        this.leftAboveTheshold = new Matrix(MatLab.greaterThan(peakLeft.getArray(), thesholdIntensity));
        this.leftThesholdChange = leftAboveTheshold.getMatrix(1, leftAboveTheshold.getRowDimension() - 1, 0, 0).minus(leftAboveTheshold.getMatrix(0, leftAboveTheshold.getRowDimension() - 2, 0, 0));
        this.leftBoundary = MatLab.find(leftThesholdChange.getArray(), 1, "last")[0][0] + 1;


        this.peakRight = beamShape.getMatrix((int) maxBeamIndex, beamShape.getRowDimension() - 1, 0, 0);
        this.rightAboveThreshold = new Matrix(MatLab.greaterThan(peakRight.getArray(), thesholdIntensity));
        this.rightThesholdChange = rightAboveThreshold.getMatrix(0, rightAboveThreshold.getRowDimension() - 2, 0, 0).minus(rightAboveThreshold.getMatrix(1, rightAboveThreshold.getRowDimension() - 1, 0, 0));
        this.rightBoundary = MatLab.find(rightThesholdChange.getArray(), 1, "first")[0][0] + maxBeamIndex;

        this.measBeamWidthAMU = beamMassInterp.get(0, (int) rightBoundary) - beamMassInterp.get(0, (int) leftBoundary);
        this.measBeamWidthMM = measBeamWidthAMU * massSpec.getEffectiveRadiusMagnetMM() / data.getPeakCenterMass();

    }


}
