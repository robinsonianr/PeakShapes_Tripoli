package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel;

import jama.Matrix;
import org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.massSpec.MassSpecModel;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataModel {

    private Matrix magnetMasses;         // vector of masses for intensity measurements
    private Matrix measPeakIntensity;    // vector of corresponding peak intensities
    private double peakCenterMass;          // mass at center of peak from header
    private String integPeriodMS;          // integration period of measurements in ms
    private String MassID;                  // name of peak getting centered e.g. "205Pb"
    private String detectorName;            // name of detector as string e.g. "L2"
    private double collectorWidthAMU;       // width of collector aperture in AMU at center mass
    private double theoreticalBeamWidthAMU; // width of beam in AMU at center mass

    public DataModel(Path inputFile) throws IOException {
        List<String> contentsByLine = new ArrayList<>(Files.readAllLines(inputFile, Charset.defaultCharset()));

        List<String[]> headerLine = new ArrayList<>();
        List<String[]> columnNames = new ArrayList<>();
        List<Double> masses = new ArrayList<>();
        List<Double> intensity = new ArrayList<>();

        int phase = 0;
        for (String line : contentsByLine) {
            if (!line.isEmpty()) {
                switch (phase) {
                    case 0 -> headerLine.add(line.split("\\s*,\\s*"));
                    case 1 -> columnNames.add(line.split("\\s*,\\s*"));
                    case 2 -> {
                        String[] cols = line.split("\\s*,\\s*");
                        masses.add(Double.parseDouble(cols[0]));
                        intensity.add(Double.parseDouble(cols[1]));
                    }
                }

                if (line.startsWith("#START")) {
                    phase = 1;
                } else if (phase == 1) {
                    phase = 2;
                }
            }
        }


        this.detectorName = headerLine.get(1)[1];
        this.MassID = headerLine.get(2)[1];
        this.peakCenterMass = Double.parseDouble(headerLine.get(4)[1]);
        this.integPeriodMS = headerLine.get(10)[1].replaceFirst("ms", "");


        double[] magMasses = masses.stream().mapToDouble(d -> d).toArray();
        magnetMasses = new Matrix(magMasses, magMasses.length);
        double[] mPeakIntensity = intensity.stream().mapToDouble(d -> d).toArray();
        measPeakIntensity = new Matrix(mPeakIntensity, mPeakIntensity.length);


    }

    public void calcCollectorWidthAMU(MassSpecModel massSpec) {
        collectorWidthAMU = peakCenterMass / massSpec.getEffectiveRadiusMagnetMM() * massSpec.getCollectorWidthMM();
    }

    public void calcBeamWidthAMU(MassSpecModel massSpec) {
        theoreticalBeamWidthAMU = peakCenterMass / massSpec.getEffectiveRadiusMagnetMM() * massSpec.getTheoreticalBeamWidthMM();
    }

    public double getCollectorWidthAMU() {
        return collectorWidthAMU;
    }

    public double getPeakCenterMass() {
        return peakCenterMass;
    }

    public double getTheoreticalBeamWidthAMU() {
        return theoreticalBeamWidthAMU;
    }

    public Matrix getMagnetMasses() {
        return magnetMasses;
    }

    public void setMagnetMasses(Matrix magnetMasses) {
        this.magnetMasses = magnetMasses;
    }

    public Matrix getMeasPeakIntensity() {
        return measPeakIntensity;
    }

    public void setMeasPeakIntensity(Matrix measPeakIntensity) {
        this.measPeakIntensity = measPeakIntensity;
    }

//    public String getDetectorName() {
//        return detectorName;
//    }
//
//    public String getIntegPeriodMS() {
//        return integPeriodMS;
//    }

    public String getMassID() {
        return MassID;
    }
}
