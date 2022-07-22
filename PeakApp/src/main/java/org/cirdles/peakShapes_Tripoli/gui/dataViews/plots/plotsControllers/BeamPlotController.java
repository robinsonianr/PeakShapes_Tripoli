package org.cirdles.peakShapes_Tripoli.gui.dataViews.plots.plotsControllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.peakShapes_Tripoli.PeakShapes_Tripoli;
import org.cirdles.peakShapes_Tripoli.beamShape.BeamDataOutput;
import org.cirdles.peakShapes_Tripoli.gui.dataViews.plots.AbstractDataView;
import org.cirdles.peakShapes_Tripoli.gui.dataViews.plots.BeamLinePlot;
import org.cirdles.peakShapes_Tripoli.visualizationUtilities.LinePlot;

import java.io.IOException;
import java.nio.file.Path;

public class BeamPlotController {

    @FXML
    private AnchorPane plotAnchorPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vBoxControl;

    @FXML
    private ToolBar toolBar;

    @FXML
    void graphBtn(ActionEvent event) throws IOException {
        loadBeamPlot("beamShape");
        ((Button) event.getSource()).setDisable(true);

    }


    @FXML
    void initialize() {

        vBoxControl.setPrefSize(500.0, 500);
        toolBar.setPrefSize(500, 20.0);
        scrollPane.setPrefSize(500.0, 500 - toolBar.getHeight());
        scrollPane.setPrefViewportWidth(485.0);
        scrollPane.setPrefViewportHeight(485);

        vBoxControl.prefWidthProperty().bind(plotAnchorPane.widthProperty());
        vBoxControl.prefHeightProperty().bind(plotAnchorPane.heightProperty());

        scrollPane.prefWidthProperty().bind(vBoxControl.widthProperty());
        scrollPane.prefHeightProperty().bind(vBoxControl.heightProperty().subtract(toolBar.getHeight()));

    }

    public void loadBeamPlot(String option) throws IOException {
        org.cirdles.commons.util.ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(PeakShapes_Tripoli.class);
        Path dataFile = RESOURCE_EXTRACTOR.extractResourceAsFile("/org/cirdles/peakShapes_Tripoli/dataProcessors/DVCC18-9 z9 Pb-570-PKC-205Pb-PM-S2B7C1.TXT").toPath();
        LinePlot linePlot = BeamDataOutput.modelTest(dataFile, option);
        int leftBoundary = (int) BeamDataOutput.getLeftBoundary();
        int rightBoundary = (int) BeamDataOutput.getRightBoundary();

        AbstractDataView histogramPlot = new BeamLinePlot(leftBoundary, rightBoundary, new Rectangle(scrollPane.getWidth(), scrollPane.getHeight()), linePlot);

        scrollPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > 100) {
                    histogramPlot.setMyWidth(newValue.intValue() - 15);
                    histogramPlot.repaint();
                }
            }
        });

        scrollPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > 100) {
                    histogramPlot.setMyHeight(newValue.intValue() - 15);
                    histogramPlot.repaint();
                }
            }
        });

        histogramPlot.preparePanel();
        scrollPane.setContent(histogramPlot);

    }


}
