package main;

import algorithm.InfectionSpread;
import algorithm.MedicineSpread;
import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import disease.SymptomType;
import files.SaveLoadManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
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
import javafx.stage.WindowEvent;
import map.MapCanvas;
import world.Country;
import world.World;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * Owner: Ivan
 */

public class Main extends Application {

    private World world;
    private MapCanvas mapCanvas;
    private InfectionSpread infectionSpread;
    private MedicineSpread medicineSpread;
    private SaveLoadManager saveLoadManager;

    private HBox buttonBar;
    private Popup popup = null;
    private Popup backgroundBlock = null;
    private Label timer = new Label();
    private Label speedLabel = new Label();
    private MenuButton medicineListBox;
    private MenuButton diseaseListBox;
    private Disease selectedDisease;
    private Medicine selectedMedicine;
    private GaussianBlur blur;
    private Button start;
    private Button pause;
    private Button fastForward;
    private Button backForward;

    private boolean isClickedOnMapDisease;
    private boolean isClickedOnMapMedicine;

    // setting this to false will make threads finish execution
    private volatile boolean areThreadsRunning;
    // are the time and main algorithms currently running
    private volatile boolean isSimulationRunning;
    // has the simulation been started at some point
    private volatile boolean didSimulationAlreadyStart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        root.setMinWidth(640);
        root.setMinHeight(480);

        world = new World();

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        mapCanvas = new MapCanvas((int) bounds.getWidth(), (int) bounds.getHeight());
        mapCanvas.setCities(world.getAllCities());

        infectionSpread = new InfectionSpread(world, mapCanvas);
        medicineSpread = new MedicineSpread();
        saveLoadManager = new SaveLoadManager();

        timer.setText(world.getTime().toString());
        timer.setId("timer");
        updateTimeSpeedLabel();

        setUpButtonBar(primaryStage);
        root.getChildren().addAll(buttonBar, mapCanvas.getCanvas());

        Scene scene = new Scene(root);
        primaryStage.setTitle("Global Epidemic Simulation");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        for (Country c : world.getListOfCountries()) {
            c.addListeners(mapCanvas);
        }

        scene.getStylesheets().add(ConstantValues.CSS_STYLE_FILE);
        blur = new GaussianBlur(0);
        root.setEffect(blur);

        // start the threads
        areThreadsRunning = true;
        createAlgorithmThread().start();
        createMedicineThread().start();
        startTimer().start();
    }

    private Thread createAlgorithmThread() {
        return new Thread(() -> {
            while (areThreadsRunning) {
                if (isSimulationRunning && isClickedOnMapDisease) {
                    infectionSpread.applyAirplaneAlgorithm();
                    if (world.getTime().checkHourHasPassed()) {
                        if (selectedDisease == null) {
                            Platform.runLater(() -> saveLoadManager
                                    .InformativeMessage("Please select a disease first!"));
                        } else {
                            infectionSpread.applyAlgorithm(selectedDisease);
                            mapCanvas.pushNewPercentageValue(world.calculateWorldTotalInfectedPercentage());
                            System.gc();
                        }
                    }
                    try {
                        Thread.sleep(1000 / ConstantValues.FPS);
                    } catch (InterruptedException e) {
                        // empty on purpose
                    }
                }
            }
        });
    }

    private Thread createMedicineThread() {
        return new Thread(() -> {
            while (areThreadsRunning) {
                if (isSimulationRunning && isClickedOnMapMedicine) {
                    if (world.getTime().checkHourHasPassed()) {
                        if (medicineSpread.getMedicine() == null) {
                            Platform.runLater(() -> saveLoadManager
                                    .InformativeMessage("Please select a medicine first!"));
                        } else {
                            medicineSpread.medicineAlgorithm();
                        }
                    }
                    try {
                        Thread.sleep(1000 / ConstantValues.FPS);
                    } catch (InterruptedException e) {
                        // empty on purpose
                    }
                }
            }
        });
    }

    private Thread startTimer() {
        return new Thread(() -> {
            while (areThreadsRunning) {
                if (isSimulationRunning) {

                    world.getTime().tickTime();
                    Platform.runLater(() -> timer.setText(world.getTime().toString()));
                    try {
                        Thread.sleep(world.getTime().calculateTimerSleepTime());
                    } catch (InterruptedException e) {
                        // empty on purpose
                    }
                }
            }
        });
    }

    private void startNewSimulation(Stage primaryStage) {
        if (didSimulationAlreadyStart) {
            WindowDialog newSimulationDialog = new WindowDialog();
            newSimulationDialog.showAndWait();

            if (newSimulationDialog.isYes()) {
                finishThreads();
                saveLoadManager.clearFilePath();
                start(primaryStage);
            }
            if (newSimulationDialog.isSaveAndExit()) {
                saveLoadManager.saveFile(primaryStage, world);
                finishThreads();
                saveLoadManager.clearFilePath();
                start(primaryStage);
            }
        }
    }

    private void finishThreads() {
        areThreadsRunning = false;
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

    /**
     * Starts the simulation
     */
    private void startResumeSimulation(Stage primaryStage) {
        start.setVisible(false);
        pause.setVisible(true);
        isSimulationRunning = true;
        didSimulationAlreadyStart = true;
        updateTimeSpeedLabel();
        addToListBoxes(diseaseListBox, medicineListBox);
        setPointers(diseaseListBox, medicineListBox, primaryStage);
        updateSpeedBackwardForwardButtons();
    }

    /**
     * Pauses the simulation
     */
    private void pauseSimulation() {
        start.setVisible(true);
        pause.setVisible(false);
        updateSpeedBackwardForwardButtons();
        isSimulationRunning = false;
        updateTimeSpeedLabel();
    }

    private void setUpEventHandlers(final Stage primaryStage, final Button disease, final Button medicine) {
        start.setOnAction(event -> startResumeSimulation(primaryStage));

        pause.setOnAction(event -> pauseSimulation());

        disease.setOnAction(event -> {
            SetUpPopupDisease(diseaseListBox, medicineListBox, primaryStage);
            blockAndShowPopup(primaryStage);
        });
        medicine.setOnAction(event -> {
            SetUpPopupMedicine(diseaseListBox, medicineListBox, primaryStage);
            blockAndShowPopup(primaryStage);
        });

        fastForward.setOnAction(event -> {
            backForward.setDisable(false);
            world.getTime().increaseSpeed();
            updateSpeedBackwardForwardButtons();
            updateTimeSpeedLabel();
        });

        backForward.setOnAction(event -> {
            fastForward.setDisable(false);
            world.getTime().decreaseSpeed();
            updateSpeedBackwardForwardButtons();
            updateTimeSpeedLabel();
        });

        primaryStage.setOnCloseRequest(event -> closeApplication(primaryStage, event));

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

        setPointers(diseaseListBox, medicineListBox, primaryStage);
    }

    private void blockAndShowPopup(Stage primaryStage) {
        backgroundBlock.show(primaryStage);
        popup.show(primaryStage);
    }

    private void createInfectionPointFromClick(MouseEvent event, Stage primaryStage) {
        if (selectedDisease != null) {
            Point2D mapPoint = mapCanvas.getGeoFinder()
                    .screenToMapCoordinates(event.getX(), event.getY());
            infectionSpread.addInfectionToCountryAtMapCoordinates(mapPoint);
            primaryStage.getScene().setCursor(Cursor.DEFAULT);
            isClickedOnMapDisease = true;
        }
        if (selectedMedicine != null) {
            String selectedCode = mapCanvas.getGeoFinder()
                    .getCountryCodeFromScreenCoordinates(event.getX(), event.getY());
            Optional<Country> countryMaybe = world.getCountryByCode(selectedCode);
            if (countryMaybe.isPresent()) {
                Country country = countryMaybe.get();
                medicineSpread.addInitialCountry(country);
                medicineSpread.setMedicine(selectedMedicine);
                selectedMedicine = null;
                isClickedOnMapMedicine = true;
            }
        }
    }

    /**
     * Changes the rendering style of the clicked on country and displays information about it.
     */
    private void selectCountryOnMap(MouseEvent event) {
        String selectedCode = mapCanvas.getGeoFinder().getCountryCodeFromScreenCoordinates(event.getX(), event.getY());
        Optional<Country> countryMaybe = world.getCountryByCode(selectedCode);
        if (countryMaybe.isPresent()) {
            Country country = countryMaybe.get();
            mapCanvas.selectCountry(event.getX(), event.getY(), country);
        }
    }

    /**
     * Creates a text field which only takes input in the format: digits followed by a single comma or dot
     * followed by more digits, or just the initial digits.
     */
    private TextField createFractionTextField() {
        return new TextField() {
            @Override
            public void replaceText(int i, int j, String string) {
                if (string.isEmpty() || (this.getText() + string).matches("(-)?(\\d+([.,])?(\\d+)?)?")) {
                    super.replaceText(i, j, string);
                }
            }
        };
    }

    /**
     * Update the disease and medicine lists to represent the currently existing
     * diseases and medicines.
     *
     * @param DiseaseMenuButton the menu containing the diseases
     * @param MedicineButton    the menu containing the medicines
     */
    private void addToListBoxes(MenuButton DiseaseMenuButton, MenuButton MedicineButton) {
        DiseaseMenuButton.getItems().clear();
        MedicineButton.getItems().clear();
        for (Disease disease : infectionSpread.getDiseaseList()) {
            DiseaseMenuButton.getItems().add(new MenuItem(disease.getName()));
        }
        for (Medicine medicine : medicineSpread.getMedicineList()) {
            MedicineButton.getItems().add(new MenuItem(medicine.getName()));
        }

    }

    private void setPointers(MenuButton diseaseListBox, MenuButton medicineListBox, Stage primaryStage) {
        for (MenuItem item : diseaseListBox.getItems()) {
            item.setOnAction(event -> {
                for (Disease disease : infectionSpread.getDiseaseList()) {
                    if (item.getText().equals(disease.getName())) {
                        Image pointer = new Image("file:./images/hazardpointer.png");
                        primaryStage.getScene().setCursor(new ImageCursor(pointer));
                        selectedDisease = disease;
                    }
                }

            });
        }
        for (MenuItem item : medicineListBox.getItems()) {
            item.setOnAction(event -> {
                for (Medicine medicine : medicineSpread.getMedicineList()) {
                    if (item.getText().equals(medicine.getName())) {
                        Image pointer = new Image("file:./images/medicinepointer.png");
                        primaryStage.getScene().setCursor(new ImageCursor(pointer));
                        selectedMedicine = medicine;
                    }
                }

            });
        }
    }

    private void setUpButtonBar(Stage primaryStage) {
        buttonBar = new HBox();
        buttonBar.getStyleClass().add("hbox");

        MenuButton fileMenuButton = new MenuButton("File");

        //addComponent items to file menu button
        MenuItem newSimulation = new MenuItem("New simulation");
        MenuItem openSimulation = new MenuItem("Open simulation");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem saveSimulation = new MenuItem("Save");
        MenuItem saveSimulationAs = new MenuItem("Save As...");

        fileMenuButton.getItems().addAll(newSimulation, openSimulation, separator,
                saveSimulation, saveSimulationAs);


        medicineListBox = new MenuButton("Medicines");
        diseaseListBox = new MenuButton("Diseases");
        addToListBoxes(diseaseListBox, medicineListBox);

        newSimulation.setOnAction(event -> startNewSimulation(primaryStage));
        openSimulation.setOnAction(event -> {
            World loadedWorld = saveLoadManager.openFile(primaryStage);
            if (loadedWorld != null) {
                world = loadedWorld;
                timer.setText(world.getTime().toString());
                mapCanvas.setCities(world.getAllCities());
                mapCanvas.pushNewPercentageValue(world.calculateWorldTotalInfectedPercentage());
                saveLoadManager.InformativeMessage("Opened!");
                isClickedOnMapDisease = true;
            }
        });
        saveSimulation.setOnAction(event -> saveLoadManager.saveFile(primaryStage, world));
        saveSimulationAs.setOnAction(event -> saveLoadManager.saveFileAs(primaryStage, world));

        // assign action handlers to the items in the file menu
        setUpButtons(fileMenuButton, primaryStage);
    }

    private void setUpButtons(MenuButton fileMenuButton, Stage primaryStage) {

        start = setUpImageButton(ConstantValues.PLAY_BUTTON_IMAGE_FILE);
        pause = setUpImageButton(ConstantValues.PAUSE_BUTTON_IMAGE_FILE);
        fastForward = setUpImageButton(ConstantValues.FAST_FORWARD_BUTTON_IMAGE_FILE);
        backForward = setUpImageButton(ConstantValues.BACK_FORWARD_BUTTON_IMAGE_FILE);
        // set up the buttons on the buttonBar
        Button disease = new Button("Create Disease");
        Button medicine = new Button("Create Medicine");

        setUpEventHandlers(primaryStage, disease, medicine);

        // addComponent the file menu, separators and the object buttons to the button bar
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(pause, start);
        stackPane.setMaxHeight(0);

        buttonBar.getChildren().addAll(
                fileMenuButton,
                disease, medicine, diseaseListBox, medicineListBox, backForward, stackPane, fastForward, speedLabel, timer);
        buttonBar.setSpacing(10);
        buttonBar.setPadding(new Insets(10, 10, 10, 10));

        fastForward.setDisable(true);
        backForward.setDisable(true);
    }

    private void closeApplication(Stage primaryStage, WindowEvent event) {
        if (didSimulationAlreadyStart) {
            WindowDialog newSimulationDialog = new WindowDialog();
            newSimulationDialog.showAndWait();
            if (newSimulationDialog.isYes()) {
                finishThreads();
                System.exit(0);
            } else if (newSimulationDialog.isSaveAndExit()) {
                saveLoadManager.saveFile(primaryStage, world);
                finishThreads();
                System.exit(0);
            }
            event.consume();
        }
        finishThreads();
        System.exit(0);
    }

    private void updateSpeedBackwardForwardButtons() {
        if (world.getTime().isAtMinSpeed()) {
            backForward.setDisable(true);
        } else {
            backForward.setDisable(false);
        }
        if (world.getTime().isAtMaxSpeed()) {
            fastForward.setDisable(true);
        } else {
            fastForward.setDisable(false);
        }
    }

    private void updateTimeSpeedLabel() {
        speedLabel.setText("x" + world.getTime().getTimeSpeed());
    }

    private void SetUpPopupDisease(MenuButton diseaseListBox, MenuButton medicineListBox, Stage primaryStage) {
        if (didSimulationAlreadyStart) {
            pauseSimulation();
        }
        popup = new Popup();
        backgroundBlock = new Popup();
        Rectangle popUpRectangleBackground = new Rectangle(390, 360);
        popUpRectangleBackground.setFill(Color.AQUAMARINE);
        blur.setRadius(15);

        Rectangle popUpRectangleBackgroundCover = new Rectangle();
        popUpRectangleBackgroundCover.setFill(Color.ALICEBLUE);
        popUpRectangleBackgroundCover.setOpacity(0.1);
        popUpRectangleBackgroundCover.setHeight(primaryStage.getHeight());
        popUpRectangleBackgroundCover.setWidth(primaryStage.getWidth());

        final Slider lethality = new Slider(0, 100, 50);
        final Slider virulence = new Slider(0, 100, 50);
        final Button save = new Button("Create disease");
        final Button cancel = new Button("Cancel");
        final ComboBox diseaseType = new ComboBox();
        diseaseType.getItems().addAll(DiseaseType.BACTERIA, DiseaseType.FUNGUS,
                DiseaseType.PARASITE, DiseaseType.VIRUS);

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

        final TextField name = new TextField();
        final TextField preferredTemp = createFractionTextField();
        final TextField tempTolerance = createFractionTextField();

        nameHB.getChildren().addAll(nameCaption, name);
        virusTypeHB.getChildren().addAll(virusType, diseaseType);
        prefTempHB.getChildren().addAll(prefTempCaption, preferredTemp);
        tempToleranceHB.getChildren().addAll(tempToleranceCaption, tempTolerance);
        lethalityHB.getChildren().addAll(lethalityCaption, lethality, lethalityValue);
        virulenceHB.getChildren().addAll(virulenceCaption, virulence, virulenceValue);
        buttonsHB.getChildren().addAll(save, space, cancel);
        buttonsHB.setPadding(new Insets(0, 10, 0, 10));
        buttonsAndFieldsVB.getChildren().addAll(nameHB, virusTypeHB, prefTempHB, tempToleranceHB,
                lethalityHB, virulenceHB, buttonsHB);

        popup.getContent().addAll(popUpRectangleBackground, buttonsAndFieldsVB);
        backgroundBlock.getContent().addAll(popUpRectangleBackgroundCover);
        buttonsAndFieldsVB.setSpacing(10);
        buttonsAndFieldsVB.setPadding(new Insets(10, 10, 10, 10));

        save.setOnAction(event -> {
            try {
                Disease disease = new Disease(name.getText(), (DiseaseType) diseaseType.getValue(),
                        new DiseaseProperties((int) lethality.getValue(),
                                Double.parseDouble(preferredTemp.getText()),
                                Double.parseDouble(tempTolerance.getText()),
                                virulence.getValue() / 100));
                System.out.print(virulence.getValue() / 200);
                infectionSpread.addDisease(disease);
                addToListBoxes(this.diseaseListBox, this.medicineListBox);
                setPointers(diseaseListBox, medicineListBox, primaryStage);
                popup.hide();
                blur.setRadius(0);
                backgroundBlock.hide();
                if (didSimulationAlreadyStart) {
                    startResumeSimulation(primaryStage);
                }


            } catch (Exception ex) {
                if (preferredTemp.getText().equals("-")) {
                    preferredTemp.setText("");
                }
                if (tempTolerance.getText().equals("-")) {
                    tempTolerance.setText("");
                }
                name.setPromptText("not filled in");
                preferredTemp.setPromptText("not filled in");
                tempTolerance.setPromptText("not filled in");
            }
        });
        cancel.setOnAction(event -> {
            popup.hide();
            blur.setRadius(0);
            backgroundBlock.hide();
            if (didSimulationAlreadyStart) {
                startResumeSimulation(primaryStage);
            }
        });
    }

    private void SetUpPopupMedicine(MenuButton diseaseListBox, MenuButton medicineListBox, Stage primaryStage) {
        if (didSimulationAlreadyStart) {
            pauseSimulation();
        }
        popup = new Popup();
        backgroundBlock = new Popup();
        blur.setRadius(15);

        Rectangle popUpRectangleBackground = new Rectangle(390, 380);
        popUpRectangleBackground.setFill(Color.AQUAMARINE);

        Rectangle popUpRectangleBackgroundCover = new Rectangle();
        popUpRectangleBackgroundCover.setFill(Color.ALICEBLUE);
        popUpRectangleBackgroundCover.setOpacity(0.1);
        popUpRectangleBackgroundCover.setHeight(primaryStage.getHeight());
        popUpRectangleBackgroundCover.setWidth(primaryStage.getWidth());

        final Slider lethality = new Slider(0, 100, 50);
        final Slider virulence = new Slider(0, 100, 50);
        final Button save = new Button("Create medicine");
        final Button cancel = new Button("Cancel");
        final ComboBox diseaseType = new ComboBox();
        diseaseType.getItems().addAll(DiseaseType.BACTERIA, DiseaseType.FUNGUS,
                DiseaseType.PARASITE, DiseaseType.VIRUS);
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

        final TextField name = new TextField();
        final TextField preferredTemp = createFractionTextField();
        final TextField tempTolerance = createFractionTextField();

        nameHB.getChildren().addAll(nameCaption, name);
        diseaseTypeHB.getChildren().addAll(diseaseTypeL, diseaseType);
        symptomTypeHB.getChildren().addAll(symptomTypeL, symptomType);
        prefTempHB.getChildren().addAll(prefTempCaption, preferredTemp);
        tempToleranceHB.getChildren().addAll(tempToleranceCaption, tempTolerance);
        lethalityHB.getChildren().addAll(lethalityCaption, lethality, lethalityValue);
        virulenceHB.getChildren().addAll(virulenceCaption, virulence, virulenceValue);
        buttonsHB.getChildren().addAll(save, space, cancel);
        buttonsHB.setPadding(new Insets(0, 10, 0, 10));
        buttonsAndFieldsVB.getChildren().addAll(nameHB, diseaseTypeHB, symptomTypeHB, prefTempHB,
                tempToleranceHB, lethalityHB, virulenceHB, buttonsHB);

        popup.getContent().addAll(popUpRectangleBackground, buttonsAndFieldsVB);
        backgroundBlock.getContent().addAll(popUpRectangleBackgroundCover);
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
                addToListBoxes(this.diseaseListBox, this.medicineListBox);
                popup.hide();
                blur.setRadius(0);
                backgroundBlock.hide();
                if (didSimulationAlreadyStart) {
                    startResumeSimulation(primaryStage);
                }
            } catch (Exception ex) {
                if (preferredTemp.getText().equals("-")) {
                    preferredTemp.setText("");
                }
                if (tempTolerance.getText().equals("-")) {
                    tempTolerance.setText("");
                }
                name.setPromptText("not filled in");
                preferredTemp.setPromptText("not filled in");
                tempTolerance.setPromptText("not filled in");
            }
        });
        cancel.setOnAction(event -> {
            popup.hide();
            blur.setRadius(0);
            backgroundBlock.hide();
            if (didSimulationAlreadyStart) {
                startResumeSimulation(primaryStage);
            }
        });
    }
}
