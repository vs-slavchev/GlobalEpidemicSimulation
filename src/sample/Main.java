package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Main extends Application {

    private HBox buttonBar;
    private VBox root;

    @Override
    public void start(Stage primaryStage) throws Exception{

        root = new VBox();
        root.setMinWidth(640);
        root.setMinHeight(480);

        setUpButtonBar(primaryStage);
        root.getChildren().addAll(buttonBar);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Pipe Flow Tool");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

    private void setUpButtonBar(Stage primaryStage) {
        buttonBar = new HBox();

        MenuButton fileMenuButton = new MenuButton("File");
        fileMenuButton.setMinHeight(42);
        fileMenuButton.setPrefHeight(42);
        fileMenuButton.setMaxHeight(42);

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
        Button start = new Button("Medicines");
        Button pause = new Button("Medicines");

        // Set up Popups
        final Popup popup = new Popup();
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

        // set up functionality
        disease.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                popup.show(primaryStage);
            }
        });

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                popup.hide();
            }
        });

        // addComponent the file menu, separators and the object buttons to the button bar
        buttonBar.getChildren().addAll(
                fileMenuButton,
                disease, medicine, smaller, stop, bigger, start, pause);
        buttonBar.setSpacing(10);
        buttonBar.setPadding(new Insets(10, 10, 10, 10));

    }

}

