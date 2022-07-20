package org.cirdles.peakShapes_Tripoli.gui.dataViews.plots;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class BeamShapePlotWindow {
    private final double xOffset = 0;
    private final double yOffset = 0;
    public Stage plottingStage;
    public Window plottingWindow;
    private Stage primaryStage;

    private BeamShapePlotWindow() {
    }

    public BeamShapePlotWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
        plottingStage = new Stage();
        plottingStage.setMinHeight(500);
        plottingStage.setMinWidth(500);
        plottingStage.setTitle("Beam Shape Demo Window");

        plottingStage.setOnCloseRequest((WindowEvent e) -> {
            plottingStage.hide();
            plottingStage.setScene(null);
            e.consume();
        });
    }

    public void loadBeamPlotWindow() {
        if (!plottingStage.isShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cirdles/peakShapes_Tripoli/gui/dataViews.plots/plotsController/BeamPlot.fxml"));
                Scene scene = new Scene(loader.load());
                plottingStage.setScene(scene);

            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
            plottingWindow = plottingStage.getScene().getWindow();
            plottingStage.show();
        }

        // center on app window
        plottingStage.setX(primaryStage.getX() - (primaryStage.getWidth() - plottingStage.getWidth()) * 2);
        plottingStage.setY(primaryStage.getY() + (primaryStage.getHeight() - plottingStage.getHeight()) / 2);
        plottingStage.requestFocus();
    }
}
