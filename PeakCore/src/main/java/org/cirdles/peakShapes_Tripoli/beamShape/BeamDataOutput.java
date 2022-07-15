package org.cirdles.peakShapes_Tripoli.beamShape;

import jama.Matrix;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.PeakMeas;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel.DataModel;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec.MassSpecModel;
import org.cirdles.peakShapes_Tripoli.matlab.MatLab;
import org.cirdles.peakShapes_Tripoli.splineBasis.SplineBasisModel;
import org.cirdles.peakShapes_Tripoli.visualizationUtilities.Histogram;

import java.io.IOException;
import java.nio.file.Path;

public class BeamDataOutput {

    public static Histogram modelTest(Path dataFile) throws IOException {
        DataModel data = new DataModel(dataFile);
        MassSpecModel massSpec = MassSpecModel.initializeMassSpec("PhoenixKansas_1e12");
        data.calcCollectorWidthAMU(massSpec);
        data.calcBeamWidthAMU(massSpec);

        return gatherBeamWidth(massSpec, data);
    }


    static Histogram gatherBeamWidth(MassSpecModel massSpec, DataModel data) {
        PeakMeas peakMeas = PeakMeas.initializePeakMeas(data, massSpec);


        double maxBeam, maxBeamIndex, thesholdIntensity;
        // Spline basis Basis

        int basisDegree = 3;
        int orderDiff = 2;
        double beamKnots = Math.ceil(peakMeas.getBeamWindow() / peakMeas.getDeltaMagnetMass()) - (2 * basisDegree);
        int nInterp = 1000;

        double xLower = data.getPeakCenterMass() - peakMeas.getBeamWindow() / 2;
        double xUpper = data.getPeakCenterMass() + peakMeas.getBeamWindow() / 2;


        Matrix beamMassInterp = new Matrix(MatLab.linspace(xLower, xUpper, nInterp));
        Matrix Basis = SplineBasisModel.bBase(beamMassInterp, xLower, xUpper, beamKnots, basisDegree);
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


        // WLS and NNLS
        Matrix GB = TrimGMatrix.times(Basis);
        Matrix WData = new Matrix(MatLab.diag(MatLab.rDivide(MatLab.max(data.getMeasPeakIntensity().getArray(), 1), 1)));
        Matrix BeamWLS = (GB.transpose().times(WData.times(GB))).inverse().times((GB.transpose().times(WData.times(data.getMeasPeakIntensity()))));
        Matrix test1 = new Matrix(WData.chol().getL().getArray()).times(GB);
        Matrix test2 = new Matrix(WData.chol().getL().getArray()).times(data.getMeasPeakIntensity());
        Matrix BeamWNNLS = MatLab.solveNNLS(test1, test2);

        // Smoothing spline
        double lambda = 1e-11;
        Matrix matrixD = new Matrix(MatLab.diff(MatLab.eye((int) (beamKnots + basisDegree)), orderDiff));
        Matrix lambdaD = matrixD.times(Math.sqrt(lambda));
        Matrix gAugmented = MatLab.concatMatrix(GB, lambdaD);
        Matrix measAugmented = MatLab.concatMatrix(data.getMeasPeakIntensity(), new Matrix(MatLab.zeros((int) beamKnots + basisDegree - orderDiff, 1)));
        Matrix wtsAugmented = MatLab.blkDiag(WData, new Matrix(MatLab.eye((int) beamKnots + basisDegree - orderDiff)));
        Matrix beamPSpline = gAugmented.transpose().times(wtsAugmented.times(gAugmented)).inverse().times(gAugmented.transpose().times(wtsAugmented.times(measAugmented)));
        Matrix test3 = new Matrix(wtsAugmented.chol().getL().getArray()).times(gAugmented);
        Matrix test4 = new Matrix(wtsAugmented.chol().getL().getArray()).times(measAugmented);
        Matrix beamNNPspl = MatLab.solveNNLS(test3, test4);

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
        Matrix leftAboveTheshold = new Matrix(MatLab.greaterThan(peakLeft.getArray(), thesholdIntensity));
        Matrix leftThesholdChange = leftAboveTheshold.getMatrix(1, leftAboveTheshold.getRowDimension() - 1, 0, 0).minus(leftAboveTheshold.getMatrix(0, leftAboveTheshold.getRowDimension() - 2, 0, 0));
        double leftBoundary = MatLab.find(leftThesholdChange.getArray(), 1, "last")[0][0] + 1;


        Matrix peakRight = beamShape.getMatrix((int) maxBeamIndex, beamShape.getRowDimension() - 1, 0, 0);
        Matrix rightAboveThreshold = new Matrix(MatLab.greaterThan(peakRight.getArray(), thesholdIntensity));
        Matrix rightThesholdChange = rightAboveThreshold.getMatrix(0, rightAboveThreshold.getRowDimension() - 2, 0, 0).minus(rightAboveThreshold.getMatrix(1, rightAboveThreshold.getRowDimension() - 1, 0, 0));
        double rightBoundary = MatLab.find(rightThesholdChange.getArray(), 1, "first")[0][0] + maxBeamIndex;

        double measBeamWidthAMU = beamMassInterp.get(0, (int) rightBoundary) - beamMassInterp.get(0, (int) leftBoundary);
        double measBeamWidthMM = measBeamWidthAMU * massSpec.getEffectiveRadiusMagnetMM() / data.getPeakCenterMass();
        double[][] ensembleBeam = new double[1][beamShape.getRowDimension() * beamShape.getColumnDimension()];
        int ensembleIndex = 0;
        for (int i = 0; i < beamShape.getRowDimension(); i++) {
            for (int l = 0; l < beamShape.getColumnDimension(); l++) {
                ensembleBeam[0][ensembleIndex] = beamShape.get(i, l);
                ensembleIndex++;
            }
        }

        Histogram histogram = Histogram.initializeHistogram(ensembleBeam[0], 20);
        //TODO add data to descriptive histogram

        return histogram;
    }
}
