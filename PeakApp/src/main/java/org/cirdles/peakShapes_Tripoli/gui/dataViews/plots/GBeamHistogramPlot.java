package org.cirdles.peakShapes_Tripoli.gui.dataViews.plots;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.cirdles.peakShapes_Tripoli.visualizationUtilities.Histogram;


public class GBeamHistogramPlot extends AbstractDataView {
    private Histogram histogram;
    private double[] xMass;
    double[] yIntensity;

    /**
     * @param bounds
     */
    public GBeamHistogramPlot(double[] massData, double[] intensityData, Rectangle bounds, Histogram histogram) {
        super(bounds, 100, 100);
        this.histogram = histogram;
        this.xMass = massData;
        this.yIntensity = intensityData;
    }

    @Override
    public void preparePanel() {
        xAxisData = histogram.getxData();

        minX = xAxisData[0];
        maxX = xAxisData[xAxisData.length - 1];
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        yAxisData = histogram.getyData();
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        for (int i = 0; i < yAxisData.length; i++) {
            minY = StrictMath.min(minY, yAxisData[i]);
            maxY = StrictMath.max(maxY, yAxisData[i]);
        }

        ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 20.0));
        if ((ticsY != null) && (ticsY.length > 1)) {
            // force y to tics
            minY = ticsY[0].doubleValue();
            maxY = ticsY[ticsY.length - 1].doubleValue();
            // adjust margins
            double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
            minY -= yMarginStretch;
            maxY += yMarginStretch;
        }

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        this.repaint();
    }

    @Override
    public void paint(GraphicsContext g2d) {
        super.paint(g2d);

        g2d.setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, 15));
        g2d.setFill(Paint.valueOf("RED"));
        g2d.fillText("Line Graph of G-Beam", 20, 20);

        // plot bins

        g2d.setLineWidth(2.5);


        g2d.beginPath();
        g2d.setStroke(Paint.valueOf("Blue"));
        g2d.setLineDashes(0);
        // x = magnetMass y = intensity
        g2d.moveTo(mapX(xMass[0]), mapY(yIntensity[0]));
        for (int i = 0; i < xMass.length; i++) {
            g2d.lineTo(mapX(xMass[i]), mapY(yIntensity[i]));
        }

        g2d.stroke();
        g2d.beginPath();
        g2d.setLineWidth(2.5);
        g2d.setLineDashes(4);
        g2d.setStroke(Paint.valueOf("Red"));
        // x = magnetMass y = G-Beam
        for (int i = 0; i < xAxisData.length; i++) {
            // line tracing through points
            g2d.lineTo(mapX(xAxisData[i]), mapY(yAxisData[i]));
        }
        g2d.stroke();
        g2d.beginPath();
        g2d.setLineDashes(0);


//        for (int i = 0; i < xDataAxis.length; i++) {
//            System.err.println(mapX(xDataAxis[i] - histogram.getBinWidth() / 2.0) + "    " + mapY(yAxisData[i]) + "   " + mapX(xAxisData[i] + histogram.getBinWidth()) + "   " + mapY(yAxisData[i]));
//            g2d.fillRect(
//                    mapX(xDataAxis[i]),
//                    mapY(yAxisData[i]),
//                    mapX(xDataAxis[1]) - mapX(xDataAxis[0]),
//                    mapY(0.0) - mapY(yAxisData[i]));
//        }

//        g2d.setStroke(Paint.valueOf("BLACK"));
//        for (int i = 0; i < xDataAxis.length; i++) {
//            g2d.strokeLine(mapX(xDataAxis[i]), mapY(0.0), mapX(xDataAxis[i]), mapY(yAxisData[i]));
//        }

        // plot line for giggles

        g2d.stroke();
    }
}
