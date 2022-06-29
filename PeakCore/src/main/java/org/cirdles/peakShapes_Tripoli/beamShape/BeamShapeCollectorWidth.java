package org.cirdles.peakShapes_Tripoli.beamShape;


import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.PeakMeas;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel.DataModel;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec.MassSpecModel;
import org.cirdles.peakShapes_Tripoli.matlab.MatLab;
import org.cirdles.peakShapes_Tripoli.splineBasis.SplineBasisModel;

import java.io.IOException;
import java.nio.file.Path;

public class BeamShapeCollectorWidth {
    DataModel data;
    PeakMeas peakMeas;

    MassSpecModel massSpec;

    MatLab matLab = new MatLab();


    private BeamShapeCollectorWidth(Path fileName, MassSpecModel massSpec) throws IOException {
        data = new DataModel(fileName);
        this.massSpec = massSpec;
        peakMeas = PeakMeas.initializePeakMeas(data, massSpec);

    }


    public double[][] getBeamShape() {
        double[][] beamShape;

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

        // calculate integration matrix G, depends on B, data

        int numMagnetMasses = data.getMagnetMasses().size();

        return null;
    }
}
