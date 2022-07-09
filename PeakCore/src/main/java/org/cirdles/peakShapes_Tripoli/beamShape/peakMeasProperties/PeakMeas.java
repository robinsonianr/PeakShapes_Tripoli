package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties;

import jama.Matrix;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel.DataModel;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec.MassSpecModel;

public class PeakMeas {

    private double collectorWidthAMU;       // collector width in AMU
    private double theoreticalBeamWidthAMU; // theoretical beam width in AMU
    private Matrix collectorLimits;         // mass range in collector at each magnet mass
    private double deltaMagnetMass;         // change in magnet mass between measurements
    private double beamWindow;// [min, max] mass range to model beam over


    private PeakMeas(DataModel data, MassSpecModel massSpec) {
        this.collectorWidthAMU = calcWidthAMU(data, massSpec, massSpec.getCollectorWidthMM());
        this.theoreticalBeamWidthAMU = calcWidthAMU(data, massSpec, massSpec.getTheoreticalBeamWidthMM());
        double[][] collector = new double[data.getMagnetMasses().getRowDimension()][2];

        // collectorLimits is a matrix with two columns and the same
        // number of rows as magnet masses.  Each row contains the mass
        // range of the beam that is entering the collector (defined by
        // collectorWidthAMU)
        for (int i = 0; i < collector.length; i++) {
            for (int j = 0; j < collector[0].length; j++) {
                if (j == 0) {
                    collector[i][j] = data.getMagnetMasses().get(i, 0) - data.getCollectorWidthAMU() / 2;
                } else {
                    collector[i][j] = data.getMagnetMasses().get(i, 0) + data.getCollectorWidthAMU() / 2;
                }
            }
        }
        this.collectorLimits = new Matrix(collector);

        this.deltaMagnetMass = data.getMagnetMasses().get(1, 0) - data.getMagnetMasses().get(0, 0);
        this.beamWindow = data.getTheoreticalBeamWidthAMU() * 2;


    }

    public static PeakMeas initializePeakMeas(DataModel data, MassSpecModel massSpec) {
        return new PeakMeas(data, massSpec);
    }


    public double calcWidthAMU(DataModel data, MassSpecModel massSpec, double widthMM) {
        return data.getPeakCenterMass() / massSpec.getEffectiveRadiusMagnetMM() * widthMM;
    }

    public double getTheoreticalBeamWidthAMU() {
        return theoreticalBeamWidthAMU;
    }

    public double getCollectorWidthAMU() {
        return collectorWidthAMU;
    }

    public double getBeamWindow() {
        return beamWindow;
    }

    public double getDeltaMagnetMass() {
        return deltaMagnetMass;
    }

    public Matrix getCollectorLimits() {
        return collectorLimits;
    }
}
