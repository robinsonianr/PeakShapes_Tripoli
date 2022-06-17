package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MassSpecFactory {
    private int collectorWidthMM;

    private int theoreticalBeamWidthMM;

    private int effectiveRadiusMagnetMM;

    private String[] faradayNames;

    private String[] ionCounterNames;

    private int amplifierResistance;

    public static Map<String, MassSpecModel> massSpecModelMap = new LinkedHashMap<>();

    static {
        MassSpecModel phoenixKansas_1e12 = MassSpecModel.initializeMassSpec("PhoenixKansas_1e12");
        massSpecModelMap.put(phoenixKansas_1e12.getMassSpecName(), phoenixKansas_1e12);


    }

}
