package xiaoDu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private xiaoDu xiaoDu;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DuUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/xiaoDu.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        String welcomeMessage = "Hello! I'm xiaoDu. What can I do for you?\n";

        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(welcomeMessage, dukeImage)
        );
    }

    public void setXiaoDu(xiaoDu d) {
        xiaoDu = d;
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = xiaoDu.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        userInput.clear();
    }
}