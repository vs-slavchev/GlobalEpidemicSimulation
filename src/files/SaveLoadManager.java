package files;

import com.sun.istack.internal.Nullable;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.World;

import java.io.*;

/**
 * Owner: Kaloyan
 */

public class SaveLoadManager {

    private String filePath;

    public void clearFilePath() {
        filePath = null;
    }

    public void saveFile(Stage primaryStage, World world) {
        // if there is no save file, go to 'save file as' dialog
        if (filePath == null) {
            saveFileAs(primaryStage, world);
            return;
        }

        try {
            FileOutputStream streamOut = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(streamOut);
            oos.writeObject(world);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Saved!");
            alert.setHeaderText(null);
            alert.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveFileAs(Stage primaryStage, World world) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                FileOutputStream streamOut = new FileOutputStream(file.getPath());
                ObjectOutputStream oos = new ObjectOutputStream(streamOut);
                oos.writeObject(world);
                filePath = file.getPath();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Nullable
    public World openFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                FileInputStream inputStream = new FileInputStream(file.getPath());
                ObjectInputStream ois = new ObjectInputStream(inputStream);
                World loadedWorld = (World) ois.readObject();
                filePath = file.getPath();

                return loadedWorld;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SIM", "*.sim")
        );
    }
}
