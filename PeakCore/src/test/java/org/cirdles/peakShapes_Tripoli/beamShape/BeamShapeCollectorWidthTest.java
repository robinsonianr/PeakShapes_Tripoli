package org.cirdles.peakShapes_Tripoli.beamShape;

import jama.Matrix;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.peakShapes_Tripoli.PeakShapes_Tripoli;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec.MassSpecModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeamShapeCollectorWidthTest {
    private static final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(PeakShapes_Tripoli.class);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getBeamShape() throws IOException {
        Matrix beam;
        System.err.println("Testing Example Data DV");
        Path dataFile = RESOURCE_EXTRACTOR.extractResourceAsFile("/org/cirdles/peakShapes_Tripoli/dataProccessors/HY30ZK z10 Pb-1004-PKC-205Pb-PM-S2B7C1.TXT").toPath();
        MassSpecModel massSpec = MassSpecModel.initializeMassSpec("PhoenixKansas_1e12");
        BeamShapeCollectorWidth beamShape = new BeamShapeCollectorWidth(dataFile, massSpec);
        beamShape.calcBeamShapeCollectorWidth();


        assertEquals(Double.parseDouble(String.format("%.4g%n", beamShape.getMeasBeamWidthAMU())), 0.1567);
        assertEquals(Double.parseDouble(String.format("%.4g%n", beamShape.getMeasBeamWidthMM())), 0.4127);


    }
}