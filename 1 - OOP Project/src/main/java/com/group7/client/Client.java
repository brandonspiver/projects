package com.group7.client;

import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Client extends Application {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private TextArea clientLog;

    @FXML
    private Button connectServer;

    @FXML
    private Button endCall;

    @FXML
    private Label hostname;

    @FXML
    private Button makeCall;

    @FXML
    private Button message;

    @FXML
    private TextArea onlineUsers;

    @FXML
    private TextField serverAddr;

    // @FXML
    // private GridPane callersGridPane;

    @FXML
    private TextField serverPort;

    @FXML
    private TextField toSend;

    @FXML
    private TextField toUser;

    @FXML
    private Label username;

    @FXML
    private TextField usernameInput;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private VBox userVbox;

    @FXML
    private ListView<HBox> onlineUsersListView = new ListView<HBox>();

    @FXML
    private static Button outgoingCallEndButton;

    @FXML
    private Button acceptButton;

    @FXML
    private Button declineButton;

    @FXML
    private Button hangUpButton;

    @FXML
    private Button muteButton;

    @FXML
    private Button callButton;

    @FXML
    private AnchorPane gridAnchorPane;

    @FXML
    private ListView<?> chatListView;

    @FXML
    private Button addParticipantButton;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextField inputText;

    private Stage stage;

    private static List<User> users = new ArrayList<>();
    private static List<User> usersOnCall = new ArrayList<>();

    Font fontAwesome = Font.loadFont("voip/fontawesomefx-8.1.jar", 30);
    private static Socket socket;
    private static BufferedWriter bw;
    private volatile BufferedReader in;
    private String serverAddress; // TCP
    private int serverPortNr; // TCP

    private static String myUsername;
    private String avatarPath;
    private static int portCalling;
    private static int portFileTransfer;
    private volatile String request;
    private volatile ArrayList<String> clientsOnGUI = new ArrayList<>();
    private volatile static VoIP voip;
    private static Client client;
    private static String usernameToRemove;
    private volatile String messageSender;
    private static boolean endOutgoingCall = false;
    private static Parent outgoingCallRoot;

    private static String messageReciever;

    // variables for incoming call
    private static String incomingCaller;
    private static String incomingCallerIPAddress;
    private static int incomingCallerPort;
    private static int incomingCallID;

    // grid TESTING
    @FXML
    private Button gridButton;

    /**
     * This method is called to start the GUI application.
     * 
     * @param primaryStage The primary stage for the GUI application.
     * 
     * @throws Exception if an error occurs during the initialization of the GUI.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        this.stage = primaryStage;
        this.stage.setOnCloseRequest(event -> {
            // Code to handle window close event
            System.out.println("GUI is closing");
            System.exit(0);
        });
        showSignIn();
    }

    /**
     * This method is called to show the sign in screen for the user.
     * 
     * @throws IOException if an error occurs while loading the FXML file.
     */
    private void showSignIn() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sign_in.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Find the username and address text fields
        TextField usernameField = (TextField) root.lookup("#usernameField");
        TextField addressField = (TextField) root.lookup("#addressField");
        TextField portNumberField = (TextField) root.lookup("#portField");
        TextField callNumberField = (TextField) root.lookup("#callNumberField");

        ComboBox<String> avatarComboBox = (ComboBox) root.lookup("#avatarComboBox");

        // get a reference to the folder containing your images
        File avatarFolderDir = new File(
                "src/main/resources/com/group7/assets/user_avatars");

        // get a list of all the image files in the folder
        File avatarFilesList[] = avatarFolderDir.listFiles();
        // iterate over the image files and add an option for each one to the ComboBox
        for (File avatarFile : avatarFilesList) {
            String avatarName = avatarFile.getName();
            Image avatar = new Image(avatarFile.toURI().toString());
            ImageView imageView = new ImageView(avatar);
            imageView.setFitHeight(100); // set the size of the image in the ComboBox
            imageView.setFitWidth(100);

            avatarComboBox.getItems().add(avatarName);
            avatarComboBox.setCellFactory(listView -> new ListCell<String>() {
                private final ImageView imageView = new ImageView();

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        imageView.setImage(new Image(new File(avatarFolderDir + "/" + item).toURI().toString()));
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                        setText(item);
                        setGraphic(imageView);
                    }
                }
            });
        }

        // Find the sign in button and attach an event handler to it
        Button signInButton = (Button) root.lookup("#signInButton");
        signInButton.setOnAction(event -> {
            try {

                myUsername = usernameField.getText();
                if (myUsername.isBlank()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Username cannot be blank",
                            ButtonType.OK);
                    alert.showAndWait();
                    return;
                }

                serverAddress = addressField.getText();
                try {
                    serverPortNr = Integer.parseInt(portNumberField.getText());
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Server Port must be a valid number",
                            ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
                try {
                    portCalling = Integer.parseInt(callNumberField.getText());

                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Calling Port must be a valid number",
                            ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
                portFileTransfer = portCalling + 1;
                String selectedAvatar;
                if (avatarComboBox.getSelectionModel().getSelectedIndex() == -1) {
                    selectedAvatar = avatarComboBox.getItems().get(0);
                } else {
                    selectedAvatar = avatarComboBox.getSelectionModel().getSelectedItem();
                }
                avatarPath = avatarFolderDir.getAbsolutePath() + "/" + selectedAvatar;
                if (socket == null) {
                    socket = new Socket(serverAddress, serverPortNr);
                    bw = new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream()));
                    // generatePorts(); // generating ports for Calling and file transfer
                    messagesFromServer();
                    registerToServer(selectedAvatar);
                    addressField.setDisable(true);
                    portNumberField.setDisable(true);
                } else {
                    registerToServer(selectedAvatar);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                socket = null;
                Alert alert = new Alert(Alert.AlertType.ERROR, "Server not online or incorrect server credentials",
                        ButtonType.OK);
                alert.showAndWait();
            }

        });

        stage.setScene(scene);
        stage.show();
    }

    /**
     * 
     * This method is called to show the client GUI once the user is signed in.
     * 
     * @throws IOException if an error occurs while loading the FXML file or setting
     *                     up the UI components.
     */
    private void showClientGui() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("clientGUI.fxml"));
        Parent root = loader.load();

        client = loader.getController();

        client.usernameLabel.setText(myUsername);
        client.usernameLabel.getStyleClass().add("usernameLabel");
        client.callButton.setDisable(true);
        client.muteButton.setDisable(true);
        client.hangUpButton.setDisable(true);
        client.addParticipantButton.setDisable(true);

        // Set the image of the current user avatar ImageView
        File avatarFile = new File(avatarPath);
        Image avatarImage = new Image(avatarFile.toURI().toString());
        client.avatarImageView.setImage(avatarImage);

        client.onlineUsersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        client.onlineUsersListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    // check if at least one item is selected
                    boolean hasSelection = (newValue != null);

                    // set the disable property of the button accordingly
                    client.callButton.setDisable(!hasSelection);
                });

        Scene scene = new Scene(root);
        scene.getStylesheets().add("voip/src/main/resources/com/group7/assets/styles.css");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method is called to update the client GUI with the current list of
     * online users.
     */
    private void updateClientGUI() {

        // display online users in panels
        for (User user : users) {
            if (!clientsOnGUI.contains(user.getUsername())) {
                displayOnlineUser(user);
                clientsOnGUI.add(user.getUsername());
            }
        }
    }

    /**
     * Displays an online user in the GUI by creating an HBox panel containing the
     * user's avatar image and username label
     * 
     * @param user the User object representing the online user to be displayed
     */
    private void displayOnlineUser(User user) {
        ImageView onlineUserAvatarImageView = new ImageView(
                new Image(new File(user.getAvatarURL()).toURI().toString()));
        onlineUserAvatarImageView.setFitWidth(60);
        onlineUserAvatarImageView.setFitHeight(60);
        Pane imagePane = new Pane(onlineUserAvatarImageView);
        imagePane.setPadding(new Insets(0, 0, 0, 40));

        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.getStyleClass().add("usernameLabel");
        usernameLabel.setAlignment(Pos.CENTER);
        HBox.setHgrow(usernameLabel, Priority.ALWAYS);
        // usernameLabel.setMaxWidth(Double.MAX_VALUE);

        HBox userPanel = new HBox();
        userPanel.setPrefHeight(80);
        userPanel.setAlignment(Pos.CENTER_LEFT);
        userPanel.setPadding(new Insets(10));
        userPanel.setSpacing(10);
        userPanel.setStyle("-fx-border-color: white; -fx-border-width: 2px;");
        userPanel.getChildren().addAll(imagePane, usernameLabel);
        userPanel.setStyle("-fx-background-color: #220059; ");
        client.onlineUsersListView.getItems().add(userPanel);

    }

    /**
     * Removes a user from the client GUI's online user list view.
     */
    private void removeUserClientGUI() {
        System.out.println("Removing user from GUI: " + usernameToRemove);
        ObservableList<HBox> items = client.onlineUsersListView.getItems();
        for (HBox item : items) {
            HBox userPanel = (HBox) item;
            Label usernameLabel = (Label) userPanel.getChildren().get(1);
            if (usernameLabel.getText().equals(usernameToRemove)) {
                items.remove(item);

                break;
            }
        }
    }

    @FXML
    void sendMessage(ActionEvent event) {
        ObservableList<HBox> selectedHboxUsers = onlineUsersListView.getSelectionModel().getSelectedItems();
        System.out.println("SIZE IF SELECTED!!!  " + selectedHboxUsers.size());
        if (selectedHboxUsers.size() < 1) {
            // broadcast
            messageReciever = "BROADCAST";
            System.out.println("SIZE IF SELECTED!!! BROADCAST  " + selectedHboxUsers.size());

        } else {
            selectedHboxUsers.forEach(hbox -> {
                String username = null;
                // iterate through each child of the HBox until the first Label is found
                for (Node node : hbox.getChildren()) {
                    // check if the child is a label
                    if (node instanceof Label) {
                        // do something with the label
                        usernameLabel = (Label) node;
                        username = usernameLabel.getText();
                        break; // exit the loop after the first Label is found
                    }
                }
                if (usernameLabel != null && username != null) {
                    messageReciever = username;
                } else {
                    System.out.println("MESSAGER REICVER BLANK");
                }
            });

        }

        // String message;
        if (messageReciever.equals("BROADCAST")) {

            Platform.runLater(() -> {
                String message = "BROADCAST_MESSAGE|" + myUsername + "|" + client.inputText.getText();

                client.chatTextArea.appendText("You to everyone: " + client.inputText.getText() + "\n");
                System.out.println("You to everyone: " + client.inputText.getText());
                inputText.setText("");
                sendToServer(message);
                onlineUsersListView.getSelectionModel().clearSelection();

            });

        } else {
            Platform.runLater(() -> {
                String message = "DIRECT_MESSAGE|" + myUsername + "|" + messageReciever + "|"
                        + client.inputText.getText();

                client.chatTextArea
                        .appendText("You to " + messageReciever + ": " + client.inputText.getText() + "\n");
                System.out.println("You to " + messageReciever + ": " + message + client.inputText.getText());
                inputText.setText("");
                sendToServer(message);
                onlineUsersListView.getSelectionModel().clearSelection();

            });
        }
    }

    /**
     * Starts a call with the selected users from the online users list view. Gets
     * the selected users
     * 
     * @param event The event that triggers the method, in this case a button click.
     * @throws IOException If there is an I/O error while displaying the outgoing
     *                     call screen.
     */
    @FXML
    void startCall(ActionEvent event) throws IOException {

        ObservableList<HBox> selectedHboxUsers = onlineUsersListView.getSelectionModel().getSelectedItems();
        List<User> selectedUsers = new ArrayList<>();
        // iterate through each HBox in the ObservableList

        if (!selectedHboxUsers.isEmpty()) {
            selectedHboxUsers.forEach(hbox -> {
                String username = null;
                String relativePath = null;
                // iterate through each child of the HBox
                for (Node node : hbox.getChildren()) {
                    // check if the child is a label
                    if (node instanceof Label) {
                        // do something with the label
                        usernameLabel = (Label) node;
                        username = usernameLabel.getText();
                    } else if (node instanceof Pane) {
                        // iterate through each child of the Pane
                        Pane imagePane = (Pane) node;
                        for (Node childNode : imagePane.getChildren()) {
                            if (childNode instanceof ImageView) {
                                // do something with the ImageView
                                ImageView imageView = (ImageView) childNode;
                                Image image = imageView.getImage();
                                String imageUrl = image.getUrl();
                                File file = new File(imageUrl);
                                String fileRelativePath = file.getPath();
                                int startIndex = fileRelativePath.indexOf("src/");
                                relativePath = fileRelativePath.substring(startIndex);
                            }
                        }
                    }
                }
                if (usernameLabel != null && relativePath != null) {
                    selectedUsers.add(new User(username, relativePath));

                }

                // selectedUsers.add(new User(username, relativePath));
            });

            String selectedUsersString = "";
            for (User user : selectedUsers) {
                selectedUsersString = selectedUsersString + user.getUsername() + "#";
            }
            selectedUsersString = selectedUsersString.substring(0, selectedUsersString.length() - 1);

            endOutgoingCall = false;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    Platform.runLater(() -> {
                        showOutgoingCall(selectedUsers);
                    });
                }
            }).start();

            sendCallRequest(selectedUsersString);
            mainTabPane.getSelectionModel().select(1);
        }

    }

    /**
     * Displays the outgoing call window with the selected users
     * 
     * @param selectedUsers a List of User objects representing the selected users
     *                      to call
     */
    @FXML
    private void showOutgoingCall(List<User> selectedUsers) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("outgoingCall.fxml"));
            outgoingCallRoot = fxmlLoader.load();
            Scene outgoingCallScene = new Scene(outgoingCallRoot);
            Stage outgoingCallStage = new Stage();

            HBox selectedUsersPanel = (HBox) outgoingCallRoot.lookup("#selectedUsersPanel");
            selectedUsersPanel.setSpacing(10);
            selectedUsersPanel.setStyle("-fx-border-color: white; -fx-border-width: 2px;");
            selectedUsersPanel.setAlignment(Pos.CENTER);
            for (User user : selectedUsers) {
                System.out.println(user.getUsername() + " " + user.getAvatarURL());
                Label callingusernameLabel = new Label();
                callingusernameLabel.setText(user.getUsername());
                ImageView callingUserAvatar = new ImageView(
                        new Image(new File(user.getAvatarURL()).toURI().toString()));
                callingUserAvatar.setFitWidth(60);
                callingUserAvatar.setFitHeight(60);
                selectedUsersPanel.getChildren().addAll(callingUserAvatar, callingusernameLabel);

            }
            selectedUsersPanel.setStyle("-fx-background-color: #220059; ");
            Platform.runLater(() -> {
                muteButton.setDisable(false);
                hangUpButton.setDisable(false);
                addParticipantButton.setDisable(false);
            });

            outgoingCallStage.setScene(outgoingCallScene);
            outgoingCallStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static GridPane callersGridPane = new GridPane();

    @FXML
    private void showPartipantsOnCall(List<User> callParticipants) throws IOException {
        client.gridAnchorPane.getChildren().remove(callersGridPane);
        callersGridPane.getChildren().clear();
        // client.gridAnchorPane.getChildren().add(callersGridPane);
        // client.gridAnchorPane.getChildren().clear();

        callersGridPane.setGridLinesVisible(true);
        callersGridPane.setAlignment(Pos.CENTER);
        int anchorWidth = (int) client.gridAnchorPane.getWidth();
        int anchorHeight = (int) client.gridAnchorPane.getHeight();

        int cellWidth = 0;
        int cellHeight = 0;

        if (callParticipants.size() < 7) {
            cellWidth = anchorWidth / 2;
            cellHeight = anchorHeight / 3;

            // Loop over the list of users and create a VBox for each one
            for (int i = 0; i < callParticipants.size(); i++) {
                User user = callParticipants.get(i);

                // Create an ImageView for the user's avatar
                ImageView callerUserAvatar = new ImageView(
                        new Image(new File(user.getAvatarURL()).toURI().toString()));
                callerUserAvatar.setFitWidth(100);
                callerUserAvatar.setFitHeight(100);

                // Create a Label for the user's username
                Label usernameLabel = new Label(user.getUsername());
                usernameLabel.setAlignment(Pos.CENTER);

                // Create a VBox for the avatar and username
                VBox userBox = new VBox();
                userBox.getChildren().addAll(callerUserAvatar, usernameLabel);
                userBox.setAlignment(Pos.CENTER);

                // Set the preferred size of the VBox to 200x200 pixels
                userBox.setPrefSize(cellWidth, cellHeight);

                callersGridPane.add(userBox, i % 2, i / 2);
            }

        } else {
            cellWidth = anchorWidth / 3;
            cellHeight = anchorHeight / 3;
            // Loop over the list of users and create a VBox for each one
            for (int i = 0; i < callParticipants.size(); i++) {
                User user = callParticipants.get(i);

                // Create an ImageView for the user's avatar
                ImageView callerUserAvatar = new ImageView(
                        new Image(new File(user.getAvatarURL()).toURI().toString()));
                callerUserAvatar.setFitWidth(100);
                callerUserAvatar.setFitHeight(100);

                // Create a Label for the user's username
                Label usernameLabel = new Label(user.getUsername());
                usernameLabel.setAlignment(Pos.CENTER);

                // Create a VBox for the avatar and username
                VBox userBox = new VBox();
                userBox.getChildren().addAll(callerUserAvatar, usernameLabel);
                userBox.setAlignment(Pos.CENTER);

                // Set the preferred size of the VBox to 200x200 pixels
                userBox.setPrefSize(cellWidth, cellHeight);

                callersGridPane.add(userBox, i % 3, i / 3);
            }

        }
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);
        callersGridPane.getColumnConstraints().addAll(col1, col2);

        // Set the row constraints to evenly distribute the available space
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        row1.setFillHeight(true);
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS);
        row2.setFillHeight(true);
        callersGridPane.getRowConstraints().addAll(row1, row2);

        client.gridAnchorPane.getChildren().add(callersGridPane);
    }

    /**
     * This method is called when the "End Call" button is clicked in the outgoing
     * call window.
     * 
     * @param event The event object that triggered the method call.
     */
    @FXML
    void endOutgoingCall(ActionEvent event) {
        endOutgoingCall = true;
        Stage stage = (Stage) outgoingCallRoot.getScene().getWindow();
        // Stage stage = (Stage) outgoingCallEndButton.getScene().getWindow();

        Platform.runLater(() -> {
            client.muteButton.setDisable(true);
            client.hangUpButton.setDisable(true);
            client.addParticipantButton.setDisable(true);
        });

        stage.close();

    }

    /**
     * This method is called when the user accepts an incoming call.
     * 
     * @param event the action event generated by the user clicking the "Accept"
     *              button
     */
    @FXML
    void acceptCall(ActionEvent event) {
        voip = new VoIP(portCalling);
        voip.addClient(incomingCaller, incomingCallerIPAddress, incomingCallerPort);
        voip.setCallID(incomingCallID);
        voip.receiveVoIP();
        voip.sendVoIP();
        sendToServer("CALL_RESPONSE|" + myUsername + "|" + incomingCaller + "|ACCEPT|" + incomingCallID);
        Stage stage = (Stage) acceptButton.getScene().getWindow();
        stage.close();

        // find user in users using calle string
        for (User user : users) {
            if (user.getUsername().equals(incomingCaller)) {
                usersOnCall.add(new User(user.getUsername(), user.getAvatarURL()));
                break;

            }

        }
        Platform.runLater(() -> {
            try {
                showPartipantsOnCall(usersOnCall);
                client.muteButton.setDisable(false);
                client.hangUpButton.setDisable(false);
                client.addParticipantButton.setDisable(false);
                client.mainTabPane.getSelectionModel().select(1);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

    }

    /**
     * Handles the event when the user clicks on the "Decline" button during an
     * incoming call.
     * 
     * @param event the ActionEvent object generated when the button is clicked
     */
    @FXML
    void declineCall(ActionEvent event) {
        sendToServer("CALL_RESPONSE|" + myUsername + "|" + incomingCaller + "|DECLINE|" + incomingCallID);
        Stage stage = (Stage) declineButton.getScene().getWindow();
        stage.close();
    }

    /**
     * 
     * Shows a GUI for an incoming call from a list of users calling.
     * 
     * The GUI displays the avatar and username of the incoming callers in an HBox.
     * 
     * @param usersCalling a list of users who are calling
     */
    private void showIncomingCallGUI(ArrayList<User> usersCalling) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("incomingCall.fxml"));
            Parent incomingCallRoot = fxmlLoader.load();
            Scene incomingCallScene = new Scene(incomingCallRoot);
            Stage incomingCallStage = new Stage();

            HBox incomingCallHbox = (HBox) incomingCallRoot.lookup("#incomingCallHbox");
            incomingCallHbox.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-background-color: #220059;");
            incomingCallHbox.setAlignment(Pos.CENTER);
            for (User user : usersCalling) {
                Label incomingCallerLabel = new Label();
                incomingCallerLabel.setText(user.getUsername());
                ImageView incomingCallerAvatar = new ImageView(
                        new Image(new File(user.getAvatarURL()).toURI().toString()));
                System.out.println(user.getAvatarURL());
                incomingCallerAvatar.setFitWidth(50);
                incomingCallerAvatar.setFitHeight(50);
                incomingCallHbox.getChildren().addAll(incomingCallerAvatar, incomingCallerLabel);
            }

            incomingCallStage.setScene(incomingCallScene);
            incomingCallStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method handles the hang-up call functionality by stopping the VoIP and
     * preparing it for the next call.
     * 
     * @param event The action event triggered by clicking the "Hang Up" button.
     */
    @FXML
    void hangUpCall(ActionEvent event) {
        // Stopping voip and preparing it for the next for the next call;
        int callID = voip.getCallID();
        voip.stop();
        voip = null;

        sendToServer("END_CALL|" + myUsername + "|" + callID);
        Platform.runLater(() -> {
            client.gridAnchorPane.getChildren().remove(callersGridPane);
            callersGridPane.getChildren().clear();

            client.muteButton.setDisable(true);
            client.hangUpButton.setDisable(true);
            client.addParticipantButton.setDisable(true);
        });
    }

    /**
     * 
     * This method handles the mute microphone functionality. If the user is already
     * on a VoIP call, it mutes the microphone.
     * 
     * @param event The action event triggered by clicking the "Mute" button.
     */
    @FXML
    void muteMic(ActionEvent event) {
        System.out.println("Mute button pressed");
        try {
            voip.mute();
        } catch (Exception e) {
            System.out.println("Cannot mute!, not on voip yet");
        }
    }

    @FXML
    void testGrid(ActionEvent event) {
        // fake fake
        List<User> participantsList = new ArrayList<>();

        // fake users
        participantsList.add(new User("ash", "src/main/resources/com/group7/assets/user_avatars/camera.png"));
        participantsList.add(new User("misty",
                "src/main/resources/com/group7/assets/user_avatars/cupcake.png"));
        participantsList.add(new User("brock",
                "src/main/resources/com/group7/assets/user_avatars/pineapple.png"));
        participantsList.add(new User("ash",
                "src/main/resources/com/group7/assets/user_avatars/camera.png"));
        // participantsList.add(new User("misty",
        // "src/main/resources/com/group7/assets/user_avatars/cupcake.png"));
        // participantsList.add(new User("brock",
        // "src/main/resources/com/group7/assets/user_avatars/pineapple.png"));
        // participantsList.add(new User("ash",
        // "src/main/resources/com/group7/assets/user_avatars/camera.png"));
        // participantsList.add(new User("misty",
        // "src/main/resources/com/group7/assets/user_avatars/cupcake.png"));
        // participantsList.add(new User("brock",
        // "src/main/resources/com/group7/assets/user_avatars/pineapple.png"));

        try {
            showPartipantsOnCall(participantsList);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @FXML
    void addParticipant(ActionEvent event) {
        // new Thread(new Runnable() {

        // @Override
        // public void run() {
        // Platform.runLater(() -> {
        // startCall();
        // });
        // }
        // }).start();

        // addParticipantToCall();

    }

    /**
     * This is the entry point of the application. It launches the JavaFX
     * application.
     * 
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch();
    }

    // Start adding Client code. Do not modify above code

    /**
     * TODO:
     * 
     * 1. Create TCP functions to communicate between clients/ groups.
     * 2. Set username and get notified on online/ joined users
     * 3. Allow users to create/ join groups
     * 4. Have error handling enabled as constant messages stored in a hashmap to
     * make it easier
     * 5. Enable calls and connection/ disconnection between user/s (caller details
     * should be sent to reciever)
     * 6. Have peer to peer voice note sending over TCP using the server to
     * acknowledge and accept decline file.
     * 7. Allow peer to peer sending of voice notes between client/s
     */

    /**
     * Sends a registration message to the server with the user's information.
     *
     * @param avatar The avatar of the user.
     */
    private void registerToServer(String avatar) {
        try {
            // Add code to get username from GUI
            String msg = "REGISTER|" + myUsername +
                    "|" + portCalling + "|" + portFileTransfer + "|" + avatar;

            sendToServer(msg);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }

    }

    /**
     * Sends a call request to the server, including the username of the
     * caller and the callees.
     *
     * @param callees The callees who should be invited to the call.
     */
    private void sendCallRequest(String callees) {
        sendToServer("CALL_REQUEST|" + myUsername + "|" + callees);
    }

    /**
     * Sends a message to the server to add a participant to an ongoing call.
     *
     * @param callee The username of the callee to be added.
     */
    private void addParticipantToCall(String callee) {
        sendToServer("ADD_PARTICIPANT|" + myUsername + "|" + callee + "|" + voip.getCallID());
    }

    /**
     * Sends a message to the server with the provided message.
     *
     * @param message The message to be sent to the server.
     */
    private void sendToServer(String message) {

        System.out.println("Sending to server: " + message);
        try {
            bw.write(message);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Listens to the input stream from the server and processes each incoming
     * message using the processServerRequest() method.
     * Runs on a separate thread to prevent blocking the main thread.
     */
    private void messagesFromServer() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while ((request = in.readLine()) != null) {
                        System.out.println("Reading input from server: " + request);
                        processServerRequest();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                        socket.close();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Server disconnected!",
                                    ButtonType.OK);
                            alert.showAndWait();
                        });
                        System.out.println("Server Disconnected");
                        System.exit(0);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    /**
     * This method handles requests received from the server
     */
    private void processServerRequest() {
        String[] parts = request.split("\\|");
        String command = parts[0];
        String username;
        String IPAddress;
        int callingPort;
        switch (command) {
            case "REGISTERED_SUCCESSFULLY":
                System.out.println(">> Registered successfully!");
                sendToServer("GET_CONNECTED_USERS|" + myUsername);
                Platform.runLater(() -> {

                    try {
                        showClientGui();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
                break;
            case "USERNAME_TAKEN":
                System.out.println("username taken choose a different");
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Username taken!",
                            ButtonType.OK);
                    alert.showAndWait();
                });
                break;
            case "DIRECT_MESSAGES":
                break;
            case "CALL_REQUEST":
                username = parts[1];
                IPAddress = parts[2];
                callingPort = Integer.parseInt(parts[3]);
                String otherParticipants = parts[4];
                int callID = Integer.parseInt(parts[5]);
                sendCallResponse(username, IPAddress, callingPort, otherParticipants, callID);
                break;
            case "CALL_RESPONSE":
                username = parts[1];
                String accepted = parts[2];
                Platform.runLater(() -> {
                    try {
                        System.out.println("Closing outgoingCallWindow");
                        Stage stage = (Stage) outgoingCallRoot.getScene().getWindow();
                        // Stage stage = (Stage) outgoingCallEndButton.getScene().getWindow();
                        stage.close();
                    } catch (Exception e) {

                    }
                });
                if (!endOutgoingCall) {
                    // Do stuff with GUI
                    if (accepted.equals("ACCEPT")) {
                        IPAddress = parts[3];
                        callingPort = Integer.parseInt(parts[4]);
                        callID = Integer.parseInt(parts[5]);
                        callAccepted(username, IPAddress, callingPort, callID);

                    } else {
                        // Do stuff wit GUI
                    }
                }
                break;
            case "JOIN_CALL":
                username = parts[1];
                IPAddress = parts[2];
                callingPort = Integer.parseInt(parts[3]);
                // GUI update
                voip.addClient(username, IPAddress, callingPort);
                // find user in users using calle string
                for (User user : users) {
                    if (user.getUsername().equals(username)) {
                        usersOnCall.add(new User(user.getUsername(), user.getAvatarURL()));
                        break;
                    }
                }
                Platform.runLater(() -> {
                    try {
                        showPartipantsOnCall(usersOnCall);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
                break;
            case "LEFT_CALL":
                // GUI code
                username = parts[1];
                voip.removeClient(username);
                if (voip.totalOnCall == 0) {
                    voip.stop();
                    voip = null;
                    int index = -1;
                    for (User user : usersOnCall) {
                        if (user.getUsername().equals(username)) {

                            index = usersOnCall.indexOf(user);
                            break;
                        }
                    }
                    usersOnCall.remove(index);
                    Platform.runLater(() -> {
                        client.muteButton.setDisable(true);
                        client.hangUpButton.setDisable(true);
                        client.addParticipantButton.setDisable(true);
                        client.gridAnchorPane.getChildren().remove(callersGridPane);
                        callersGridPane.getChildren().clear();
                        // Platform.runLater(() -> {
                        try {
                            showPartipantsOnCall(usersOnCall);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // });
                    });
                }
                break;

            case "DIRECT_MESSAGE":
                messageSender = parts[1] + " to you: " + parts[2];
                Platform.runLater(() -> {
                    client.chatTextArea.appendText(messageSender + "\n");
                });
                break;
            case "BROADCAST_MESSAGE":
                messageSender = parts[1] + " to Everyone: " + parts[2];
                Platform.runLater(() -> {
                    client.chatTextArea.appendText(messageSender + "\n");
                });
                System.out.println(messageSender);
                break;
            case "CONNECTED_USERS":
            case "NEW_CLIENT":
                users.add(new User(parts[1],
                        "src/main/resources/com/group7/assets/user_avatars/" + parts[2]));
                // GUI update
                Platform.runLater(() -> {
                    updateClientGUI();
                });
                break;
            case "CLIENT_LEFT":
                usernameToRemove = parts[1];
                int index = 19;
                for (User user : users) {
                    if (user.getUsername().equals(usernameToRemove)) {
                        index = users.indexOf(user);
                        break;
                    }
                }
                users.remove(index);
                clientsOnGUI.remove(usernameToRemove);
                Platform.runLater(() -> {
                    removeUserClientGUI();
                });
                break;
            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }

    /**
     * Sends a call response to the caller with the specified information, and
     * either accepts or denies the call based on whether the user is already on
     * another call.
     *
     * @param caller            the username of the caller.
     * @param callerIPAddress   the IP address of the caller.
     * @param callerPort        the port number of the caller.
     * @param otherParticipants a string representing other participants on the
     *                          call.
     * @param callID            the ID of the call.
     */
    private void sendCallResponse(String caller, String callerIPAddress, int callerPort, String otherParticipants,
            int callID) {

        boolean alreadyOnCall = voip != null;

        // getting list of all participants on the call
        String[] othParArr = otherParticipants.split("#");
        ArrayList<User> usersCalling = new ArrayList<>();
        for (User user : users) {
            if (caller.equals(user.getUsername())) {
                usersCalling.add(new User(
                        caller, user.getAvatarURL()));
                break;
            }
        }
        for (int i = 0; i < othParArr.length; i++) {
            if (!othParArr[i].equals(myUsername)) {
                for (User user : users) {
                    if (othParArr[i].equals(user.getUsername())) {
                        usersCalling.add(new User(
                                othParArr[i], user.getAvatarURL()));
                        break;
                    }
                }
            }
        }

        incomingCaller = caller;
        incomingCallerIPAddress = callerIPAddress;
        incomingCallerPort = callerPort;
        incomingCallID = callID;

        if (!alreadyOnCall) {
            // GUI intervention

            Platform.runLater(() -> {
                showIncomingCallGUI(usersCalling);
            });

        } else {
            // GUI intervention
            sendToServer("CALL_RESPONSE|" + myUsername + "|" + caller + "|BUSY|" + callID);
        }

    }

    /**
     * Handles a call acceptance message from the callee and adds them to the
     * current VoIP call if one exists, otherwise initializes a new VoIP call with
     * the callee as the first participant.
     *
     * @param callee          the username of the callee who accepted the call.
     * @param calleeIPAddress the IP address of the callee.
     * @param calleePort      the port number of the callee.
     */
    private void callAccepted(String callee, String calleeIPAddress, int calleePort, int callID) {
        if (voip == null) {
            voip = new VoIP(portCalling);
            voip.setCallID(callID);
            voip.addClient(callee, calleeIPAddress, calleePort);
            voip.receiveVoIP();
            voip.sendVoIP();
        } else {
            voip.addClient(callee, calleeIPAddress, calleePort);
        }
        // find user in users using calle string
        for (User user : users) {
            if (user.getUsername().equals(callee)) {
                usersOnCall.add(new User(user.getUsername(), user.getAvatarURL()));
                break;

            }

        }
        Platform.runLater(() -> {
            try {
                showPartipantsOnCall(usersOnCall);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        // add new user using user referen

        // call partiantiant grid function
    }

    /**
     * Generates port numbers for VoIP calling and file transfer.
     */
    // private void generatePorts() {
    // portCalling = findAvailablePort(4000);
    // portFileTransfer = findAvailablePort(portCalling + 1);
    // }

    /**
     * Finds an available port number starting from the specified port number.
     *
     * @param startPort the port number to start the search from.
     * @return an available port number.
     */
    // private int findAvailablePort(int startPort) {
    // boolean foundPort = false;
    // int port = startPort;
    // while (!foundPort) {
    // try (ServerSocket ss = new ServerSocket(port)) {
    // ss.setReuseAddress(true);
    // ss.close();
    // break;
    // } catch (Exception ex) {
    // port++;
    // }
    // }
    // return port;
    // }

}
