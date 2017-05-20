package main;

import algorithm.InfectionSpread;
import algorithm.MedicineSpread;
import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import disease.SymptomType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.ImageCursor;
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
import java.util.Optional;
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
    private Label speedLabel = new Label();
    private World world;
    private MenuButton MedicineListBox;
    private MenuButton DiseaseListBox;
    private Disease selectedDisease;

    private MedicineSpread medicineSpread;
    private InfectionSpread infectionSpread;
    private volatile boolean isWorking = true;
    private volatile boolean isStarted = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new VBox();
        root.setMinWidth(640);
        root.setMinHeight(480);
        isStarted = false;

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();
        mapCanvas = new MapCanvas(width, height);

        world = new World();
        random = new Random();
        infectionSpread = new InfectionSpread(random, world, mapCanvas);
        medicineSpread = new MedicineSpread();

        timer.setText(world.getTime().toString());
        timer.setId("timer");
        speedLabel.setText("x" + world.getTime().getRunSpeed());

        setUpButtonBar(primaryStage);
        root.getChildren().addAll(buttonBar, mapCanvas.getCanvas());

        Scene scene = new Scene(root);
        primaryStage.setTitle("Global Epidemic Simulation");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.getStylesheets().add(ConstantValues.CSS_STYLE_FILE);

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


        MedicineListBox = new MenuButton("Medicines");
        DiseaseListBox = new MenuButton("Diseases");
        addtoListBoxes(DiseaseListBox, MedicineListBox);

        saveItem.setOnAction(event -> {
            infectionSpread.saveInfectionSpread(primaryStage, world.getTime());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Saved!");
            alert.setHeaderText(null);
            alert.showAndWait();
        });
        openItem.setOnAction(event -> {
            infectionSpread.openInfectionSpread(primaryStage, world.getTime());
            timer.setText(world.getTime().toString());
        });
        saveAsItem.setOnAction(event -> {
            infectionSpread.saveAsInfectionSpread(primaryStage, world.getTime());
        });
        newItem.setOnAction(event -> {
            if (isStarted) {
                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Simulation is already started, do you wish to exit? If yes all progress will be lost.");
                a.setHeaderText(null);
                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("No");
                ButtonType buttonTypeSave = new ButtonType("Save and exit");
                a.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeSave);
                Optional<ButtonType> result = a.showAndWait();
                if (result.get() == buttonTypeYes) {
                    try {
                        try {

                            if (AlgorithmThread() != null && AlgorithmThread().isAlive()) {
                                AlgorithmThread().interrupt();
                            }
                            if (startTimer() != null && startTimer().isAlive()) {
                                startTimer().interrupt();
                            }

                            start(primaryStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (result.get() == buttonTypeSave) {
                    infectionSpread.saveInfectionSpread(primaryStage, world.getTime());
                    try {
                        start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        // assign action handlers to the items in the file menu
        setUpButtons(fileMenuButton, DiseaseListBox, MedicineListBox, primaryStage);
    }

    private void setUpButtons(MenuButton fileMenuButton, MenuButton diseaseList, MenuButton MedicineListBox, Stage primaryStage) {

        Button start = setUpImageButton(ConstantValues.PLAY_BUTTON_IMAGE_FILE);
        Button pause = setUpImageButton(ConstantValues.PAUSE_BUTTON_IMAGE_FILE);
        Button fastForward = setUpImageButton(ConstantValues.FAST_FORWARD_BUTTON_IMAGE_FILE);
        Button backForward = setUpImageButton(ConstantValues.BACK_FORWARD_BUTTON_IMAGE_FILE);
        // set up the buttons on the buttonBar
        // set up the buttons on the buttonBar
        Button disease = new Button("Create Disease");
        Button medicine = new Button("Create Medicine");

        setUpEventHandlers(primaryStage, disease, medicine, start, pause, fastForward, backForward, diseaseList, MedicineListBox);

        // addComponent the file menu, separators and the object buttons to the button bar
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(pause, start);
        stackPane.setMaxHeight(0);

        buttonBar.getChildren().addAll(
                fileMenuButton,
                disease, medicine, diseaseList, MedicineListBox, backForward, stackPane, fastForward, speedLabel, timer);
        buttonBar.setSpacing(10);
        buttonBar.setPadding(new Insets(10, 10, 10, 10));

        fastForward.setDisable(true);
        backForward.setDisable(true);
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

    private void setUpEventHandlers(final Stage primaryStage, final Button disease, final Button medicine,
                                    final Button start, final Button pause, final Button fastForwardbutton,
                                    final Button backForwardbutton, final MenuButton diseaseListBox, final MenuButton medicineListBox) {
        start.setOnAction(event -> {
            AlgorithmThread().start();
            startTimer().start();
            start.setVisible(false);
            pause.setVisible(true);
            isWorking = true;
            world.getTime().setRunSpeed(1);
            isStarted = true;
            if (world.getTime().getSavedRunSpeed() != 0) {
                world.getTime().setRunSpeed(world.getTime().getSavedRunSpeed());
            }
            speedLabel.setText("x"+world.getTime().getRunSpeed());
            addtoListBoxes(DiseaseListBox,MedicineListBox);
            setPointers(diseaseListBox,medicineListBox,primaryStage);
            backForwardbutton.setDisable(false);
            if(world.getTime().getRunSpeed()<70){
                fastForwardbutton.setDisable(false);
            }
            else{
                fastForwardbutton.setDisable(true);}

        });

        pause.setOnAction(event -> {
            start.setVisible(true);
            pause.setVisible(false);
            backForwardbutton.setDisable(true);
            fastForwardbutton.setDisable(true);
            isWorking = false;
            world.getTime().saveRunSpeed();
            world.getTime().setRunSpeed(0);
            speedLabel.setText("x" + world.getTime().getRunSpeed());
        });

        disease.setOnAction(event -> {
            if (popup != null) {
                popup.hide();
            }
            SetUpPopupDisease(diseaseListBox, medicineListBox, primaryStage);
            popup.show(primaryStage);
        });
        medicine.setOnAction(event -> {
            if (popup != null) {
                popup.hide();
            }
            SetUpPopupMedicine(diseaseListBox, medicineListBox, primaryStage);
            popup.show(primaryStage);
        });

        fastForwardbutton.setOnAction(event -> {
            backForwardbutton.setDisable(false);
            world.getTime().addRunSpeed();
            if (world.getTime().getRunSpeed() >= 70) {
                fastForwardbutton.setDisable(true);
            }
            speedLabel.setText("x" + world.getTime().getRunSpeed());
        });

        backForwardbutton.setOnAction(event -> {
            fastForwardbutton.setDisable(false);
            world.getTime().substract();
            if (world.getTime().getRunSpeed() <= 1) {
                backForwardbutton.setDisable(true);
            }
            speedLabel.setText("x" + world.getTime().getRunSpeed());
        });

        setPointers(diseaseListBox, medicineListBox, primaryStage);
        primaryStage.setOnCloseRequest(event -> {
            if (isStarted) {
                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Simulation is started, do you wish to exit? If yes all progress will be lost.");
                a.setHeaderText(null);
                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("Cancel");
                ButtonType buttonTypeSave = new ButtonType("Save and exit");
                a.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeSave);
                Optional<ButtonType> result = a.showAndWait();
                if (result.get() == buttonTypeYes) {
                    try {

                        if (AlgorithmThread() != null && AlgorithmThread().isAlive()) {
                            AlgorithmThread().interrupt();
                        }
                        if (startTimer() != null && startTimer().isAlive()) {
                            startTimer().interrupt();
                        }
                        System.out.print(1);
                        System.exit(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (result.get() == buttonTypeSave) {
                    try {
                        infectionSpread.saveInfectionSpread(primaryStage, world.getTime());
                        if (AlgorithmThread() != null && AlgorithmThread().isAlive()) {
                            AlgorithmThread().interrupt();
                        }
                        if (startTimer() != null && startTimer().isAlive()) {
                            startTimer().interrupt();
                        }
                        System.out.print(0);
                        System.exit(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else System.out.print(2);
                event.consume();

            }
        });

        mapCanvas.getCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 1) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    selectCountryOnMap(event);
                } else if (event.getButton() == MouseButton.PRIMARY) {
                    createInfectionPointFromClick(event, primaryStage);
                }
            }
            event.consume();
        });

    }

    private void createInfectionPointFromClick(MouseEvent event, Stage primaryStage) {
        if (world.getCountry("Bulgaria").isPresent()) {
            Country country = world.getCountry("Bulgaria").get();
            java.awt.geom.Point2D mapInfectionPoint = mapCanvas.getGeoFinder().screenToMapCoordinates(event.getX(), event.getY());
            country.addInfectionPoint(mapInfectionPoint);
            primaryStage.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
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

    private void SetUpPopupDisease(MenuButton diseaseListBox, MenuButton medicineListBox, Stage primaryStage) {
        // Set up Popups

        popup = new Popup();
        Rectangle popUpRectangleBackground = new Rectangle(360, 350);
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
        final Button cancel = new Button("Cancel");
        final ComboBox diseaseType = new ComboBox();
        diseaseType.getItems().addAll(DiseaseType.BACTERIA, DiseaseType.FUNGUS, DiseaseType.PARASITE, DiseaseType.VIRUS);

        lethality.setShowTickLabels(true);
        lethality.setShowTickMarks(true);
        virulence.setShowTickLabels(true);
        virulence.setShowTickMarks(true);

        final Label nameCaption = new Label("Name:");
        final Label virusType = new Label("Virus type:");
        final Label prefTempCaption = new Label("Preferred temperature:");
        final Label tempToleranceCaption = new Label("Temperature tolerance:");
        final Label lethalityCaption = new Label("Lethality Level:");
        final Label virulenceCaption = new Label("Virulence Level:");
        final Label space = new Label("      ");

        nameCaption.setPrefWidth(170);
        virusType.setPrefWidth(170);
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
        HBox virusTypeHB = new HBox();
        HBox prefTempHB = new HBox();
        HBox tempToleranceHB = new HBox();
        HBox lethalityHB = new HBox();
        HBox virulenceHB = new HBox();
        HBox buttonsHB = new HBox();
        VBox buttonsAndFieldsVB = new VBox();

        nameHB.getChildren().addAll(nameCaption, name);
        virusTypeHB.getChildren().addAll(virusType, diseaseType);
        prefTempHB.getChildren().addAll(prefTempCaption, preferredTemp);
        tempToleranceHB.getChildren().addAll(tempToleranceCaption, tempTolerance);
        lethalityHB.getChildren().addAll(lethalityCaption, lethality, lethalityValue);
        virulenceHB.getChildren().addAll(virulenceCaption, virulence, virulenceValue);
        buttonsHB.getChildren().addAll(save, space, cancel);
        buttonsHB.setPadding(new Insets(0, 10, 0, 10));
        buttonsAndFieldsVB.getChildren().addAll(nameHB, virusTypeHB, prefTempHB, tempToleranceHB, lethalityHB, virulenceHB, buttonsHB);

        popup.getContent().addAll(popUpRectangleBackground, buttonsAndFieldsVB);
        buttonsAndFieldsVB.setSpacing(10);
        buttonsAndFieldsVB.setPadding(new Insets(10, 10, 10, 10));

        save.setOnAction(event -> {
            try {
                Disease disease = new Disease(name.getText(), (DiseaseType) diseaseType.getValue(),
                        new DiseaseProperties((int) lethality.getValue(),
                                Double.parseDouble(preferredTemp.getText()),
                                Double.parseDouble(tempTolerance.getText()),
                                virulence.getValue() / 100));
                infectionSpread.addDisease(disease);
                addtoListBoxes(DiseaseListBox, MedicineListBox);
                setPointers(diseaseListBox, medicineListBox, primaryStage);
                popup.hide();
            } catch (Exception ex) {
                name.setPromptText("not filled in");
                preferredTemp.setPromptText("not filled in");
                tempTolerance.setPromptText("not filled in");
            }
        });
        cancel.setOnAction(event -> {
            popup.hide();
        });
    }

    private void SetUpPopupMedicine(MenuButton diseaseListBox, MenuButton medicineListBox, Stage primaryStage) {
        // Set up Popups

        popup = new Popup();
        Rectangle popUpRectangleBackground = new Rectangle(360, 350);
        popUpRectangleBackground.setFill(Color.AQUAMARINE);

        //Items in popup
        final TextField name = new TextField();

        //restricting the user to type only integers for the preferred temperature
        final TextField preferredTemp = createFractionTextField();

        //restricting the user to type only integers for the temperature tolerance
        final TextField tempTolerance = createFractionTextField();


        final Slider lethality = new Slider(0, 100, 50);
        final Slider virulence = new Slider(0, 100, 50);
        final Button save = new Button("Create medicine");
        final Button cancel = new Button("Cancel");
        final ComboBox diseaseType = new ComboBox();
        diseaseType.getItems().addAll(DiseaseType.BACTERIA, DiseaseType.FUNGUS, DiseaseType.PARASITE, DiseaseType.VIRUS);
        final ComboBox symptomType = new ComboBox();
        symptomType.getItems().addAll(SymptomType.COUGH, SymptomType.NAUSEA);

        lethality.setShowTickLabels(true);
        lethality.setShowTickMarks(true);
        virulence.setShowTickLabels(true);
        virulence.setShowTickMarks(true);

        final Label nameCaption = new Label("Name:");
        final Label diseaseTypeL = new Label("Targeted disease type:");
        final Label symptomTypeL = new Label("Targeted symptom type:");
        final Label prefTempCaption = new Label("Preferred temperature:");
        final Label tempToleranceCaption = new Label("Temperature tolerance:");
        final Label lethalityCaption = new Label("Lethality Level:");
        final Label virulenceCaption = new Label("Virulence Level:");
        final Label space = new Label("      ");

        nameCaption.setPrefWidth(170);
        diseaseTypeL.setPrefWidth(170);
        symptomTypeL.setPrefWidth(170);
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
        HBox diseaseTypeHB = new HBox();
        HBox symptomTypeHB = new HBox();
        HBox prefTempHB = new HBox();
        HBox tempToleranceHB = new HBox();
        HBox lethalityHB = new HBox();
        HBox virulenceHB = new HBox();
        HBox buttonsHB = new HBox();
        VBox buttonsAndFieldsVB = new VBox();

        nameHB.getChildren().addAll(nameCaption, name);
        diseaseTypeHB.getChildren().addAll(diseaseTypeL, diseaseType);
        symptomTypeHB.getChildren().addAll(symptomTypeL, symptomType);
        prefTempHB.getChildren().addAll(prefTempCaption, preferredTemp);
        tempToleranceHB.getChildren().addAll(tempToleranceCaption, tempTolerance);
        lethalityHB.getChildren().addAll(lethalityCaption, lethality, lethalityValue);
        virulenceHB.getChildren().addAll(virulenceCaption, virulence, virulenceValue);
        buttonsHB.getChildren().addAll(save, space, cancel);
        buttonsHB.setPadding(new Insets(0, 10, 0, 10));
        buttonsAndFieldsVB.getChildren().addAll(nameHB, diseaseTypeHB, symptomTypeHB, prefTempHB, tempToleranceHB, lethalityHB, virulenceHB, buttonsHB);

        popup.getContent().addAll(popUpRectangleBackground, buttonsAndFieldsVB);
        buttonsAndFieldsVB.setSpacing(10);
        buttonsAndFieldsVB.setPadding(new Insets(10, 10, 10, 10));

        save.setOnAction(event -> {
            try {
                Medicine medicine = new Medicine(name.getText(), (DiseaseType) diseaseType.getValue(),
                        (SymptomType) symptomType.getValue(),
                        new DiseaseProperties((int) lethality.getValue(),
                                Double.parseDouble(preferredTemp.getText()),
                                Double.parseDouble(tempTolerance.getText()),
                                virulence.getValue() / 100));
                medicineSpread.addMedicine(medicine);
                setPointers(diseaseListBox, medicineListBox, primaryStage);
                for (Medicine m : medicineSpread.getMedicineList()
                        ) {
                    System.out.println(m.toString());
                }
                addtoListBoxes(DiseaseListBox, MedicineListBox);
                popup.hide();
            } catch (Exception ex) {
                name.setPromptText("not filled in");
                preferredTemp.setPromptText("not filled in");
                tempTolerance.setPromptText("not filled in");
            }
        });
        cancel.setOnAction(event -> {
            popup.hide();
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

    private Thread startTimer() {
        return new Thread(() -> {
            while (isWorking) {
                world.getTime().setElapsedTime();
                Platform.runLater(() -> timer.setText(world.getTime().toString()));
                try {
                    Thread.sleep(world.getTime().timerSleepTime());
                } catch (InterruptedException e) {
                }
            }
        });

    }

    private Thread AlgorithmThread() {
        return new Thread(() -> {
            while (isWorking) {
                if(world.getTime().checkHour()){
                    infectionSpread.applyAlgorithm(selectedDisease);
                    mapCanvas.updateInfectionPointsCoordinates(world.getAllInfectionPoints());
                    try {
                        Thread.sleep(33);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
    }

    private void addtoListBoxes(MenuButton DiseasemenuButton, MenuButton MedicineButton) {
        DiseasemenuButton.getItems().clear();
        MedicineButton.getItems().clear();
        for (Disease d : infectionSpread.getDiseaseList()
                ) {
            DiseasemenuButton.getItems().add(new MenuItem(d.getName()));
        }
        for (Medicine d : medicineSpread.getMedicineList()
                ) {
            MedicineButton.getItems().add(new MenuItem(d.getName()));
        }

    }

    private void setPointers(MenuButton diseaseListBox, MenuButton medicineListBox, Stage primaryStage) {
        for (MenuItem item : diseaseListBox.getItems()
                ) {
            item.setOnAction(event -> {
                for (Disease d : infectionSpread.getDiseaseList()
                        ) {
                    if (item.getText().equals(d.getName())) {
                        Image pointer = new Image("file:./images/hazardpointer.png");
                        primaryStage.getScene().setCursor(new ImageCursor(pointer));
                        selectedDisease = d;
                        System.out.print(selectedDisease.toString());
                    }
                }

            });
        }
        for (MenuItem item : medicineListBox.getItems()
                ) {
            item.setOnAction(event -> {
                for (Medicine m : medicineSpread.getMedicineList()
                        ) {
                    if (item.getText().equals(m.getName())) {
                        Image pointer = new Image("file:./images/medicinepointer.png");
                        primaryStage.getScene().setCursor(new ImageCursor(pointer));
                    }
                }

            });
        }
    }

}
