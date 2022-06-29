package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties;

import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel.DataModel;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec.MassSpecModel;

public class PeakMeas {

    private double collectorWidthAMU;       // collector width in AMU
    private double theoreticalBeamWidthAMU; // theoretical beam width in AMU
    private double[] collectorLimits;         // mass range in collector at each magnet mass
    private double[] deltaMagnetMass;         // change in magnet mass between measurements
    private double[] beamWindow;// [min, max] mass range to model beam over


    private PeakMeas(DataModel data, MassSpecModel massSpec){

    }

    /*
     % collectorLimits is a matrix with two columns and the same
            % number of rows as magnet masses.  Each row contains the mass
            % range of the beam that is entering the collector (defined by
            % collectorWidthAMU)
     */
}
