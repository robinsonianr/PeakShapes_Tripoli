package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec;

import java.io.Serial;
import java.io.Serializable;

public class MassSpecModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 123455665660201L;

    private String massSpecName;


    private MassSpecModel(String massSpecName){
        this.massSpecName = massSpecName;

    }


    public static  MassSpecModel initializeMassSpec(String name){
        return new MassSpecModel(name);
    }


    public String getMassSpecName() {
        return massSpecName;
    }
}
