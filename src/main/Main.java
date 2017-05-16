package main;

import algorithm.InfectionSpread;
import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import map.MapCanvas;
import reader.ConstantValues;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Owner: Ivan
 */

public class Main extends Application {

    private HBox buttonBar;
    private VBox root;
    private MapCanvas mapCanvas;
    private Popup popup = null;
    private Random random;
    private Label timer = new Label();
    private World world;
    private InfectionSpread infectionSpread;
    private Thread timerThread = startTimer(60);
    private Thread algorithmThread = AlgorithmThread();
    private volatile boolean isWorking = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

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

        scene.getStylesheets().add(ConstantValues.CSS_STYLE_FILE);

        world = new World();
        random = new Random();
        infectionSpread = new InfectionSpread(random,world,mapCanvas);
        timer.setText(world.getTime().toString());
        timer.setId("timer");
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

    private void setUpButtons(MenuButton fileMenuButton, Stage primaryStage) {

        Button start = setUpImageButton(ConstantValues.PLAY_BUTTON_IMAGE_FILE);
        Button pause = setUpImageButton(ConstantValues.PAUSE_BUTTON_IMAGE_FILE);
        Button fastForward = setUpImageButton(ConstantValues.FAST_FORWARD_BUTTON_IMAGE_FILE);
        Button fastForward2 = setUpImageButton(ConstantValues.FAST_FORWARD_BUTTON_IMAGE_FILE2);
        Button backForward = setUpImageButton(ConstantValues.BACK_FORWARD_BUTTON_IMAGE_FILE);
        Button backForward2 = setUpImageButton(ConstantValues.BACK_FORWARD_BUTTON_IMAGE_FILE2);
        // set up the buttons on the buttonBar
        Button disease = new Button("Diseases");
        Button medicine = new Button("Medicines");
        Button smaller = new Button("<");
        Button stop = new Button("Medicines");
        Button bigger = new Button("Medicines");

        setUpEventHandlers(primaryStage, disease, start, pause,fastForward,fastForward2,backForward,backForward2);

        // addComponent the file menu, separators and the object buttons to the button bar
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(pause, start);
        stackPane.setMaxHeight(0);

        StackPane stackPaneFast = new StackPane();
        stackPaneFast.getChildren().addAll(fastForward2,fastForward);
        stackPaneFast.setMaxHeight(0);

        StackPane stackPaneBack = new StackPane();
        stackPaneBack.getChildren().addAll(backForward2,backForward);
        stackPaneBack.setMaxHeight(0);

        buttonBar.getChildren().addAll(
                fileMenuButton,
                disease, medicine, smaller, stop, bigger, stackPaneBack,stackPane,stackPaneFast, timer);
        buttonBar.setSpacing(10);
        buttonBar.setPadding(new Insets(10, 10, 10, 10));
        //timer.relocate(10, buttonBar.getMaxWidth());
    }

    /**
     * Creates an image button, sets it up and sets its id properly.
     */
    private Button setUpImageButton(String buttonId) {
        ImageView imagePlay = new ImageView();
        try {
            imagePlay = new ImageView(new Image(new FileInputStream("images/" + buttonId + ".png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imagePlay.setFitHeight(25);
        imagePlay.setFitWidth(25);
        Button start = new Button();
        start.setGraphic(imagePlay);
        start.setId(buttonId);
        return start;
    }

    private void setUpEventHandlers(final Stage primaryStage, final Button disease,
                                    final Button start, final Button pause, final Button fastForwardbutton,
                                    final Button fastForwardbutton2, final Button backForwardbutton,
                                    final Button backForwardbutton2) {
        start.setOnAction(event -> {
            start.setVisible(false);
            pause.setVisible(true);
            isWorking = true;
            algorithmThread.start();
            timerThread.start();
        });

        pause.setOnAction(event -> {
            start.setVisible(true);
            pause.setVisible(false);
            isWorking = false;
        });

        disease.setOnAction(event -> {
            SetUpPopup();
            popup.show(primaryStage);
        });

        fastForwardbutton.setOnAction(event ->{
            fastForwardbutton.setVisible(false);
            fastForwardbutton2.setVisible(true);
        });

        fastForwardbutton2.setOnAction(event ->{
            fastForwardbutton.setVisible(true);
            fastForwardbutton2.setVisible(false);
        });

        backForwardbutton.setOnAction(event ->{
            backForwardbutton.setVisible(false);
            backForwardbutton2.setVisible(true);
        });

        backForwardbutton2.setOnAction(event ->{
            backForwardbutton.setVisible(false);
            backForwardbutton2.setVisible(true);
        });

        primaryStage.setOnCloseRequest(event -> {
            if (algorithmThread != null && algorithmThread.isAlive()) {
                algorithmThread.interrupt();
            }
            if (timerThread != null && timerThread.isAlive()) {
                timerThread.interrupt();
            }
            System.exit(1);
        });

        mapCanvas.getCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 1) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    selectCountryOnMap(event);
                } else if (event.getButton() == MouseButton.PRIMARY) {
                    createInfectionPointFromClick(event);
                }
            }
            event.consume();
        });
    }

    private void createInfectionPointFromClick(MouseEvent event) {
        if (world.getCountry("Bulgaria").isPresent()) {
            Country country = world.getCountry("Bulgaria").get();
            java.awt.geom.Point2D mapInfectionPoint = mapCanvas.getGeoFinder().screenToMapCoordinates(event.getX(), event.getY());
            country.addInfectionPoint(mapInfectionPoint);
        }
    }

    /**
     * Changes the rendering style of the clicked on country.
     *
     * @return The name of the country that was clicked on.
     */
    private String selectCountryOnMap(MouseEvent event) {
        mapCanvas.selectStyleChange(event.getX(), event.getY());
        mapCanvas.setNeedsRepaint();
        return mapCanvas
                .getGeoFinder()
                .getCountryNameFromScreenCoordinates(event.getX(), event.getY());
    }

    private void SetUpPopup() {
        // Set up Popups
        popup = new Popup();
        Rectangle popUpRectangleBackground = new Rectangle(500, 500);
        popUpRectangleBackground.setFill(Color.AQUAMARINE);

        //Items in popup
        final TextField name = new TextField();

        //restricting the user to type only integers for the preferred temperature
        final TextField preferredTemp = createFractionTextField();

        //restricting the user to type only integers for the temperature tolerance
        final TextField tempTolerance = createFractionTextField();


        final Slider lethality = new Slider(0, 100, 50);
        final Slider virulence = new Slider(0, 100, 50);
        final Button save = new Button("Create disease");

        lethality.setShowTickLabels(true);
        lethality.setShowTickMarks(true);
        virulence.setShowTickLabels(true);
        virulence.setShowTickMarks(true);

        final Label nameCaption = new Label("Name:");
        final Label prefTempCaption = new Label("Preferred temperature:");
        final Label tempToleranceCaption = new Label("Temperature tolerance:");
        final Label lethalityCaption = new Label("Lethality Level:");
        final Label virulenceCaption = new Label("Virulence Level:");

        nameCaption.setPrefWidth(170);
        prefTempCaption.setPrefWidth(170);
        tempToleranceCaption.setPrefWidth(170);
        lethalityCaption.setPrefWidth(170);
        virulenceCaption.setPrefWidth(170);

        final Label lethalityValue = new Label("50%");
        final Label virulenceValue = new Label("50%");

        lethality.valueProperty().addListener(
                (ov, oldValue, newValue) ->
                        lethalityValue.setText(String.format("%.0f%%", newValue)));

        virulence.valueProperty().addListener(
                (ov, oldValue, newValue) ->
                        virulenceValue.setText(String.format("%.0f%%", newValue)));

        HBox nameHB = new HBox();
        HBox prefTempHB = new HBox();
        HBox tempToleranceHB = new HBox();
        HBox lethalityHB = new HBox();
        HBox virulenceHB = new HBox();
        VBox buttonsAndFieldsVB = new VBox();

        nameHB.getChildren().addAll(nameCaption, name);
        prefTempHB.getChildren().addAll(prefTempCaption, preferredTemp);
        tempToleranceHB.getChildren().addAll(tempToleranceCaption, tempTolerance);
        lethalityHB.getChildren().addAll(lethalityCaption, lethality, lethalityValue);
        virulenceHB.getChildren().addAll(virulenceCaption, virulence, virulenceValue);
        buttonsAndFieldsVB.getChildren().addAll(nameHB, prefTempHB, tempToleranceHB, lethalityHB, virulenceHB, save);

        popup.getContent().addAll(popUpRectangleBackground, buttonsAndFieldsVB);
        buttonsAndFieldsVB.setSpacing(10);
        buttonsAndFieldsVB.setPadding(new Insets(10, 10, 10, 10));

        save.setOnAction(event -> {
            try {
                Disease disease = new Disease(name.getText(), DiseaseType.BACTERIA,
                        new DiseaseProperties((int) lethality.getValue(),
                                Double.parseDouble(preferredTemp.getText()),
                                Double.parseDouble(tempTolerance.getText()),
                                virulence.getValue() / 100));
                infectionSpread.getDiseaseList().add(disease);
                popup.hide();
            } catch (Exception ex) {
                name.setPromptText("not filled in");
                preferredTemp.setPromptText("not filled in");
                tempTolerance.setPromptText("not filled in");
            }
        });
    }

    /**
     * Creates a text field which only takes input in the format: digits followed by a single comma or dot
     * followed by more digits.
     */
    private TextField createFractionTextField() {
        return new TextField() {
            @Override
            public void replaceText(int i, int j, String string) {
                if (string.isEmpty() || (this.getText() + string).matches("\\d+([.,])?(\\d+)?")) {
                    super.replaceText(i, j, string);
                }
            }
        };
    }


    private Thread startTimer(double speed) {
        return new Thread(() -> {
            while (isWorking) {
                world.getTime().setElapsedTime(speed);
                Platform.runLater(() -> timer.setText(world.getTime().toString()));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }
            }
        });

    }
    private Thread AlgorithmThread(){
        return new Thread(() -> {
                    while (isWorking) {
                        infectionSpread.applyAlgorithm();
                        mapCanvas.updateInfectionPointsCoordinates(world.getAllInfectionPoints());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                });
    }

}

