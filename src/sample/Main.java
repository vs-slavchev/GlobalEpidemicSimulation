package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import main.MapCanvas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main extends Application {

    private HBox buttonBar;
    private VBox root;
    private MapCanvas mapCanvas;
    private Popup popup = null;

    @Override
    public void start(Stage primaryStage) throws Exception{

        root = new VBox();
        root.setMinWidth(640);
        root.setMinHeight(480);


        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();
        mapCanvas = new MapCanvas(width, height);

        setUpButtonBar(primaryStage);
        root.getChildren().addAll(buttonBar, mapCanvas.getCanvas());

        Scene scene = new Scene(root);
        primaryStage.setTitle("Global Epidemic Simulation");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.getStylesheets().add("Style.css");
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void setUpButtonBar(Stage primaryStage) {
        buttonBar = new HBox();

        MenuButton fileMenuButton = new MenuButton("File");

        //addComponent items to file menu button
        MenuItem newItem = new MenuItem("New simulation");
        MenuItem openItem = new MenuItem("Open simulation");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem saveItem = new MenuItem("Save");
        MenuItem saveAsItem = new MenuItem("Save As...");
        fileMenuButton.getItems().addAll(newItem, openItem, separator, saveItem, saveAsItem);

        // assign action handlers to the items in the file menu


        setUpButtons(fileMenuButton, primaryStage);
    }

    private void setUpButtons(MenuButton fileMenuButton, Stage primaryStage ){

        // set up the buttons on the buttonBar
        Button disease = new Button("Diseases");
        Button medicine = new Button("Medicines");
        Button smaller = new Button("<");
        Button stop = new Button("Medicines");
        Button bigger = new Button("Medicines");

        FileInputStream playInput = null;
        try {
            playInput = new FileInputStream("images/play.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Image playImage = new Image(playInput);
        ImageView imagePlay = new ImageView(playImage);
        imagePlay.setFitHeight(25);
        imagePlay.setFitWidth(25);
        Button start = new Button();
        start.setGraphic(imagePlay);
        start.setId("start-button");

        FileInputStream pauseInput = null;
        try {
            pauseInput = new FileInputStream("images/pause.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Image image = new Image(pauseInput);
        ImageView imagePause = new ImageView(image);
        imagePause.setFitHeight(25);
        imagePause.setFitWidth(25);
        Button pause = new Button();
        pause.setGraphic(imagePause);
        pause.setId("pause-button");

        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                start.setVisible(false);
                pause.setVisible(true);
            }
        });

        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                start.setVisible(true);
                pause.setVisible(false);
            }
        });


        // set up functionality
        disease.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                SetUpPopup();
                popup.show(primaryStage);
            }
        });

        // addComponent the file menu, separators and the object buttons to the button bar
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(pause, start);
        stackPane.setMaxHeight(0);

        buttonBar.getChildren().addAll(
            fileMenuButton,
            disease, medicine, smaller, stop, bigger, stackPane);
        buttonBar.setSpacing(10);
        buttonBar.setPadding(new Insets(10, 10, 10, 10));


    }

    private void SetUpPopup(){
        // Set up Popups
        popup = new Popup();
        Rectangle rec = new Rectangle(500, 500);
        rec.setFill(Color.AQUAMARINE);

        //Items in popup
        final Button save = new Button("YaskataaaaFX");
        final Slider lethality  = new Slider(0, 100, 50);
        final Slider virulence = new Slider(0, 100, 50);

        lethality.setShowTickLabels(true);
        lethality.setShowTickMarks(true);
        virulence.setShowTickLabels(true);
        virulence.setShowTickMarks(true);

        final Label lethalityCaption = new Label("Lethality Level:");
        final Label virulenceCaption = new Label("Virulence Level:");

        lethalityCaption.setPrefWidth(85);
        virulenceCaption.setPrefWidth(85);

        final Label lethalityValue = new Label("50");
        final Label virulenceValue = new Label("50");

        lethality.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                lethalityValue.setText(String.format("%.0f", new_val));
            }
        });

        virulence.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                virulenceValue.setText(String.format("%.0f", new_val));
            }
        });

        HBox first = new HBox();
        HBox second = new HBox();
        VBox test = new VBox();

        first.getChildren().addAll(lethalityCaption, lethality, lethalityValue);
        second.getChildren().addAll(virulenceCaption, virulence, virulenceValue);
        test.getChildren().addAll(first, second, save);
        popup.getContent().addAll(rec, test);
        test.setSpacing(10);
        test.setPadding(new Insets(10, 10, 10, 10));
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                popup.hide();
            }
        });
    }

}

