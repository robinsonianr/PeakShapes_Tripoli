package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel;

import java.nio.file.Path;

public class DataModel {

    private double[][] magnetMasses;        // vector of masses for intensity measurements
    private double[][] measPeakIntensity;   // vector of corresponding peak intensities
    private double peakCenterMass;          // mass at center of peak from header
    private double integPeriodMS ;          // integration period of measurements in ms
    private String MassID;                  // name of peak getting centered e.g. "205Pb"
    private String detectorName;            // name of detector as string e.g. "L2"
    private double collectorWidthAMU;       // width of collector aperture in AMU at center mass
    private double theoreticalBeamWidthAMU; // width of beam in AMU at center mass


    public DataModel(Path inputFile){
        /*
    function data = dataModel(filename)
            %DATAMODEL Construct an instance of this class
            %   Detailed explanation goes here

            arguments
                filename (1,1) string
            end

            % 1. parse numerical data
            dataTable = parseDataFromTextFile(filename);
            data.magnetMasses = dataTable.Mass;
            data.measPeakIntensity = dataTable.Intensity;

            % 2. parse header with metadata
            fileAsStrings = readlines(filename, 'EmptyLineRule', 'skip');
            header = fileAsStrings(1:11); % just the header
            data.peakCenterMass = str2double(extractAfter(header(5), ","));
            data.integPeriodMS = str2double(extractAfter(header(11), "ms"));
            data.MassID = strtrim(extractAfter(header(3), ","));
            data.detectorName = strtrim(extractAfter(header(2), ","));

     */
    }

}
