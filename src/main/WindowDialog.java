package main;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Owner: Ivan
 *
 * A custom dialog asking the user for confirmation when progress may be lost.
 * <p>
 * After making a new instance the showAndWait method should be used and then the
 * instance can be queried about the result.
 */
public class WindowDialog {

    private ButtonType buttonTypeYes = new ButtonType("Yes");
    private ButtonType buttonTypeNo = new ButtonType("No");
    private ButtonType buttonTypeSave = new ButtonType("Save and exit");

    private Alert alert;
    private Optional<ButtonType> result;

    public WindowDialog() {
        alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Simulation is already started,\n" +
                        "do you wish to lose any unsaved progress?");
        alert.setHeaderText(null);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeSave);

    }

    public void showAndWait() {
        result = alert.showAndWait();
    }

    public boolean isYes() {
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    public boolean isNo() {
        return result.isPresent() && result.get() == buttonTypeNo;
    }

    public boolean isSaveAndExit() {
        return result.isPresent() && result.get() == buttonTypeSave;
    }
}
