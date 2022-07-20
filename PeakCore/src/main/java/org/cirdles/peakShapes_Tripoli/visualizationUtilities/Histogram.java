/*
 * Copyright 2022 James Bowring, Noah McLean, Scott Burdick, and CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cirdles.peakShapes_Tripoli.visualizationUtilities;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author James F. Bowring
 */
public class Histogram {

    private double[] xData;
    private double[] yData;
    private int binCount;
    private double[] binCounts;
    private double binWidth;
    private double[] binCenters;

    private Histogram(double[] xData, double[] yData, int binCount) {
        this.xData = xData;
        this.yData = yData;
        this.binCount = binCount;
    }

    public static Histogram initializeHistogram(double[] xData, double[] yData, int binCount) {
        Histogram histogram = new Histogram(xData, yData, binCount);
        histogram.generateHistogram();
        return histogram;
    }

    private void generateHistogram() {
        DescriptiveStatistics descriptiveStatisticsRatios = new DescriptiveStatistics();
        for (int index = 0; index < yData.length; index++) {
            descriptiveStatisticsRatios.addValue(yData[index]);
        }
        double dataMax = descriptiveStatisticsRatios.getMax();
        double dataMin = descriptiveStatisticsRatios.getMin();

        binCounts = new double[binCount];
        binWidth = (dataMax - dataMin) / (double) binCount;

        int maxBinCount = 0;
        for (int index = 0; index < yData.length; index++) {
            double datum = yData[index];
            if (datum > 0.0) { //ignore 0s here
                int binNum = (int) Math.floor(Math.abs((datum - dataMin * 1.000000001) / binWidth));
                try {
                    binCounts[binNum]++;
                    if (binCounts[binNum] > maxBinCount)
                        maxBinCount++;
                } catch (Exception eHist) {
                    System.err.println(eHist.getMessage());
                }
            }
        }

        binCenters = new double[binCount];
        for (int binIndex = 0; binIndex < binCount; binIndex++) {
            binCenters[binIndex] = dataMin + (binIndex + 0.5) * binWidth;
        }
    }

    public double[] getBinCounts() {
        return binCounts;
    }

    public double getBinWidth() {
        return binWidth;
    }

    public double[] getBinCenters() {
        return binCenters;
    }

    public double[] getyData() {
        return yData;
    }

    public double[] getxData() {
        return xData;
    }
}