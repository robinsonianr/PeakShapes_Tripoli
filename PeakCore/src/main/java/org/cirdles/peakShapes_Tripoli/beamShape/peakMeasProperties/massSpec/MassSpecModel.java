package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec;

import java.io.Serial;
import java.io.Serializable;

public class MassSpecModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 123455665660201L;

    private int collectorWidthMM;

    private int theoreticalBeamWidthMM;

    private int effectiveRadiusMagnetMM;

    private String[] faradayNames;

    private String[] ionCounterNames;

    private int amplifierResistance;

    private String massSpecName;


    private MassSpecModel(String massSpecName){
        this.massSpecName = massSpecName;

    }


}
