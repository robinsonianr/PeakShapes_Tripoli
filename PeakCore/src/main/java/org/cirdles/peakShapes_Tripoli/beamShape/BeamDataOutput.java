package org.cirdles.peakShapes_Tripoli.beamShape;

import jama.Matrix;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.PeakMeas;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel.DataModel;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec.MassSpecModel;
import org.cirdles.peakShapes_Tripoli.matlab.MatLab;
import org.cirdles.peakShapes_Tripoli.splineBasis.SplineBasisModel;
import org.cirdles.peakShapes_Tripoli.visualizationUtilities.LinePlot;


import java.io.IOException;
import java.nio.file.Path;

public class BeamDataOutput {

    private static double[] massData;
    private static double[] intensityData;
    private static double leftBoundary;
    private static double rightBoundary;

    public static LinePlot modelTest(Path dataFile, String option) throws IOException {
        DataModel data = new DataModel(dataFile);
        MassSpecModel massSpec = MassSpecModel.initializeMassSpec("PhoenixKansas_1e12");
        data.calcCollectorWidthAMU(massSpec);
        data.calcBeamWidthAMU(massSpec);

        return gatherBeamWidth(massSpec, data, option);
    }


    static LinePlot gatherBeamWidth(MassSpecModel massSpec, DataModel data, String option) {
        PeakMeas peakMeas = PeakMeas.initializePeakMeas(data, massSpec);
        LinePlot linePlot = null;

        double maxBeam, maxBeamIndex, thesholdIntensity;
        // Spline basis Basis

        int basisDegree = 3;
        // int orderDiff = 2;
        double beamKnots = Math.ceil(peakMeas.getBeamWindow() / peakMeas.getDeltaMagnetMass()) - (2 * basisDegree);
        int nInterp = 1000;

        double xLower = data.getPeakCenterMass() - peakMeas.getBeamWindow() / 2;
        double xUpper = data.getPeakCenterMass() + peakMeas.getBeamWindow() / 2;


        Matrix beamMassInterp = MatLab.linspace(xLower, xUpper, nInterp);
        Matrix Basis = SplineBasisModel.bBase(beamMassInterp, xLower, xUpper, beamKnots, basisDegree);
        double deltaBeamMassInterp = beamMassInterp.get(0, 1) - beamMassInterp.get(0, 0);


        // Calculate integration matrix G, depends on matrix B and data
        int numMagnetMasses = data.getMagnetMasses().getRowDimension();
        Matrix gMatrix = new Matrix(numMagnetMasses, nInterp, 0);


        for (int iMass = 0; iMass < numMagnetMasses; iMass++) {
            Matrix term1 = MatLab.greaterOrEqual(beamMassInterp, peakMeas.getCollectorLimits().get(iMass, 0));
            Matrix term2 = MatLab.lessOrEqual(beamMassInterp, peakMeas.getCollectorLimits().get(iMass, 1));
            Matrix massesInCollector = term1.arrayTimes(term2);
            Matrix firstMassIndexInside;
            Matrix lastMassIndexInside;
            if (!(MatLab.find(massesInCollector, 1, "first").get(0, 0) == 0 && MatLab.find(massesInCollector, 1, "last").get(0, 0) == 0)) {
                firstMassIndexInside = MatLab.find(massesInCollector, 1, "first");
                lastMassIndexInside = MatLab.find(massesInCollector, 1, "last");
                for (int i = (int) firstMassIndexInside.get(0, 0) + 1; i < (int) lastMassIndexInside.get(0, 0); i++) {
                    gMatrix.set(iMass, i, deltaBeamMassInterp);

                }

                gMatrix.set(iMass, (int) firstMassIndexInside.get(0, 0), deltaBeamMassInterp / 2);
                gMatrix.set(iMass, (int) lastMassIndexInside.get(0, 0), deltaBeamMassInterp / 2);
            }


        }

        // Trim data
        int newDataSet = 0;
        Matrix hasModelBeam = MatLab.any(gMatrix, 2);
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
        Matrix TrimGMatrix = new Matrix(trimGMatrix);

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
        massData = new Matrix(trimMagnetMasses).transpose().getArray()[0];
        intensityData = new Matrix(trimPeakIntensity).transpose().getArray()[0];


        // WLS and NNLS
        Matrix GB = TrimGMatrix.times(Basis);
        Matrix WData = MatLab.diag(MatLab.rDivide(MatLab.max(data.getMeasPeakIntensity(), 1), 1));
        // Matrix BeamWLS = (GB.transpose().times(WData.times(GB))).inverse().times((GB.transpose().times(WData.times(data.getMeasPeakIntensity()))));
        Matrix test1 = new Matrix(WData.chol().getL().getArray()).times(GB);
        Matrix test2 = new Matrix(WData.chol().getL().getArray()).times(data.getMeasPeakIntensity());
        Matrix BeamWNNLS = MatLab.solveNNLS(test1, test2);

        // Smoothing spline
//        double lambda = 1e-11;
//        int size = (int) (beamKnots + basisDegree);
//        Matrix matrixD = MatLab.diff(Matrix.identity(size, size), orderDiff);
//
//        Matrix lambdaD = matrixD.times(Math.sqrt(lambda));
//        Matrix gAugmented = MatLab.concatMatrix(GB, lambdaD);
//        int zeroSize = (int) beamKnots + basisDegree - orderDiff;
//        Matrix measAugmented = MatLab.concatMatrix(data.getMeasPeakIntensity(), new Matrix(zeroSize, 1, 0));
//        int eSize = (int) beamKnots + basisDegree - orderDiff;
//        Matrix wtsAugmented = MatLab.blockDiag(WData, Matrix.identity(eSize, eSize));
//        Matrix beamPSpline = gAugmented.transpose().times(wtsAugmented.times(gAugmented)).inverse().times(gAugmented.transpose().times(wtsAugmented.times(measAugmented)));
//        Matrix test3 = new Matrix(wtsAugmented.chol().getL().getArray()).times(gAugmented);
//        Matrix test4 = new Matrix(wtsAugmented.chol().getL().getArray()).times(measAugmented);
//         does not compute on Ryan data file
//        Matrix beamNNPspl = MatLab.solveNNLS(test3, test4);

        // Determine peak width
        Matrix beamShape = Basis.times(BeamWNNLS);
        maxBeam = beamShape.normInf();
        maxBeamIndex = 0;
        int index = 0;
        for (int i = 0; i < beamShape.getRowDimension(); i++) {
            for (int l = 0; l < beamShape.getColumnDimension(); l++) {
                if (beamShape.get(i, l) == maxBeam) {
                    maxBeamIndex = index;
                    break;
                }
                index++;
            }
        }
        thesholdIntensity = maxBeam * (0.01);

        Matrix peakLeft = beamShape.getMatrix(0, (int) maxBeamIndex - 1, 0, 0);
        Matrix leftAboveTheshold = MatLab.greaterThan(peakLeft, thesholdIntensity);
        Matrix leftThesholdChange = leftAboveTheshold.getMatrix(1, leftAboveTheshold.getRowDimension() - 1, 0, 0).minus(leftAboveTheshold.getMatrix(0, leftAboveTheshold.getRowDimension() - 2, 0, 0));
        leftBoundary = MatLab.find(leftThesholdChange, 1, "last").get(0, 0) + 1;


        Matrix peakRight = beamShape.getMatrix((int) maxBeamIndex, beamShape.getRowDimension() - 1, 0, 0);
        Matrix rightAboveThreshold = MatLab.greaterThan(peakRight, thesholdIntensity);
        Matrix rightThesholdChange = rightAboveThreshold.getMatrix(0, rightAboveThreshold.getRowDimension() - 2, 0, 0).minus(rightAboveThreshold.getMatrix(1, rightAboveThreshold.getRowDimension() - 1, 0, 0));
        rightBoundary = MatLab.find(rightThesholdChange, 1, "first").get(0, 0) + maxBeamIndex;

//        double measBeamWidthAMU = beamMassInterp.get(0, (int) rightBoundary) - beamMassInterp.get(0, (int) leftBoundary);
//        double measBeamWidthMM = measBeamWidthAMU * massSpec.getEffectiveRadiusMagnetMM() / data.getPeakCenterMass();


        Matrix gBeam = TrimGMatrix.times(beamShape);

        if (option.equalsIgnoreCase("beamShape")) {

            linePlot = LinePlot.initializeLinePlot(beamMassInterp.getArray()[0], beamShape.transpose().getArray()[0]);

        } else if (option.equalsIgnoreCase("gBeam")) {
            linePlot = LinePlot.initializeLinePlot(data.getMagnetMasses().transpose().getArray()[0], gBeam.transpose().getArray()[0]);
        }

        return linePlot;
    }


    public static double[] getIntensityData() {
        return intensityData;
    }

    public static double[] getMassData() {
        return massData;
    }

    public static double getLeftBoundary() {
        return leftBoundary;
    }

    public static double getRightBoundary() {
        return rightBoundary;
    }
}
