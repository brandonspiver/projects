
/** 
 * 
 * TCP chat client that communicates with a TCP chat server
 * numbers in square brackets (eg. [1]) are used to correlate the
 * communication between the server and clients.
 *
 * @author Michael Bonthuys	23687770@sun.ac.za
 * @author Doğan Kusluoglu	22577440@sun.ac.za
 * @author Brandon Spiver	22701168@sun.ac.za
 * @author Cole Kisten		22637893@sun.ac.za
 * @author Shaygah Hendricks	22734775@sun.ac.za
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import java.io.File;

import javax.sound.sampled.*;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * client class: communicates with the server
 */
public class client extends Application {
    Stage window;
    Scene scene1, scene2, scene3, scene4;
    TextField inputNickname, inputMessage;
    Label label = new Label();
    TextArea output, clientList;
    Button buttonWhisper;
    public volatile static ArrayList<String> clients = new ArrayList<>();
    public static Socket s = null;
    private static BufferedReader br = null;
    private static BufferedWriter bw = null;
    private String nickname;

    String css = this.getClass().getResource("style.css").toExternalForm();

    /**
     * main methods starts the client and the GUI with javafx
     */
    public static void main(String[] args) {
        try {
            // [1]: Requesting to connect To server
            s = new Socket("localhost", 4000);

            // Streams used to communicate with server
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            receiveClients(); // (line 64)

        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("No server is online");
            System.exit(0);
        }

        // GUI is launched here (code at line 160)
        launch(args);

    }

    /*
     * [2]: List of that have already connected to server are received here
     */
    public static void receiveClients() throws IOException {

        String msg = br.readLine();
        while (!msg.equals("//done")) {
            clients.add(msg);
            msg = br.readLine();
        }
    }

    /*
     * Sends messages to server
     */
    public void sendToServer(String msg) {
        try {
            bw.write(msg);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            makeClose(); // (line 141)
        }
    }

    /*
     * A thread is started to receive messages from GUI in background
     */
    public void messagesFromServer() {
        new Thread(new Runnable() {

            public void run() {

                try {

                    // [5]: All messages server sends are received here
                    String msg;

                    while (s.isConnected()) {

                        // Thread waits here till message received from server
                        msg = br.readLine();

                        // [5.1]
                        if (msg.startsWith("##Join")) {
                            if (!msg.substring(6).equals("null")) {
                                clients.add(msg.substring(6));
                                output.appendText(msg.substring(6) + " joined\n");
                                playSound(
                                        "/home/brandon/Documents/2023projects/project1/phase3/project/files/Notification-2.wav");
                            }
                        }
                        // [5.6]
                        else if (msg.startsWith("##Left")) {
                            if (!msg.substring(6).equals("null")) {
                                clients.remove(msg.substring(6));
                                output.appendText(msg.substring(6) + " left\n");
                                playSound(
                                        "/home/brandon/Documents/2023projects/project1/phase3/project/files/Notification-2.wav");
                            }
                        }

                        // [5.2], [5.3], [5.4], [5.5]
                        else {
                            output.appendText(msg + "\n");
                            playSound(
                                    "/home/brandon/Documents/2023projects/project1/phase3/project/filesNotification-1.wav");
                        }
                        updateClientListGUI(); // (line 128)
                    }
                } catch (Exception e) {
                    System.out.println("Server disconnected");
                    // e.printStackTrace();
                    makeClose(); // (line 141)
                }
            }
        }).start();
    }

    /*
     * Updates the GUI TextArea where online clients are displayed
     */
    public void updateClientListGUI() {
        clientList.clear();
        clientList.appendText("Online Clients:\nYou\n");
        for (String client_ : clients) {
            clientList.appendText(client_ + "\n");
        }
        if (clients.isEmpty())
            buttonWhisper.setDisable(true);
        else
            buttonWhisper.setDisable(false);
    }

    /* Closes Streams when server disonnects */
    public void makeClose() {
        try {
            if (br != null)
                br.close();
            if (bw != null)
                bw.close();
            if (s != null)
                s.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        System.exit(0);
    }

    /*
     * 
    */
    public static void playSound(String filePath) {
        try {

            File f = new File(filePath);

            if (f.exists()) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }

        } catch (Exception e) {

        }
    }

    /*
     * GUI, Done with javafx
     * I didnt really explain how javafx works but i added a link to the shared doc
     * that i used to do most of the GUI.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        // Layout 1 - Choose a nickname
        Button buttonEnter = new Button("Enter");
        buttonEnter.setOnAction(e -> {
            nickname = inputNickname.getText();
            inputNickname.setText("");
            if (nickname.equals("")) {
                label.setText("Please enter a nickname!");
                window.setScene(scene2);
                playSound("/home/brandon/Downloads/Error-Sound.wav");
            } else if (clients.contains(nickname)) {
                label.setText("Already taken, try again.");
                window.setScene(scene2);
                playSound("/home/brandon/Downloads/Error-Sound.wav");
            } else {
                // [3]: Unique nickname gets sent to server
                sendToServer(nickname); // procedure I made (line: 72)
                updateClientListGUI(); // (line 128)
                window.setScene(scene3);
                window.setTitle("Chat Room: " + nickname);
                if (clients.isEmpty())
                    buttonWhisper.setDisable(true);
                else
                    buttonWhisper.setDisable(false);
                messagesFromServer();
            }
        });

        Label label1 = new Label("Enter a unique nickname.");
        inputNickname = new TextField();
        inputNickname.setMaxWidth(280);

        VBox layout1 = new VBox(10);
        // layout1.getChildren().add(logo);
        layout1.getChildren().add(label1);
        layout1.getChildren().add(inputNickname);
        layout1.getChildren().add(buttonEnter);
        layout1.setAlignment(Pos.CENTER);
        scene1 = new Scene(layout1, 300, 150);
        scene1.getStylesheets().add(css);

        // Layout 2 - If nickname already exists, or no nickname selected
        Button buttonCancel = new Button("Cancel");
        buttonCancel.setOnAction(e -> System.exit(0));
        Button buttonTryAgain = new Button("Try again");
        buttonTryAgain.setOnAction(e -> window.setScene(scene1));
        VBox layout2 = new VBox(20);
        layout2.getChildren().add(label);
        layout2.getChildren().add(buttonTryAgain);
        layout2.getChildren().add(buttonCancel);
        layout2.setAlignment(Pos.CENTER);
        scene2 = new Scene(layout2, 300, 200);
        scene2.getStylesheets().add(css);

        // Layout 3 - Chat room
        inputMessage = new TextField();
        inputMessage.setLayoutX(20);
        inputMessage.setLayoutY(340);
        inputMessage.setMinHeight(35);
        inputMessage.setMinWidth(400);
        output = new TextArea();
        output.setEditable(false);
        output.setMaxHeight(300);
        output.setMinHeight(300);
        output.setMaxWidth(400);
        output.setMinWidth(400);
        output.setLayoutX(20);
        output.setLayoutY(20);
        clientList = new TextArea();
        clientList.setEditable(false);
        clientList.setMaxHeight(300);
        clientList.setMinHeight(300);
        clientList.setMaxWidth(180);
        clientList.setMinWidth(180);
        clientList.setLayoutX(440);
        clientList.setLayoutY(20);
        Button buttonSend = new Button("Send");
        buttonSend.setOnAction(e -> {
            if (inputMessage.getText().equals("")) {
                NoMessage.display(css, "No message typed yet"); // (line 350)
            } else {
                // [4.1]: message in TextField gets sent to server (Sends to all clients)
                String msg = inputMessage.getText();
                inputMessage.setText("");
                sendToServer(msg); // line 72
            }
        });

        buttonSend.setLayoutX(440);
        buttonSend.setLayoutY(340);
        buttonWhisper = new Button("Whisper");
        buttonWhisper.setOnAction(e -> {
            if (inputMessage.getText().equals("")) {
                NoMessage.display(css, "No message typed yet"); // (line 350)
            } else {
                // [4.2]: message in TextField gets sent to server (Sends to wisperee)
                String whisperee = whisper.display(clients, css);
                if (!whisperee.equals("--Cancel--")) {
                    sendToServer("{" + whisperee + "}" + inputMessage.getText());
                    inputMessage.setText("");
                }
            }

        });
        buttonWhisper.setLayoutX(520);
        buttonWhisper.setLayoutY(340);
        buttonWhisper.setMaxHeight(480);

        Pane layout3 = new Pane();
        layout3.getChildren().addAll(output, clientList, inputMessage, buttonSend, buttonWhisper);
        layout3.setStyle("-fx-background-color: #460855;");
        scene3 = new Scene(layout3, 660, 400);
        scene3.getStylesheets().add(css);

        window.setScene(scene1);
        window.setTitle("Chat room");
        window.show();
        window.setOnCloseRequest(e -> System.exit(0));
    }

}

// this class is for a pop up window when the user clicks whisper
class whisper {

    private static String choice; // stores the whisperee

    // called when user clicks whisper button
    public static String display(ArrayList<String> clients, String css) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Whisper");
        window.setMinWidth(250);

        ChoiceBox<String> choiceClients = new ChoiceBox<>();
        choiceClients.getItems().add("--Select--");
        choiceClients.getItems().addAll(clients);
        choiceClients.setValue("--Select--");

        Label label = new Label("Choose client you wish to whisper to");
        choice = "--Cancel--";
        Button buttonSend = new Button("Send");
        buttonSend.setOnAction(e -> {
            choice = choiceClients.getValue();
            if (choice.equals("--Select--")) {
                label.setText("ERROR! Please select a client");
            } else {
                window.close();
            }
        });
        Button buttonCancel = new Button("Cancel");
        buttonCancel.setOnAction(e -> {
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, choiceClients, buttonSend, buttonCancel);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 200);
        scene.getStylesheets().add(css);
        window.setScene(scene);
        window.showAndWait();

        return choice;
    }

}

// this class is for a pop up window when the user clicks send/whisper without
// having typed a message
class NoMessage {

    public static void display(String css, String message) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("ERROR!");
        window.setMinWidth(250);

        Label label = new Label(message);
        Button buttonOK = new Button("OK");
        buttonOK.setOnAction(e -> {
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, buttonOK);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(css);
        window.setScene(scene);
        window.showAndWait();
    }

}