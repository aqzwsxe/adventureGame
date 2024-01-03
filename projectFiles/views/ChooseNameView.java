package views;

import javafx.geometry.Insets;
import javafx.scene.AccessibleRole;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

public class ChooseNameView {
    private Stage dialog;
    private Label nameInputLabel;
    private TextField nameInput;
    private Button continueButton;
    private boolean dialogStatus = false; // Whether dialog has succeeded

    public ChooseNameView()
    {
        dialog = new Stage();

        VBox dialogVBox = new VBox(20);
        dialogVBox.setPadding(new Insets(20, 20, 20, 20));
        dialogVBox.setStyle("-fx-background-color: #121212;");

        // Room input label
        nameInputLabel = new Label("Enter character name");
        nameInputLabel.setId("nameInput");
        nameInputLabel.setStyle("-fx-text-fill: #e8e6e3");
        nameInputLabel.setFont(new Font(16));

        // Room input
        nameInput = new TextField();
        nameInput.setId("roomInput");
        nameInput.setFont(new Font(16));
        nameInput.setFocusTraversable(true);
        nameInput.setAccessibleRole(AccessibleRole.TEXT_AREA);
        nameInput.setAccessibleRoleDescription("Character name entry box.");
        nameInput.setAccessibleText("Enter the characters name here.");
        nameInput.setAccessibleHelp("Enter the character who's adventure this is.");

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

        dialogVBox.getChildren().addAll(nameInputLabel, nameInput, continueButton);

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
     * Get character name
     * @return Character name
     */
    public Optional<String> getName() {
        if (dialogStatus)
            return Optional.of(nameInput.getText());

        return Optional.empty();
    }
}
