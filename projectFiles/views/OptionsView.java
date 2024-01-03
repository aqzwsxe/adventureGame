package views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OptionsView {
    public static final int DEFAULT_FONT_SIZE = 16;
    private AdventureGameView gameView; // Game view window
    private Stage dialog; // Options view window
    private Button saveBtn, loadBtn, restartBtn, closeBtn; // Action buttons
    private Label saveGameLabel, fontSizeLabel, bgColorLabel;
    private ColorPicker bgColorPicker;
    private Spinner<Integer> fontSizeInput;

    public OptionsView(AdventureGameView gameView) {
        this.gameView = gameView;

        // Options dialog box
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); // Ensure options is dealt with before game
        dialog.initOwner(gameView.stage); // Set parent window

        // Dialog view
        VBox dialogVBox = new VBox(20);
        dialogVBox.setPadding(new Insets(20, 20, 20, 20));
        dialogVBox.setStyle("-fx-background-color: " + gameView.bgColor + ";");

        // Options view
        VBox optionsVBox = new VBox(20);
        optionsVBox.setPadding(new Insets(10, 10, 10, 10));
        optionsVBox.setStyle("-fx-background-color: " + gameView.bgColor + ";");

        // Options scroll panel
        ScrollPane scrollPane = new ScrollPane(optionsVBox);
        scrollPane.setPadding(new Insets(10));
        scrollPane.setStyle("-fx-background: " + gameView.bgColor + ";");
        scrollPane.setFitToWidth(true); // Ensure the pane only scrolls vertically
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Close button
        closeBtn = new Button("Close");
        closeBtn.setId("closeOptionsBtn");
        closeBtn.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        closeBtn.setPrefSize(200, 50);
        closeBtn.setFont(new Font(16 + gameView.fontSizeModifier));
        closeBtn.setOnAction(e -> dialog.close());
        AdventureGameView.makeButtonAccessible(closeBtn, "Close Options", "This is a button to close the options game window", "Use this button to close the options game window.");

        // Reset game button
        restartBtn = new Button("Restart Game");
        restartBtn.setId("resetOptionsBtn");
        restartBtn.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        restartBtn.setPrefSize(200, 50);
        restartBtn.setFont(new Font(16 + gameView.fontSizeModifier));
        restartBtn.setOnAction(e -> restartGame());
        AdventureGameView.makeButtonAccessible(restartBtn, "Restart Game", "This is a button to restart the game", "Use this button to restart the game.");

        // Save and close buttons
        saveGameLabel = new Label("Save/Load Game");
        saveGameLabel.setId("saveGameLabel");
        saveGameLabel.setStyle("-fx-text-fill: #e8e6e3");
        saveGameLabel.setFont(new Font(16 + gameView.fontSizeModifier));

        saveBtn = new Button("Save Game");
        saveBtn.setId("saveBtn");
        saveBtn.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        saveBtn.setPrefSize(200, 50);
        saveBtn.setFont(new Font(16 + gameView.fontSizeModifier));
        saveBtn.setOnAction(e -> {
            dialog.requestFocus();
            SaveView saveView = new SaveView(gameView);
        });
        AdventureGameView.makeButtonAccessible(saveBtn, "Save Button", "This button saves the game.", "This button saves the game. Click it in order to save your current progress, so you can play more later.");

        loadBtn = new Button("Load Game");
        loadBtn.setId("loadBtn");
        loadBtn.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        loadBtn.setPrefSize(200, 50);
        loadBtn.setFont(new Font(16 + gameView.fontSizeModifier));
        loadBtn.setOnAction(e -> {
            dialog.requestFocus();
            LoadView loadView = new LoadView(gameView);
        });
        AdventureGameView.makeButtonAccessible(loadBtn, "Load Button", "This button loads a game from a file.", "This button loads the game from a file. Click it in order to load a game that you saved at a prior date.");

        HBox gameStateBtnBox = new HBox(saveBtn, loadBtn);
        gameStateBtnBox.setSpacing(20);
        gameStateBtnBox.setStyle("-fx-background-color: " + gameView.bgColor + ";");

        // Background color
        bgColorLabel = new Label("Background Color");
        bgColorLabel.setId("bgColorLabel");
        bgColorLabel.setStyle("-fx-text-fill: #e8e6e3");
        bgColorLabel.setFont(new Font(16 + gameView.fontSizeModifier));

        bgColorPicker = new ColorPicker(Color.valueOf(gameView.bgColor));
        bgColorPicker.setId("bgColorPicker");
        bgColorPicker.setStyle("-fx-text-fill: #e8e6e3");
        bgColorPicker.setOnAction(e -> {
            // Convert from color class to hex code
            Color color = bgColorPicker.getValue();
            String bgColor = String.format("#%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));
            // Update colors
            gameView.setBackgroundColor(bgColor);
            dialogVBox.setStyle("-fx-background-color: " + gameView.bgColor + ";");
            optionsVBox.setStyle("-fx-background-color: " + gameView.bgColor + ";");
            scrollPane.setStyle("-fx-background-color: " + gameView.bgColor + ";");
            gameStateBtnBox.setStyle("-fx-background-color: " + gameView.bgColor + ";");
        });

        // Font size
        fontSizeLabel = new Label("Font Size");
        fontSizeLabel.setId("fontSizeLabel");
        fontSizeLabel.setStyle("-fx-text-fill: #e8e6e3");
        fontSizeLabel.setFont(new Font(16 + gameView.fontSizeModifier));

        fontSizeInput = new Spinner<>(8, 32, DEFAULT_FONT_SIZE + gameView.fontSizeModifier);
        fontSizeInput.setId("fontSizeInput");
        fontSizeInput.setStyle("-fx-text-fill: #e8e6e3");
        fontSizeInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            gameView.setFontSize(oldValue, newValue);
            resizeFont(newValue);
        });

        // Add children
        optionsVBox.getChildren().addAll(fontSizeLabel, fontSizeInput,
                bgColorLabel, bgColorPicker,
                saveGameLabel, gameStateBtnBox, restartBtn);
        dialogVBox.getChildren().addAll(scrollPane, closeBtn);

        Scene dialogScene = new Scene(dialogVBox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Resizes the font for this view
     */
    private void resizeFont(int fontSize) {

        // Resize all buttons and labels
        saveBtn.setFont(new Font(fontSize));
        loadBtn.setFont(new Font(fontSize));
        restartBtn.setFont(new Font(fontSize));
        closeBtn.setFont(new Font(fontSize));
        saveGameLabel.setFont(new Font(fontSize));
        fontSizeLabel.setFont(new Font(fontSize));
        bgColorLabel.setFont(new Font(fontSize));
    }

    /**
     * Restarts the game
     */
    private void restartGame() {
        gameView.stopArticulation();
        gameView.model.resetGame();
        gameView.updateItems();
        gameView.updateScene(null);
    }
}
