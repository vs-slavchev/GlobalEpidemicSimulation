package files;

import com.sun.istack.internal.Nullable;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.World;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Owner: Kaloyan
 */

public class SaveLoadManager {

    private String filePath;

    /**
     * setting the filePath variable to null
     */
    public void clearFilePath() {
        filePath = null;
    }

    /**
     * Saving the current state of the world variable to a file
     * if there is no file already created, execute the saveFileAs method
     *
     * @param primaryStage used to pass the Stage to the SaveAs
     *                     method to be used in the fileChooser
     * @param world        used to pass the instance of the world object
     *                     to be saved
     */
    public void saveFile(Stage primaryStage, World world) {
        // if there is no save file, go to 'save file as' dialog
        if (filePath == null) {
            saveFileAs(primaryStage, world);
        } else {
            try {
                FileOutputStream streamOut = new FileOutputStream(filePath);
                ObjectOutputStream oos = new ObjectOutputStream(streamOut);
                oos.writeObject(world);
                InformativeMessage("Saved!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Saving the current state of the world variable through a FileChooser
     * and creating a new file(save)  of the world or overwriting an existing
     * one if needed
     *
     * @param primaryStage used as a reference of the stage being used
     *                     in the fileChooser
     * @param world        used as reference of the world object to be saved
     */
    public void saveFileAs(Stage primaryStage, World world) {
        //getting current date-time
        String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".bin";
        //creating new FileChooser
        FileChooser fileChooser = new FileChooser();
        //setting the title
        fileChooser.setTitle("Save File");
        //setting the default name for the file
        fileChooser.setInitialFileName(timeLog);
        //setting the starting directory of the file chooser to be in the project's dir
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                FileOutputStream streamOut = new FileOutputStream(file.getPath());
                ObjectOutputStream oos = new ObjectOutputStream(streamOut);
                oos.writeObject(world);
                filePath = file.getPath();
                InformativeMessage("Saved!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Creating a FileChooser and selecting a file to be opened
     *
     * @param primaryStage used to pass the stage to the fileChooser
     * @return the world object read from the fileChooser or return a null
     * if the fileChooser is null
     */
    @Nullable
    public World openFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        configureOpenFileChooser(fileChooser);
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

    /**
     * setting all the needed parameters of the OpenFileChooser
     *
     * @param fileChooser used as reference of type FileChooser to pass
     */
    private void configureOpenFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Open file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("BIN", "*.bin")
        );
    }

    /**
     * Displaying a informative message about the action done
     *
     * @param text used to pass the text to be used in the MessageBox
     */
    public void InformativeMessage(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, text);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
