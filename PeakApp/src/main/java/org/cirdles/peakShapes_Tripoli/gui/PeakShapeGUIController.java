package org.cirdles.peakShapes_Tripoli.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.cirdles.peakShapes_Tripoli.gui.dataViews.plots.BeamPlotWindow;



public class PeakShapeGUIController {

    @FXML
    private Button histogramBtn;




    @FXML
    void beamDemo(ActionEvent event){
        BeamPlotWindow beamPlotWindow = new BeamPlotWindow(PeakShapesGUI.primaryStage);
        beamPlotWindow.loadBeamPlotWindow();
    }


}
