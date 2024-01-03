package views;

import AdventureModel.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

/**
 * Class AdventureGameView.
 */
public class AdventureGameView {

    AdventureGame model; //model of the game
    Stage stage; //stage on which all is rendered
    Button helpButton, timeButton, musicButton, UndoButton, WeatherButton, optionsButton; //buttons
    Boolean helpToggle = false; //is help on display?

    Boolean timeToggle = false;
    GridPane gridPane = new GridPane(); //to hold images and buttons
    Text roomDescLabel = new Text(); //to hold room description and/or instructions
    VBox objectsInRoom = new VBox(); //to hold room items
    VBox objectsInInventory = new VBox(); //to hold inventory items
    ImageView roomImageView; //to hold room image
    TextField inputTextField; //for user input

    HBox topButtons;
    Boolean encounter_force = false;
    private MediaPlayer mediaPlayer; //to play audio
    private boolean mediaPlaying; //to know if the audio is playing

    String bgColor = "#000000"; // Background color of screen

    List<Labeled> fontSizeTargets = new ArrayList<>(); // Labels that are affected by font size modification
    int fontSizeModifier = 0; // Font size modifier

    /**
     * Adventure Game View Constructor
     * __________________________
     * Initializes attributes
     */
    public AdventureGameView(AdventureGame model, Stage stage) {
        this.model = model;
        this.stage = stage;
        intiUI();
    }

    /**
     * Initialize the UI
     */
    public void intiUI() {
        ChooseRoomView roomView = new ChooseRoomView();
        while (true) {
            roomView.showAndWait();
            Optional<Integer> startRoom = roomView.getStartNum();

            if (startRoom.isPresent()) {
                model.player.setCurrentRoom(model.getRooms().get(startRoom.get()));
                break;
            }
        }

        String characterName;
        ChooseNameView nameView = new ChooseNameView();
        while (true) {
            nameView.showAndWait();
            Optional<String> charName = nameView.getName();

            if (charName.isPresent()) {
                characterName = charName.get();
                break;
            }
        }

        // setting up the stage
        this.stage.setTitle(characterName + "'s Adventure Game"); //Replace <YOUR UTORID> with your UtorID

        //Inventory + Room items
        objectsInInventory.setSpacing(10);
        objectsInInventory.setAlignment(Pos.TOP_CENTER);
        objectsInRoom.setSpacing(10);
        objectsInRoom.setAlignment(Pos.TOP_CENTER);

        // GridPane, anyone?
        gridPane.setPadding(new Insets(20));
        gridPane.setBackground(new Background(new BackgroundFill(
                Color.valueOf(bgColor),
                new CornerRadii(0),
                new Insets(0)
        )));

        //Three columns, three rows for the GridPane
        ColumnConstraints column1 = new ColumnConstraints(150);
        ColumnConstraints column2 = new ColumnConstraints(650);
        ColumnConstraints column3 = new ColumnConstraints(150);
        column3.setHgrow(Priority.SOMETIMES); //let some columns grow to take any extra space
        column1.setHgrow(Priority.SOMETIMES);

        // Row constraints
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints(550);
        RowConstraints row3 = new RowConstraints();
        row1.setVgrow(Priority.SOMETIMES);
        row3.setVgrow(Priority.SOMETIMES);

        gridPane.getColumnConstraints().addAll(column1, column2, column1);
        gridPane.getRowConstraints().addAll(row1, row2, row1);

        // Buttons
        helpButton = new Button("Instructions");
        helpButton.setId("Instructions");
        customizeButton(helpButton, 130, 50);
        makeButtonAccessible(helpButton, "Help Button", "This button gives game instructions.", "This button gives instructions on the game controls. Click it to learn how to play.");
        addInstructionEvent();
        fontSizeTargets.add(helpButton);

        timeButton = new Button("Time");
        timeButton.setId("Time");
        customizeButton(timeButton, 65, 50);
        makeButtonAccessible(timeButton, "Time Button", "This button gives current time of the real world", "This button gives current time of the real world");
        addTimeEvent();
        fontSizeTargets.add(timeButton);

        musicButton = new Button("Music");
        musicButton.setId("Music");
        customizeButton(musicButton, 65, 50);
        makeButtonAccessible(musicButton, "Music Button", "This button will show a list of music to paly", "This button will show a list of music to paly");
        addMusicEvent();
        fontSizeTargets.add(musicButton);

        UndoButton = new Button("Undo");
        musicButton.setId("Undo");
        customizeButton(UndoButton, 70, 50);
        makeButtonAccessible(UndoButton, "Undo Button", "This button will undo the move", "This button will undo the move");
        addUndoEvent();
        fontSizeTargets.add(UndoButton);

        WeatherButton = new Button("Weather");
        WeatherButton.setId("weather");
        customizeButton(WeatherButton, 90, 50);
        makeButtonAccessible(WeatherButton, "Weather Button", "This button will show the current weather", "This button will show the current weather");
        addWeatherEvent();
        fontSizeTargets.add(WeatherButton);

        optionsButton = new Button("Options");
        optionsButton.setId("Options");
        customizeButton(optionsButton, 80, 50);
        makeButtonAccessible(optionsButton, "Options Button", "This button gives game options.", "This button gives options to the game. Click it to modify settings to your desire.");
        addOptionsEvent();
        fontSizeTargets.add(optionsButton);

        topButtons = new HBox();
        topButtons.getChildren().addAll(helpButton, timeButton, musicButton, UndoButton, WeatherButton, optionsButton);
        topButtons.setSpacing(5);
        topButtons.setAlignment(Pos.CENTER);

        inputTextField = new TextField();
        inputTextField.setFont(new Font("Arial", 16));
        inputTextField.setFocusTraversable(true);

        inputTextField.setAccessibleRole(AccessibleRole.TEXT_AREA);
        inputTextField.setAccessibleRoleDescription("Text Entry Box");
        inputTextField.setAccessibleText("Enter commands in this box.");
        inputTextField.setAccessibleHelp("This is the area in which you can enter commands you would like to play.  Enter a command and hit return to continue.");
        addTextHandlingEvent(); //attach an event to this input field

        //labels for inventory and room items
        Label objLabel = new Label("Objects in Room");
        objLabel.setAlignment(Pos.CENTER);
        objLabel.setStyle("-fx-text-fill: white;");
        objLabel.setFont(new Font("Arial", 16));
        fontSizeTargets.add(objLabel);

        Label invLabel = new Label("Your Inventory");
        invLabel.setAlignment(Pos.CENTER);
        invLabel.setStyle("-fx-text-fill: white;");
        invLabel.setFont(new Font("Arial", 16));
        fontSizeTargets.add(invLabel);

        //add all the widgets to the GridPane
        gridPane.add(objLabel, 0, 0, 1, 1);  // Add label
        gridPane.add(topButtons, 1, 0, 1, 1);  // Add buttons
        gridPane.add(invLabel, 2, 0, 1, 1);  // Add label

        Label commandLabel = new Label("What would you like to do?");
        commandLabel.setStyle("-fx-text-fill: white;");
        commandLabel.setFont(new Font("Arial", 16));
        fontSizeTargets.add(commandLabel);

        updateScene(""); //method displays an image and whatever text is supplied
        updateItems(); //update items shows inventory and objects in rooms

        // adding the text area and submit button to a VBox
        VBox textEntry = new VBox();
        textEntry.setStyle("-fx-background-color: " + bgColor + ";");
        textEntry.setPadding(new Insets(20, 20, 20, 20));
        textEntry.getChildren().addAll(commandLabel, inputTextField);
        textEntry.setSpacing(10);
        textEntry.setAlignment(Pos.CENTER);
        gridPane.add(textEntry, 0, 2, 3, 1);

        // Render everything
        var scene = new Scene(gridPane, 1000, 800);
        scene.setFill(Color.BLACK);
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.stage.show();

    }

    /**
     * makeButtonAccessible
     * __________________________
     * For information about ARIA standards, see
     * https://www.w3.org/WAI/standards-guidelines/aria/
     *
     * @param inputButton the button to add screenreader hooks to
     * @param name        ARIA name
     * @param shortString ARIA accessible text
     * @param longString  ARIA accessible help text
     */
    public static void makeButtonAccessible(Button inputButton, String name, String shortString, String longString) {
        inputButton.setAccessibleRole(AccessibleRole.BUTTON);
        inputButton.setAccessibleRoleDescription(name);
        inputButton.setAccessibleText(shortString);
        inputButton.setAccessibleHelp(longString);
        inputButton.setFocusTraversable(true);
    }

    /**
     * customizeButton
     * __________________________
     *
     * @param inputButton the button to make stylish :)
     * @param w           width
     * @param h           height
     */
    private void customizeButton(Button inputButton, int w, int h) {
        inputButton.setPrefSize(w, h);
        inputButton.setFont(new Font("Arial", 16));
        inputButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
    }

    /**
     * addTextHandlingEvent
     * __________________________
     * Add an event handler to the inputTextField attribute
     * <p>
     * Your event handler should respond when users
     * hits the ENTER or TAB KEY. If the user hits
     * the ENTER Key, strip white space from the
     * input to inputTextField and pass the stripped
     * string to submitEvent for processing.
     * <p>
     * If the user hits the TAB key, move the focus
     * of the scene onto any other node in the scene
     * graph by invoking requestFocus method.
     */
    private void addTextHandlingEvent() {
        inputTextField.setPromptText("Please input here");
        inputTextField.setOnKeyPressed(a -> {
            if (a.getCode() == KeyCode.ENTER) {
                String trim = inputTextField.getText().trim(); // remove all of the space
                submitEvent(trim);
            }
            if (a.getCode() == KeyCode.TAB) {
                this.helpButton.requestFocus();
            }
        });
    }


    /**
     * submitEvent
     * __________________________
     *
     * @param text the command that needs to be processed
     */
    private void submitEvent(String text) {

        text = text.strip(); //get rid of white space
        stopArticulation(); //if speaking, stop

        if (text.equalsIgnoreCase("LOOK") || text.equalsIgnoreCase("L")) {
            String roomDesc = this.model.getPlayer().getCurrentRoom().getRoomDescription();
            String objectString = this.model.getPlayer().getCurrentRoom().getObjectString();
            if (!objectString.isEmpty()) roomDescLabel.setText(roomDesc + "\n\nObjects in this room:\n" + objectString);
            articulateRoomDescription(); //all we want, if we are looking, is to repeat description.
            return;
        } else if (text.equalsIgnoreCase("HELP") || text.equalsIgnoreCase("H")) {
            showInstructions();
            return;
        } else if (text.equalsIgnoreCase("COMMANDS") || text.equalsIgnoreCase("C")) {
            showCommands(); //this is new!  We did not have this command in A1
            return;
        }

        //try to move!
        String output = this.model.interpretAction(text); //process the command!
        // drop bird is done in code, only need to worry about the GUI
        if (output!=null&&output.equals("FORCED")) {
            encounter_force = true;
            System.out.println(encounter_force);
        }

        if (output == null || (!output.equals("GAME OVER") && !output.equals("FORCED") && !output.equals("HELP"))) {
            updateScene(output);
            updateItems();
        } else if (output.equals("GAME OVER")) {
            updateScene("");
            updateItems();
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(event -> {
                Platform.exit();
            });
            pause.play();
        } else if (output.equals("FORCED")) {
            forceHelper();
        }

    }

    /*
        check if a room contain any forced direction
     */
    private Boolean checkForced() {
        Player player = this.model.player;
        Room currentRoom = player.getCurrentRoom();
        List<Passage> passageTable = currentRoom.getMotionTable().passageTable;
        for (Passage passage : passageTable) {
            if (passage.getDirection().equals("FORCED")) {
                return true;
            }
        }

        return false;
    }

    /*
        check game over, check if the destination room is room 0
     */
    private Boolean checkZero() {
        Player player = this.model.player;
        Room currentRoom = player.getCurrentRoom();
        List<Passage> passageTable = currentRoom.getMotionTable().passageTable;
        for (Passage passage : passageTable) {
            if (passage.getDestinationRoom() == 0) {
                return true;
            }
        }

        return false;
    }


    private void forceHelper() {
        final Player player = this.model.player;
        final HashMap<Integer, Room> rooms = this.model.getRooms();
        // in room 6 right now, try to move to room 7
        updateScene("");
        updateItems();
        // update the items and scene of room 6
        // update the image of room 6
        PauseTransition pt = new PauseTransition(Duration.seconds(5));
        // pause for 5 seconds
        pt.setOnFinished(a -> {
            // after 5 seconds, move from 6 to the destination, by forced direction
            final PassageTable motionTable = player.getCurrentRoom().getMotionTable();
            final List<Passage> passageTable = motionTable.passageTable;
            for (Passage passage : passageTable) {
                for (AdventureObject obj : player.inventory) {
                    if (obj.getName().equals(passage.getKeyName())) {

                        // scene and items will be updated when forced is reached next time
                        final Boolean aBoolean = checkForced();
                        final Boolean des0 = checkZero();
                        // if the destination roomNumber is 0, stop the program
                        if (des0) {
                            updateScene("");
                            updateItems();
                            PauseTransition pause = new PauseTransition(Duration.seconds(10));
                            pause.setOnFinished(event -> {
                                Platform.exit();
                            });
                            pause.play();
                        }
                        if (aBoolean && (!des0)) {
                            final int destinationRoom = passage.getDestinationRoom();
                            if (passage.getDirection().equals("FORCED")) {
                                submitEvent("FORCED");
                            } else {
                                return;
                            }
                        }
                        return;
                    }
                }
            }
            for (Passage passage : passageTable) {
                if (passage.getKeyName() == null) {
                    final int destinationRoom = passage.getDestinationRoom();
                    final Boolean aBoolean = checkForced();
                    final Boolean des0 = checkZero();
                    if (des0) {
                        updateScene("");
                        updateItems();
                        PauseTransition pause = new PauseTransition(Duration.seconds(10));
                        pause.setOnFinished(event -> {
                            Platform.exit();
                        });
                        pause.play();
                    }
                    if (aBoolean && (!des0)) {
                        final int destinationRoom1 = passage.getDestinationRoom();
                        // the room is 9 right now, but the passage is still the passage of room 6
                        if (passage.getDirection().equals("FORCED")) {
                            submitEvent("FORCED");
                        } else {
                            return;
                        }
                    }

                }
            }
        });
        pt.play();
    }

    /**
     * showCommands
     * __________________________
     * <p>
     * update the text in the GUI (within roomDescLabel)
     * to show all the moves that are possible from the
     * current room.
     */
    private void showCommands() {
        StringBuilder builder = new StringBuilder();
        final HashMap<Integer, Room> roomsMap = this.model.getRooms();
        final Room currentRoom = this.model.getPlayer().getCurrentRoom();
        builder.append("the current room is: " + currentRoom.getRoomNumber() + " " + currentRoom.getRoomName());
        builder.append('\n');
        final PassageTable motionTable = currentRoom.getMotionTable();
        for (Passage passage : motionTable.passageTable) {
            builder.append(passage.getDirection());
            builder.append(" -> ");
            final Room room = roomsMap.get(passage.getDestinationRoom());
            builder.append(room.getRoomNumber() + " ");
            final String keyName = passage.getKeyName();
            if (keyName != null) {
                builder.append(keyName + "/");
            }

            builder.append(room.getRoomName());// get the destination room from the roomMap

            // add the name of the room to the string builder
            builder.append('\n'); // for each passage, start a new line
        }
        this.roomDescLabel.setText(builder.toString());
    }


    /**
     * updateScene
     * __________________________
     * <p>
     * Show the current room, and print some text below it.
     * If the input parameter is not null, it will be displayed
     * below the image.
     * Otherwise, the current room description will be dispplayed
     * below the image.
     *
     * @param textToDisplay the text to display below the image.
     */
    public void updateScene(String textToDisplay) {

        getRoomImage(); //get the image of the current room
        formatText(textToDisplay); //format the text to display
        roomDescLabel.setWrappingWidth(500);
        VBox roomPane = new VBox(roomImageView, roomDescLabel);
        roomPane.setPadding(new Insets(10));
        roomPane.setAlignment(Pos.TOP_CENTER);
        roomPane.setStyle("-fx-background-color: " + bgColor + ";");

        ScrollPane roomScrollPane = new ScrollPane(roomPane);
        roomScrollPane.setStyle("-fx-background-color: " + bgColor + ";");
        roomScrollPane.setFitToWidth(true);
        roomScrollPane.setFitToHeight(true);

        gridPane.add(roomScrollPane, 1, 1);
        stage.sizeToScene();

        //finally, articulate the description
        if (textToDisplay == null || textToDisplay.isBlank()) articulateRoomDescription();
    }

    /**
     * formatText
     * __________________________
     * <p>
     * Format text for display.
     *
     * @param textToDisplay the text to be formatted for display.
     */
    private void formatText(String textToDisplay) {
        if (textToDisplay == null || textToDisplay.isBlank()) {
            String roomDesc = this.model.getPlayer().getCurrentRoom().getRoomDescription() + "\n";
            String objectString = this.model.getPlayer().getCurrentRoom().getObjectString();
            if (objectString != null && !objectString.isEmpty())
                roomDescLabel.setText(roomDesc + "\n\nObjects in this room:\n" + objectString);
            else roomDescLabel.setText(roomDesc);
        } else roomDescLabel.setText(textToDisplay);
        roomDescLabel.setFill(Paint.valueOf("white"));
        roomDescLabel.setFill(Paint.valueOf("white"));
        roomDescLabel.setFont(new Font("Arial", 16 + fontSizeModifier));
        roomDescLabel.setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * getRoomImage
     * __________________________
     * <p>
     * Get the image for the current room and place
     * it in the roomImageView
     */
    private void getRoomImage() {

        int roomNumber = this.model.getPlayer().getCurrentRoom().getRoomNumber();
        String roomImage = this.model.getDirectoryName() + "/room-images/" + roomNumber + ".png";

        Image roomImageFile = new Image(roomImage);
        roomImageView = new ImageView(roomImageFile);
        roomImageView.setPreserveRatio(true);
        roomImageView.setFitWidth(400);
        roomImageView.setFitHeight(400);

        //set accessible text
        roomImageView.setAccessibleRole(AccessibleRole.IMAGE_VIEW);
        roomImageView.setAccessibleText(this.model.getPlayer().getCurrentRoom().getRoomDescription());
        roomImageView.setFocusTraversable(true);
    }

    /**
     * updateItems
     * __________________________
     * <p>
     * This method is partially completed, but you are asked to finish it off.
     * <p>
     * The method should populate the objectsInRoom and objectsInInventory Vboxes.
     * Each Vbox should contain a collection of nodes (Buttons, ImageViews, you can decide)
     * Each node represents a different object.
     * <p>
     * Images of each object are in the assets
     * folders of the given adventure game.
     */
    public void updateItems() {
        // if the player enters a new room a null will be returned by the interpertAction()
        // use those 2 list to store all of the possible button in those 2 vBox
        ArrayList<Button> buttonsInRoom = new ArrayList<>();
        ArrayList<Button> buttonsInInventory = new ArrayList<>();

        objectsInRoom.getChildren().clear(); // clear the room vbox first everytime the updateItem() is called
        objectsInInventory.getChildren().clear();
        // objInRoom is empty after take the object
        // if move a player to another room
        Player player = this.model.getPlayer();
        ArrayList<AdventureObject> inventory = player.inventory; // get the player's inventory
        final Room currentRoom = player.getCurrentRoom();
        ArrayList<AdventureObject> RoomObjs = currentRoom.objectsInRoom; // get the objects in the room

        // for player to drop object
        // the inventory
        for (AdventureObject obj : inventory) {
            String objName = obj.getName();
            String imagePath = this.model.getDirectoryName() + "/objectImages/" + objName + ".jpg";
            File file = new File(imagePath);
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setId(objName + "1");
            imageView.setFitWidth(100);
            Button button_inv = new Button(objName);
            button_inv.setId(objName);
            Button button_room = new Button(objName);
            button_room.setId(objName);

            // store those button2 to those 2 list
            if (!buttonsInRoom.contains(button_room)) {
                buttonsInRoom.add(button_room);
            }
            if (!buttonsInInventory.contains(button_inv)) {
                buttonsInInventory.add(button_inv);
            }

            makeButtonAccessible(button_inv, objName, obj.getName(), obj.getDescription());
            makeButtonAccessible(button_room, objName, obj.getName(), obj.getDescription());
            if (!checkExist(button_inv, this.objectsInInventory)) {
                this.objectsInInventory.getChildren().add(button_inv);
            }
            if (!checkExist(imageView, this.objectsInInventory)) {
                this.objectsInInventory.getChildren().add(imageView);
            }

            buttonHelper(button_room, button_inv, player, obj, imageView);


        }
        // for player to take object
        // the room objects
        for (AdventureObject obj : RoomObjs) {
            String objName = obj.getName();
            String imagePath = this.model.getDirectoryName() + "/objectImages/" + objName + ".jpg";
            File file = new File(imagePath);
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setId(objName + "1");
            imageView.setFitWidth(100);
            Button button_room = new Button(objName);
            button_room.setId(objName);
            Button button_inv = new Button(objName);
            button_inv.setId(objName);

            // store those button2 to those 2 list
            if (!buttonsInRoom.contains(button_room)) {
                buttonsInRoom.add(button_room);
            }
            if (!buttonsInInventory.contains(button_inv)) {
                buttonsInInventory.add(button_inv);
            }

            makeButtonAccessible(button_room, objName, obj.getName(), obj.getDescription());
            makeButtonAccessible(button_inv, objName, obj.getName(), obj.getDescription());
            if (!checkExist(button_room, this.objectsInRoom)) {
                this.objectsInRoom.getChildren().add(button_room);
            }
            if (!checkExist(imageView, this.objectsInRoom)) {
                this.objectsInRoom.getChildren().add(imageView);
            }

            buttonHelper(button_room, button_inv, player, obj, imageView);

        }
        //write some code here to add images of objects in a given room to the objectsInRoom Vbox
        //write some code here to add images of objects in a player's inventory room to the objectsInInventory Vbox
        //please use setAccessibleText to add "alt" descriptions to your images!
        //the path to the image of any is as follows:
        //this.model.getDirectoryName() + "/objectImages/" + objectName + ".jpg";

        ScrollPane scO = new ScrollPane(this.objectsInRoom);
        scO.setPadding(new Insets(10));
        scO.setStyle("-fx-background: " + bgColor + "; -fx-background-color:transparent;");
        scO.setFitToWidth(true);
        gridPane.add(scO, 0, 1);

        ScrollPane scI = new ScrollPane(objectsInInventory);
        scI.setFitToWidth(true);
        scI.setStyle("-fx-background: " + bgColor + "; -fx-background-color:transparent;");
        gridPane.add(scI, 2, 1);


    }

    /*
     The helper function for setting buttons
     */
    private void buttonHelper(Button button_room, Button button_inv, Player player, AdventureObject obj, ImageView imageView) {
        // drop object button
        button_inv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                player.dropObject(obj.getName());
                // drop from the inventory to the room

                objectsInInventory.getChildren().remove(imageView);
                objectsInInventory.getChildren().remove(button_inv);

                objectsInRoom.getChildren().add(button_room);
                objectsInRoom.getChildren().add(imageView);
                String text = "YOU HAVE DROPPED:\n " + obj.getName().toUpperCase();
                roomDescLabel.setText(text);
                return;
            }


        });
        // take object button
        button_room.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                player.takeObject(obj.getName());
                // take object from room to the inventory
                objectsInRoom.getChildren().remove(button_room);
                objectsInRoom.getChildren().remove(imageView);


                objectsInInventory.getChildren().add(button_inv);
                objectsInInventory.getChildren().add(imageView);

                String text = "YOU HAVE TAKEN:\n " + obj.getName().toUpperCase();

                roomDescLabel.setText(text);
                return;
            }
        });

    }

    private Boolean checkExist(Node node, VBox vBox) {
        final ObservableList<Node> children = vBox.getChildren();
        for (Node child : children) {
            if (child.getId().equals(node.getId())) {
                // the child exist in the vbox
                return true;
            }
        }
        return false;
    }

    /*
     * Show the game instructions.
     *
     * If helpToggle is FALSE:
     * -- display the help text in the CENTRE of the gridPane (i.e. within cell 1,1)
     * -- use whatever GUI elements to get the job done!
     * -- set the helpToggle to TRUE
     * -- REMOVE whatever nodes are within the cell beforehand!
     *
     * If helpToggle is TRUE:
     * -- redraw the room image in the CENTRE of the gridPane (i.e. within cell 1,1)
     * -- set the helpToggle to FALSE
     * -- Again, REMOVE whatever nodes are within the cell beforehand!
     */
    public void showInstructions() {
        ObservableList<Node> children = this.gridPane.getChildren();
        if (this.helpToggle) {
            Node temp = null;
            for (Node child : children) {
                if (GridPane.getColumnIndex(child) == 1 && GridPane.getRowIndex(child) == 1) {
                    temp = child;
                    break;
                }
            }
            children.remove(temp);
            // if it is true

            VBox roomPane = new VBox(this.roomImageView, this.roomDescLabel);
            roomPane.setPadding(new Insets(10));
            // set a padding of 10 pixels around the vbox
            roomPane.setAlignment(Pos.TOP_CENTER); //
            roomPane.setStyle("-fx-background-color: " + bgColor); //set the backgroud color of the VBox
            this.gridPane.add(roomPane, 1, 1); // add the box to the cell (1,1)
            this.helpToggle = false;
        } else {
            // get the helpText of the game
            Node temp = null;
            for (Node child : children) {
                if (GridPane.getColumnIndex(child) == 1 && GridPane.getRowIndex(child) == 1) {
                    temp = child;
                    break;
                }
            }
            children.remove(temp);

            Text text = new Text(this.model.getInstructions());
            text.setStyle("-fx-fill: #FFFFFF"); // set the color of the text to white
            text.setWrappingWidth(500);// set the length of each line
            // of word
            VBox box = new VBox(text);
            box.setPadding(new Insets(10));
            box.setAlignment(Pos.TOP_LEFT);
            box.setStyle("-fx-background-color: " + bgColor + ";");

            this.gridPane.add(box, 1, 1);
            this.helpToggle = true;
        }
    }

    /**
     * This method handles the event related to the
     * options button.
     */
    public void addOptionsEvent() {
        optionsButton.setOnAction(e -> {
            stopArticulation();
            OptionsView optionsView = new OptionsView(this);
        });
    }


    /**
     * This method handles the event related to the
     * help button.
     */
    public void addInstructionEvent() {
        helpButton.setOnAction(e -> {
            stopArticulation(); //if speaking, stop
            showInstructions();
        });
    }

    public void addMusicEvent(){
        musicButton.setOnAction(e-> {
            gridPane.requestFocus();
            MusicPlayer player = new MusicPlayer(this);
        }); }

    /*
     * This method handles the event related to the
     * Time button.
     */
    public void addTimeEvent() {
        ObservableList<Node> children = this.gridPane.getChildren();
        timeButton.setOnAction(e -> {
            if (this.timeToggle) {
                Node temp = null;
                for (Node child : children) {
                    if (GridPane.getColumnIndex(child) == 1 && GridPane.getRowIndex(child) == 1) {
                        temp = child;
                        break;
                    }
                }
                children.remove(temp);
                // if it is true
                VBox roomPane = new VBox(this.roomImageView, this.roomDescLabel);
                roomPane.setPadding(new Insets(10));
                // set a padding of 10 pixels around the vbox
                roomPane.setAlignment(Pos.TOP_CENTER); //
                roomPane.setStyle("-fx-background-color: " + bgColor); //set the backgroud color of the VBox
                this.gridPane.add(roomPane, 1, 1); // add the box to the cell (1,1)
                this.timeToggle = false;
            } else {
                Clock instanceClock = Clock.getInstanceClock();
                String timeStr = instanceClock.CurrentTime();
                Text text = new Text(timeStr);

                text.setFont(new Font(90));
                text.setStyle("-fx-fill: #FFFFFF"); // set the color of the text to white
                text.setWrappingWidth(500);// set the length of each line
                // of word
                VBox box = new VBox(text);
                box.setPadding(new Insets(10));
                box.setAlignment(Pos.TOP_LEFT);
                box.setStyle("-fx-background-color: " + bgColor + ";");

                this.gridPane.add(box, 1, 1);
                this.timeToggle = true;

            }
        });


    }

    private void addUndoEvent() {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        UndoButton.setOnAction(e -> {
            Room previousRoom = this.model.getPreviousRoom();
            String text = roomDescLabel.getText();
            if (previousRoom == null) {

                roomDescLabel.setText("Since you have not moved, so you cannot go back to the previous room");
                pause.setOnFinished(a -> {
                    roomDescLabel.setText(text);
                });

                pause.play();
            } else if (encounter_force) {

                roomDescLabel.setText("Since you have encountered force, so you cannot go back to the previous room");
                pause.setOnFinished(s -> {
                    roomDescLabel.setText(text);
                });
                pause.play();
            } else {
                this.model.player.setCurrentRoom(previousRoom);
                updateItems();
                updateScene("");
                showCommands();
                topButtons.getChildren().remove(UndoButton);
            }


        });
    }


    private void addWeatherEvent() {
        Weather weather = new Weather();
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        WeatherButton.setOnAction(e -> {
            String weatherMessage = weather.today_weather();
            String oldText = roomDescLabel.getText();

            roomDescLabel.setText(weatherMessage);

            pause.setOnFinished(s -> {
                roomDescLabel.setText(oldText);
            });
            pause.play();
        });
    }


    /**
     * This method articulates Room Descriptions
     */
    public void articulateRoomDescription() {
        String musicFile;
        String adventureName = this.model.getDirectoryName();
        String roomName = this.model.getPlayer().getCurrentRoom().getRoomName();

        if (!this.model.getPlayer().getCurrentRoom().getVisited())
            musicFile = "./" + adventureName + "/sounds/" + roomName.toLowerCase() + "-long.mp3";
        else musicFile = "./" + adventureName + "/sounds/" + roomName.toLowerCase() + "-short.mp3";
        musicFile = musicFile.replace(" ", "-");

        Media sound = new Media(new File(musicFile).toURI().toString());

        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        mediaPlaying = true;

    }

    /**
     * This method stops articulations
     * (useful when transitioning to a new room or loading a new game)
     */
    public void stopArticulation() {
        if (mediaPlaying) {
            mediaPlayer.stop(); //shush!
            mediaPlaying = false;
        }
    }

    /**
     * Sets the background color of the game
     *
     * @param color Hex code of background
     */
    public void setBackgroundColor(String color) {
        bgColor = color;

        // Force components to update
        stopArticulation(); // Stop sound as we redraw the scene
        inputTextField.getParent().setStyle("-fx-background-color: " + bgColor + ";");
        gridPane.setBackground(new Background(new BackgroundFill(
                Color.valueOf(bgColor),
                new CornerRadii(0),
                new Insets(0)
        )));
        updateItems();
        updateScene(null);
    }


    public void setFontSize(int oldSize, int newSize)
    {
        fontSizeModifier = newSize - OptionsView.DEFAULT_FONT_SIZE;
        int deltaSize = newSize - oldSize;

        // Modify font for all targets
        for(Labeled node : fontSizeTargets)
        {
            node.setFont(new Font(node.getFont().getName(), node.getFont().getSize() + deltaSize));
        }

        roomDescLabel.setFont(new Font(roomDescLabel.getFont().getName(), roomDescLabel.getFont().getSize() + deltaSize));
    }
}
