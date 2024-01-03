package views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

public class ChooseRoomView {
    private Stage dialog;
    private Label roomInputLabel;
    private Spinner<Integer> roomInput;
    private Button continueButton;
    private boolean dialogStatus = false; // Whether dialog has succeeded

    public ChooseRoomView()
    {
        dialog = new Stage();

        VBox dialogVBox = new VBox(20);
        dialogVBox.setPadding(new Insets(20, 20, 20, 20));
        dialogVBox.setStyle("-fx-background-color: #121212;");

        // Room input label
        roomInputLabel = new Label("Enter a start room (1-5)");
        roomInputLabel.setId("roomInputLabel");
        roomInputLabel.setStyle("-fx-text-fill: #e8e6e3");
        roomInputLabel.setFont(new Font(16));

        // Room input
        roomInput = new Spinner<>(1, 5, 1);
        roomInput.setId("roomInput");
        roomInput.setStyle("-fx-text-fill: #e8e6e3");

        // Close dialog button
        continueButton = new Button("Continue");
        continueButton.setId("continueButton");
        continueButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        continueButton.setFont(new Font(16));
        continueButton.setOnAction(e -> {
            dialogStatus = true;
            dialog.close();
        });
        AdventureGameView.makeButtonAccessible(continueButton, "Continue", "Continue", "Continue to game");

        dialogVBox.getChildren().addAll(roomInputLabel, roomInput, continueButton);

        Scene dialogScene = new Scene(dialogVBox, 200, 160);
        dialog.setScene(dialogScene);
    }

    /**
     * Shows the dialog and wait for exit
     */
    public void showAndWait()
    {
        dialog.showAndWait();
    }

    /**
     * Get the starting room number
     * @return Selected start room
     */
    public Optional<Integer> getStartNum() {
        if (dialogStatus)
            return Optional.of(roomInput.getValue());

        return Optional.empty();
    }
}
