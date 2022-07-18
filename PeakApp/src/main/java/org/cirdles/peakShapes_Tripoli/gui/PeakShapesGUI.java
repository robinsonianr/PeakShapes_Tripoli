package org.cirdles.peakShapes_Tripoli.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class PeakShapesGUI extends Application {

    protected static Stage primaryStage;
    public static Window primaryStageWindow;


    public static void main(String[] args) {


        StringBuilder logo = new StringBuilder();
        logo.append("        _________          _                  __    _   \n");
        logo.append("       |  _   _  |        (_)                [  |  (_)  \n");
        logo.append("       |_/ | | \\_|_ .--.  __  _ .--.    .--.  | |  __   \n");
        logo.append("           | |   [ `/'`\\][  |[ '/'`\\ \\/ .'`\\ \\| | [  |  \n");
        logo.append("          _| |_   | |     | | | \\__/ || \\__. || |  | |  \n");
        logo.append("         |_____| [___]   [___]| ;.__/  '.__.'[___][___] \n");
        logo.append("                             [__|                       \n");
        System.out.println(logo);

        launch();
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        PeakShapesGUI.primaryStage = primaryStage;
        Parent root = new AnchorPane();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PeakShape");

        primaryStageWindow = primaryStage.getScene().getWindow();

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });

        FXMLLoader loader = new FXMLLoader(PeakShapesGUI.class.getResource("PeakShapeGUI.fxml"));
        scene.setRoot(loader.load());
        scene.setUserData(loader.getController());
        primaryStage.show();
        primaryStage.setMinHeight(scene.getHeight() + 15);
        primaryStage.setMinWidth(scene.getWidth());

    }
}
