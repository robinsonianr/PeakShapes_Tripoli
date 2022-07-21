package org.cirdles.peakShapes_Tripoli.gui.dataViews.plots;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.cirdles.peakShapes_Tripoli.visualizationUtilities.LinePlot;


public class GBeamLinePlot extends AbstractDataView {
    private LinePlot linePlot;
    private double[] xMass;
    double[] yIntensity;

    /**
     * @param bounds
     */
    public GBeamLinePlot(double[] massData, double[] intensityData, Rectangle bounds, LinePlot linePlot) {
        super(bounds, 100, 100);
        this.linePlot = linePlot;
        this.xMass = massData;
        this.yIntensity = intensityData;
    }

    @Override
    public void preparePanel() {
        xAxisData = linePlot.getxData();

        minX = xAxisData[0];
        maxX = xAxisData[xAxisData.length - 1];

        ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphWidth / 25.0));
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        yAxisData = linePlot.getyData();
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        for (int i = 0; i < yIntensity.length; i++) {
            minY = StrictMath.min(minY, yIntensity[i]);
            maxY = StrictMath.max(maxY, yIntensity[i]);
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

        Text text = new Text();
        g2d.setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, 15));
        int textWidth = 0;

        g2d.setFill(Paint.valueOf("RED"));
        g2d.fillText("Line Plot of G-Beam", 20, 20);

        // plot bins

        g2d.setLineWidth(2.5);


        g2d.beginPath();
        g2d.setStroke(Paint.valueOf("Blue"));
        g2d.setLineDashes(0);
        // x = magnetMass y = intensity

        for (int i = 0; i < xMass.length; i++) {
            g2d.lineTo(mapX(xMass[i]), mapY(yIntensity[i]));
        }

        g2d.stroke();
        g2d.beginPath();
        g2d.setLineWidth(2.5);
        g2d.setLineDashes(4);
        g2d.setStroke(Paint.valueOf("Red"));

        // x = magnetMass y = G-Beam
        g2d.moveTo(mapX(xAxisData[0]), mapY(yAxisData[0]));
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
        if (ticsY.length > 1) {
            // border and fill
            g2d.setLineWidth(0.5);
            g2d.setStroke(Paint.valueOf("BLACK"));
            g2d.strokeRect(
                    mapX(minX),
                    mapY(ticsY[ticsY.length - 1].doubleValue()),
                    graphWidth,
                    StrictMath.abs(mapY(ticsY[ticsY.length - 1].doubleValue()) - mapY(ticsY[0].doubleValue())));

            g2d.setFill(Paint.valueOf("BLACK"));

            // ticsY
            float verticalTextShift = 3.2f;
            g2d.setFont(Font.font("SansSerif", 10));
            if (ticsY != null) {
                for (int i = 0; i < ticsY.length; i++) {
                    g2d.strokeLine(
                            mapX(minX), mapY(ticsY[i].doubleValue()), mapX(maxX), mapY(ticsY[i].doubleValue()));

                    // left side
                    text.setText(ticsY[i].toString());
                    textWidth = (int) text.getLayoutBounds().getWidth();
                    g2d.fillText(text.getText(),//
                            (float) mapX(minX) - textWidth + 5f,
                            (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);

                }
                // ticsX
                if (ticsX != null) {
                    for (int i = 0; i < ticsX.length - 1; i++) {
                        try {
                            g2d.strokeLine(
                                    mapX(ticsX[i].doubleValue()),
                                    mapY(ticsY[0].doubleValue()),
                                    mapX(ticsX[i].doubleValue()),
                                    mapY(ticsY[0].doubleValue()) + 5);

                            // bottom
                            String xText = ticsX[i].toPlainString();
                            g2d.fillText(xText,
                                    (float) mapX(ticsX[i].doubleValue()) - 5f,
                                    (float) mapY(ticsY[0].doubleValue()) + 15);

                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }
}