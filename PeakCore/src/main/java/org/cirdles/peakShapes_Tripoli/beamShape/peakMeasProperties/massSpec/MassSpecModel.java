package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec;

import java.io.Serial;
import java.io.Serializable;

public class MassSpecModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 123455665660201L;
    private final String massSpecName;
    private double collectorWidthMM;        //% collector aperture width (mm)
    //    private String[] faradayNames;            //% names of Faradays as string array
//    private String[] ionCounterNames;        //% names of ion counters as string array
//    private Matrix amplifierResistance;    // % resistance of Faraday amplifiers (ohms)
    private double theoreticalBeamWidthMM;  //% a priori estimate of beam width (mm)
    private double effectiveRadiusMagnetMM; //% effective radius of magnet (mm)


    private MassSpecModel(String massSpecName) {
        this.massSpecName = massSpecName;
        initializeMassSpecModel(massSpecName);

    }

    public static MassSpecModel initializeMassSpec(String name) {
        return new MassSpecModel(name);
    }

    public void initializeMassSpecModel(String massSpecName) {

        switch (massSpecName) {
            case "PhoenixKansas_1e12", "PhoenixKansas_1e11" -> {
                this.collectorWidthMM = 0.95135;
                this.theoreticalBeamWidthMM = 0.35;
                this.effectiveRadiusMagnetMM = 540;
//                this.faradayNames = new String[]{"L5", "L4", "L3", "L2", "Ax", "H1", "H2", "H3", "H4"};
//                this.ionCounterNames = new String[]{"PM", "SEM"};
//                this.amplifierResistance = new Matrix(1, 9, 1).times(1e12);
//                MatLab.multMatrix(MatLab.ones(1, 9), 1e12);
            }
            //                this.amplifierResistance = new Matrix(1, 9, 1).times(1e11);
            //                MatLab.multMatrix(MatLab.ones(1, 9), 1e11);
            case "" -> System.out.println("Mass spectrometer not recognized");
        }
    }

    public String getMassSpecName() {
        return massSpecName;
    }

    public double getCollectorWidthMM() {
        return collectorWidthMM;
    }

    public double getEffectiveRadiusMagnetMM() {
        return effectiveRadiusMagnetMM;
    }

    public double getTheoreticalBeamWidthMM() {
        return theoreticalBeamWidthMM;
    }

//    public double[][] getAmplifierResistance() {
//        return amplifierResistance;
//    }
//
//    public String[] getFaradayNames() {
//        return faradayNames;
//    }
//
//    public String[] getIonCounterNames() {
//        return ionCounterNames;
//    }
}
