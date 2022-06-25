package org.cirdles.peakShapes_Tripoli.beamShape.peakMeasProperties.dataModel;

public class DataModel {
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
