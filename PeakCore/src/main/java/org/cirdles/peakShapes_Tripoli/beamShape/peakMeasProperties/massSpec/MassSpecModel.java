package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec;

import org.cirdles.peakShapes_Tripoli.matlab.MatLab;
import org.cirdles.peakShapes_Tripoli.splineBasis.SplineBasisModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class MassSpecModel implements Serializable {

    private double collectorWidthMM;        //% collector aperture width (mm)
    private double theoreticalBeamWidthMM;  //% a priori estimate of beam width (mm)
    private double effectiveRadiusMagnetMM; //% effective radius of magnet (mm)
    private String[] faradayNames;            //% names of Faradays as string array
    private String[]  ionCounterNames;        //% names of ion counters as string array
    private double[][] amplifierResistance;    // % resistance of Faraday amplifiers (ohms)

    @Serial
    private static final long serialVersionUID = 123455665660201L;

    private String massSpecName;


    private MassSpecModel(String massSpecName){
        this.massSpecName = massSpecName;
        MatLab matLab = new MatLab();

        switch (massSpecName){
            case "PhoenixKansas_1e12" -> {
                this.collectorWidthMM = 0.95135;
                this.theoreticalBeamWidthMM = 0.35;
                this.effectiveRadiusMagnetMM = 540;
                this.faradayNames = new String[]{"L5", "L4", "L3", "L2", "Ax", "H1", "H2", "H3", "H4"};
                this.ionCounterNames = new String[]{"PM", "SEM"};
                this.amplifierResistance = matLab.multMatrix(matLab.ones(1, 9), 1e12);
            }
            case "PhoenixKansas_1e11" -> {
                this.collectorWidthMM = 0.95135;
                this.theoreticalBeamWidthMM = 0.35;
                this.effectiveRadiusMagnetMM = 540;
                this.faradayNames = new String[]{"L5", "L4", "L3", "L2", "Ax", "H1", "H2", "H3", "H4"};
                this.ionCounterNames = new String[]{"PM", "SEM"};
                this.amplifierResistance = matLab.multMatrix(matLab.ones(1, 9), 1e11);
            }
            case "" -> System.out.println("Mass spectrometer not recognized");
        }

    }


    public static  MassSpecModel initializeMassSpec(String name){
        return new MassSpecModel(name);
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

    public double[][] getAmplifierResistance() {
        return amplifierResistance;
    }

    public String[] getFaradayNames() {
        return faradayNames;
    }

    public String[] getIonCounterNames() {
        return ionCounterNames;
    }
}
