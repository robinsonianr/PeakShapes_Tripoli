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
public class HistogramPlot extends AbstractDataView {

    private Histogram histogram;
    private double[] xDataAxis;

    /**
     * @param bounds
     */
    public HistogramPlot(Rectangle bounds, Histogram histogram) {
        super(bounds, 100, 200);
        this.histogram = histogram;
    }

    @Override
    public void preparePanel() {

        xAxisData = histogram.getBinCenters();
        xDataAxis = new double[xAxisData.length];

        for (int i = 0; i < xAxisData.length; i++) {
            xDataAxis[i] = i;
        }
        minX = xDataAxis[0];
        maxX = xDataAxis[xDataAxis.length - 1];

        yAxisData = histogram.getData();
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        for (int i = 0; i < yAxisData.length; i++) {
            minY = StrictMath.min(minY, yAxisData[i]);
            maxY = StrictMath.max(maxY, yAxisData[i]);
        }

        minY = -5.0;
        maxY += 5.0;

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        this.repaint();
    }

    @Override
    public void paint(GraphicsContext g2d) {
        super.paint(g2d);

        g2d.setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, 15));
        g2d.setFill(Paint.valueOf("RED"));
        g2d.fillText("Line Graph", 20, 20);

        // plot bins
        g2d.setLineWidth(2.0);
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
        g2d.setStroke(Paint.valueOf("Black"));
        g2d.beginPath();
        g2d.moveTo(mapX(xDataAxis[0]), mapY(yAxisData[0]));
        for (int i = 0; i < xDataAxis.length; i++) {
            // line tracing through points
            g2d.lineTo(mapX(xDataAxis[i]), mapY(yAxisData[i]));
        }
        g2d.stroke();
    }
}
