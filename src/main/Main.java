package main;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();

        MapCanvas canvas = new MapCanvas(width, height);

        Pane pane = new Pane(canvas.getCanvas());
        Scene scene = new Scene(pane);
        primaryStage.setMaximized(true);
        primaryStage.setX(width);
        primaryStage.setY(0);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Global Epidemic Simulation");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
