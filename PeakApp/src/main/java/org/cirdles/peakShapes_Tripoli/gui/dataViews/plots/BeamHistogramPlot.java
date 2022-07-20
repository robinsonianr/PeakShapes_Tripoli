package org.cirdles.peakShapes_Tripoli.gui.dataViews.plots;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.cirdles.peakShapes_Tripoli.visualizationUtilities.Histogram;


/**
 * @author James F. Bowring
 */
public class BeamHistogramPlot extends AbstractDataView {

    private Histogram histogram;
    int leftBoundary;
    int rightBoundary;

    /**
     * @param bounds
     */
    public BeamHistogramPlot(int leftBoundary, int rightBoundary, Rectangle bounds, Histogram histogram) {
        super(bounds, 100, 100);
        this.histogram = histogram;
        this.leftBoundary = leftBoundary;
        this.rightBoundary = rightBoundary;
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
        g2d.fillText("Line Graph of Beam Shape", 20, 20);

        // plot bins
        g2d.setLineWidth(2.0);
        // new line graph
        g2d.setStroke(Paint.valueOf("Black"));
        g2d.beginPath();
        g2d.moveTo(mapX(xAxisData[0]), mapY(yAxisData[0]));
        for (int i = 0; i < xAxisData.length; i++) {
            // line tracing through points

            g2d.lineTo(mapX(xAxisData[i]), mapY(yAxisData[i]));
        }

        g2d.stroke();
        g2d.beginPath();
        g2d.setLineDashes(5);
        g2d.setStroke(Paint.valueOf("Blue"));
        for (int i = leftBoundary; i <= rightBoundary; i++) {
            // line tracing through points

            g2d.lineTo(mapX(xAxisData[i]), mapY(0.0));
        }
        g2d.stroke();

        g2d.setFill(Paint.valueOf("Red"));
        g2d.fillOval(mapX(xAxisData[leftBoundary]), mapY(yAxisData[leftBoundary]), 6, 6);
        g2d.fillOval(mapX(xAxisData[rightBoundary]), mapY(yAxisData[rightBoundary]), 6, 6);

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
