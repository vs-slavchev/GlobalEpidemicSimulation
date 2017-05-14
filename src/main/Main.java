package main;

import algorithm.InfectionSpread;
import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javafx.application.Application;
import javafx.fxml.FXML;
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
import javax.swing.JTextField;
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

    private Thread algorithmThread;
    private World world;
    private InfectionSpread infectionSpread;
    private volatile boolean isWorking = true;
    private List<String> points = new ArrayList<>();
    public static final double INFECTION_RADIUS = 2.0;

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
        infectionSpread = new InfectionSpread();
        world.readCountryInfo();
        world.readTemps();
        for (Country c:world.getCountries()
             ) {

            System.out.print(c.toString());
            System.out.println("");
        }
    }

    private void applyAlgorithm() {

        for (java.awt.geom.Point2D infectionPoint : world.getAllInfectionPoints()) {
            if (random.nextDouble() < infectionSpread
                    .getMainDisease()
                    .getProperties()
                    .getVirulence()) {



                    double offsetX = random.nextDouble() * INFECTION_RADIUS + INFECTION_RADIUS;
                    double offsetY = random.nextDouble() * INFECTION_RADIUS + INFECTION_RADIUS;
                    double newPointX = infectionPoint.getX() +
                            (random.nextBoolean() ? + offsetX : - offsetX);
                    double newPointY = infectionPoint.getY() +
                            (random.nextBoolean() ? offsetY : - offsetY);
                    String conc = "" + newPointX +newPointY;
                    while (points.contains(conc)){
                        newPointX =+
                                (random.nextBoolean() ? + offsetX/5: - offsetX/5);
                        newPointY =+
                                (random.nextBoolean() ? offsetY/5 : - offsetY/5);
                    }


                        java.awt.geom.Point2D screenNewPoint = mapCanvas.getGeoFinder()
                                .mapToScreenCoordinates(newPointX, newPointY);

                        String countryName = mapCanvas.getGeoFinder().getCountryNameFromScreenCoordinates(
                                screenNewPoint.getX(), screenNewPoint.getY());
                        if (countryName.equals("water")) {
                            continue;
                        }

                        if (world.getCountry("Bulgaria").isPresent()) {
                            Country country = world.getCountry("Bulgaria").get();
                            country.addInfectionPoint(
                                    new java.awt.geom.Point2D.Double(newPointX, newPointY));
                        }
                        points.add(conc);


                }





            }
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

    private void setUpButtons(MenuButton fileMenuButton, Stage primaryStage) {

        // TODO: refactor the copy pasta
        FileInputStream playInput = null;
        try {
            playInput = new FileInputStream(ConstantValues.PLAY_BUTTON_IMAGE_FILE);
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
            pauseInput = new FileInputStream(ConstantValues.PAUSE_BUTTON_IMAGE_FILE);
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






        // set up the buttons on the buttonBar
        Button disease = new Button("Diseases");
        Button medicine = new Button("Medicines");
        Button smaller = new Button("<");
        Button stop = new Button("Medicines");
        Button bigger = new Button("Medicines");

        setUpEventHandlers(primaryStage, disease, start, pause);

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

    private void setUpEventHandlers(final Stage primaryStage, final Button disease,
                                    final Button start, final Button pause) {
        start.setOnAction(event -> {
            start.setVisible(false);
            pause.setVisible(true);
            isWorking = true;
            algorithmThread = new Thread(() -> {
                while (isWorking) {
                    applyAlgorithm();
                    mapCanvas.updateInfectionPointsCoordinates(world.getAllInfectionPoints());

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        System.exit(0);
                    }
                }
            });
            algorithmThread.start();
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

        primaryStage.setOnCloseRequest(event -> {
            if (algorithmThread.isAlive()) {
                algorithmThread.interrupt();
            }
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

    private void selectCountryOnMap(MouseEvent event) {
        String clickedOnCountryName = mapCanvas
                .getGeoFinder()
                .getCountryNameFromScreenCoordinates(event.getX(), event.getY());

        mapCanvas.selectStyleChange(event.getX(), event.getY());
        mapCanvas.setNeedsRepaint();
    }

    private void SetUpPopup() {
        // Set up Popups
        popup = new Popup();
        Rectangle rec = new Rectangle(500, 500);
        rec.setFill(Color.AQUAMARINE);

        //Items in popup
        final TextField name = new TextField();

        //restricting the user to type only integers for the preferred temperature
        final TextField prefTemp = new TextField(){

            @Override
            public  void  replaceText(int i, int j, String string){
                if(string.isEmpty() || (this.getText() + string).matches("\\d+([.,])?(\\d+)?")){
                    super.replaceText(i, j, string);
                }
            }
            public void paste(){}
        };

        //restricting the user to type only integers for the temperature tolerance
        final TextField tempTolerance = new TextField(){

            @Override
            public  void  replaceText(int i, int j, String string){
                if(string.isEmpty() || (this.getText() + string).matches("\\d+([.,])?(\\d+)?")){
                    super.replaceText(i, j, string);
                }
            }
            public void paste(){}
        };


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
        VBox test = new VBox();

        nameHB.getChildren().addAll(nameCaption, name);
        prefTempHB.getChildren().addAll(prefTempCaption, prefTemp);
        tempToleranceHB.getChildren().addAll(tempToleranceCaption, tempTolerance);
        lethalityHB.getChildren().addAll(lethalityCaption, lethality, lethalityValue);
        virulenceHB.getChildren().addAll(virulenceCaption, virulence, virulenceValue);
        test.getChildren().addAll(nameHB, prefTempHB, tempToleranceHB, lethalityHB, virulenceHB, save);

        // TODO: auto-rename key is Shift + F6
        popup.getContent().addAll(rec, test);
        test.setSpacing(10);
        test.setPadding(new Insets(10, 10, 10, 10));

        // TODO: validate input
        save.setOnAction(event -> {
            Disease disease = new Disease(name.getText(), DiseaseType.BACTERIA,
                    new DiseaseProperties((int) lethality.getValue(),
                            Integer.parseInt(prefTemp.getText()),
                            Integer.parseInt(tempTolerance.getText()),
                            virulence.getValue() / 100));
            infectionSpread.getDiseaseList().add(disease);
            popup.hide();
        });
    }

}

