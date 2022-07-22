package org.cirdles.peakShapes_Tripoli.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.cirdles.peakShapes_Tripoli.gui.dataViews.plots.BeamShapePlotWindow;
import org.cirdles.peakShapes_Tripoli.gui.dataViews.plots.GBeamPlotWindow;


public class PeakShapeGUIController {


    @FXML
    void beamDemo(ActionEvent event) {
        BeamShapePlotWindow beamShapePlotWindow = new BeamShapePlotWindow(PeakShapesGUI.primaryStage);
        beamShapePlotWindow.loadBeamPlotWindow();

        GBeamPlotWindow gBeamPlotWindow = new GBeamPlotWindow(PeakShapesGUI.primaryStage);
        gBeamPlotWindow.loadBeamPlotWindow();
    }

}
