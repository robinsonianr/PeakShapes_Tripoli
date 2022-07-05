package org.cirdles.peakShapes_Tripoli.beamShape;


import jama.CholeskyDecomposition;
import jama.LUDecomposition;
import jama.Matrix;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.PeakMeas;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel.DataModel;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec.MassSpecModel;
import org.cirdles.peakShapes_Tripoli.matlab.MatLab;
import org.cirdles.peakShapes_Tripoli.splineBasis.SplineBasisModel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

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
        Matrix Basis, gb, WData, BeamWLS, TrimGMatrix, TrimMagnetMasses, peakMassIntensity, magnetMasses, D, beamShape, BeamWNNLS;


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
        double deltaBeamMassInterp = beamMassInterp[0][1] - beamMassInterp[0][0];
        Basis = new Matrix(basis);


        // calculate integration matrix G, depends on B, data


        int numMagnetMasses = data.getMagnetMasses().length;
        double[][] gMatrix = matLab.zeros(numMagnetMasses, nInterp);
        double[][] massesInCollector = null;
        for (int iMass = 0; iMass < numMagnetMasses; iMass++) {
            massesInCollector = matLab.andMatrix(matLab.greatEqual(beamMassInterp, peakMeas.getCollectorLimits()[iMass][0]), matLab.lessEqual(beamMassInterp, peakMeas.getCollectorLimits()[iMass][1]));

            double[][] firstMassIndexInside = matLab.find(massesInCollector, 1, "first");
            double[][] lastMassIndexInside = matLab.find(massesInCollector, 1, "last");

            for (int i = (int) firstMassIndexInside[0][0] + 1; i < (int) lastMassIndexInside[0][0] - 1; i++) {
                gMatrix[iMass][i] = deltaBeamMassInterp;

            }
            gMatrix[iMass][(int) firstMassIndexInside[0][0]] = deltaBeamMassInterp / 2;
            gMatrix[iMass][(int) lastMassIndexInside[0][0]] = deltaBeamMassInterp / 2;

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
        double[][] trimGMatrix = new double[newDataSet + 1][gMatrix[0].length];
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


        peakMassIntensity = Matrix.constructWithCopy(data.getMeasPeakIntensity().clone());
        magnetMasses = new Matrix(data.getMagnetMasses());


        // WLS and NNLS
        double[][] GB = matLab.multMatrix(trimGMatrix, basis);
        double[][] wData = matLab.diag(matLab.rDivide(matLab.max(data.getMeasPeakIntensity(), 1), 1));
        //double[][] beamWLS = matLab.mLDivide(matLab.multMatrix(matLab.transpose(GB), matLab.multMatrix(wData, GB)) , matLab.multMatrix(matLab.transpose(GB), matLab.multMatrix(wData, data.getMeasPeakIntensity())));
        gb = TrimGMatrix.times(Basis);
        WData = new Matrix(wData);
//        BeamWLS = gb.transpose().times(WData.times(gb)).arrayLeftDivide(gb.transpose().times(WData.times(peakMassIntensity)));
        BeamWNNLS = new LUDecomposition(new CholeskyDecomposition(WData).solve(gb)).solve(new CholeskyDecomposition(WData).solve(peakMassIntensity));

        // smoothing spline
        double lambda = 1e-11;
        D = new Matrix(matLab.diff(matLab.eye((int) (beamKnots + basisDegree)), orderDiff));

        beamShape = Basis.times(BeamWNNLS);

        return beamShape;
    }
}
