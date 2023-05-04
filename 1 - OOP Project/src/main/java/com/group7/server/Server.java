package com.group7.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The Server class is responsible for handling client connections and
 * managing chat groups. It also provides a JavaFX-based graphical user
 * interface for server control.
 */
public class Server extends Application {

    // *********[ Declare local variable related to GUI here ]*********|

    @FXML // This tag allows Scenebuilder to "see" the variable
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button connectServer;

    @FXML
    private TextField portName;

    @FXML
    private TextArea serverLog;

    @FXML
    private TextArea userLog;

    @FXML
    private Button startButton;

    @FXML
    private AnchorPane AnPane;
    // *****************************************************************|

    // *******[ Declare local variable not related to GUI here ]********|
    private static Scene scene;
    private static Map<Integer, String> serverMessages = new HashMap<Integer, String>();
    private String logMessages;
    private String connectLog = "";
    // *****************************************************************|

    static ArrayList<String> participantsOnCall;

    private void initMap() {
        serverMessages.putIfAbsent(0, "✗ FAILED TO START SERVER.\n");
        serverMessages.putIfAbsent(1, "✓ CONNECTED TO START SERVER.\n");
        serverMessages.putIfAbsent(2, "✗ CLIENT FAILED JOINED SERVER.\n");
        serverMessages.putIfAbsent(3, "✓ CLIENT JOINED SERVER.\n");
    }

    // Starts GUI
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("serverGUI"), 695, 603);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            // Code to handle window close event
            System.out.println("Server GUI is closing");
            System.exit(0);
        });
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {

        launch();
    }

    private void updateServerLog(String message) {
        Platform.runLater(() -> {
            serverLog.setText(logMessages + message);
            logMessages = logMessages + message;
        });
    }
    // End of GUI code

    // Start adding functional Server code. Do not modify above code

    /**
     * TODO:
     * 
     * 1. Create TCP server to allow communication between clients and server.
     * 2. Add username validaiton and storing on online users and notifying other
     * users
     * 3. Allow users to to message each other through the server
     * 4. Allow users to create/ join groups
     * 5. Have error handling enabled as constant messages stored in a hashmap to
     * make it easier
     * 6. Enable call notification and connection/ disconnection between user/s
     * (caller details should be sent to reciever)
     */

    private int PORT = 12345;
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, ClientInfo> clients;
    private ConcurrentHashMap<Integer, Call> calls;
    private boolean isRunning;
    /**
     * A scheduled executor service for running periodic tasks.
     */
    private final ScheduledExecutorService executorService;

    /**
     * Constructs a new Server instance.
     */
    public Server() {
        this.clients = new ConcurrentHashMap<>();
        this.calls = new ConcurrentHashMap<>();
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * Activates the server, starting a new thread to accept client connections.
     */
    @FXML
    private void activateServer() {
        //
        if (portName.getText().equals("")) {
            Alert alert = new Alert(AlertType.ERROR, "Invalid host name or port name", ButtonType.CLOSE);
            alert.showAndWait();
        } else {
            try {
                initMap();
                logMessages = "Server started on Port: " + portName.getText() + "\n";
                PORT = Integer.parseInt(portName.getText());

                // Run the startServer method in a new thread
                new Thread(() -> {
                    try {
                        this.startServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                startButton.setDisable(true);
                serverLog.clear();
                updateServerLog(serverMessages.get(1));
            } catch (Exception e) {
                updateServerLog(serverMessages.get(0));
            }
        }
    }

    /**
     * Starts the server by creating a server socket and a separate thread
     * to accept incoming client connections.
     *
     * @throws IOException if there is an error creating the server socket
     */
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        isRunning = true;

        // Create a new thread for the server loop
        new Thread(() -> {
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    updateServerLog(serverMessages.get(3));
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    System.out.println("client : " + clientSocket.getPort());
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    if (isRunning) {
                        updateServerLog(serverMessages.get(2));
                    }
                }
            }
        }).start();

        // Schedule periodic tasks to be executed
        executorService.scheduleAtFixedRate(() -> {
            System.out.println("Perform periodic tasks here...");
        }, 1, 1, TimeUnit.MINUTES);
    }

    @FXML
    public void stop() {
        try {
            isRunning = false;
            if (serverSocket != null) {
                serverSocket.close();
            }
            // serverLog.appendText("Server stopped\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * Registers a new client with the provided username, socket, calling port, file
     * transfer port and avatar.
     * If the username is not already taken, the client is added to the clients map
     * and a message is broadcast to all clients
     * about the new client. If the username is already taken, the method sends a
     * notification to the client indicating that
     * the username is already in use.
     * 
     * @param username         The username of the client to register.
     * @param socket           The client's socket.
     * @param callingPort      The port for the client's calling connection.
     * @param fileTransferPort The port for the client's file transfer connection.
     * @param avatar           The avatar of the client.
     */
    public synchronized void registerClient(String username, Socket socket, int callingPort, int fileTransferPort,
            String avatar) {
        ClientInfo clientInfo = new ClientInfo(username, socket, callingPort, fileTransferPort, avatar);
        if (!clients.containsKey(username)) {
            clients.put(username, clientInfo);
            connectLog = connectLog + username + "\n";
            userLog.clear();
            userLog.setText(connectLog);
            System.out.println(username + " joined");
            broadcastServerWide("NEW_CLIENT|" + username + "|" + avatar, username);
            String message = "REGISTERED_SUCCESSFULLY";
            updateServerLog("Client " + username + " has registered successfully" + "\n");
            sendNotification(clientInfo, message);
        } else {
            String message = "USERNAME_TAKEN";
            sendNotification(clientInfo, message);
        }
    }

    /**
     * 
     * Sends a message to a requesting client with a list of all connected clients
     * on the server.
     * If the requester is not in the clients map, the method does nothing.
     * 
     * @param requester The username of the client requesting the list of connected
     *                  users.
     */
    public synchronized void sendConnectedUsers(String requester) {
        if (clients.containsKey(requester)) {
            ClientInfo requesterInfo = clients.get(requester);
            for (String username : clients.keySet()) {
                if (!username.equals(requester)) {
                    String message = "CONNECTED_USERS";
                    message += "|" + username;
                    ClientInfo client = clients.get(username);
                    message += "|" + client.getAvatar();
                    sendNotification(requesterInfo, message);
                }
            }
            // System.err.println(">> " + message + "\n");
        }
    }

    /**
     * 
     * Removes a client from the server's list of clients and broadcasts a
     * server-wide message that the client has left.
     * 
     * @param username The username of the client to be unregistered
     */
    public synchronized void unregisterClient(String username) {
        clients.remove(username);
        broadcastServerWide("CLIENT_LEFT|" + username, username);
        updateServerLog("Client " + username + " has left" + "\n");
        connectLog = connectLog.replace(username + "\n", "");
        userLog.clear();
        userLog.setText(connectLog);
    }

    /**
     * 
     * Sends a direct message from a sender to a receiver.
     * 
     * @param sender   The username of the client sending the message
     * @param receiver The username of the client receiving the message
     * @param message  The content of the message
     */
    public synchronized void sendDirectMessage(String sender, String receiver, String message) {
        if (clients.containsKey(receiver)) {
            ClientInfo receiverInfo = clients.get(receiver);
            String msg = "DIRECT_MESSAGE|" + sender + "|" + message;
            sendNotification(receiverInfo, msg);
        }
    }

    /**
     * 
     * Broadcasts a message from a sender to a chat group.
     * 
     * @param sender    The username of the client sending the message
     * @param groupName The name of the chat group to broadcast the message to
     * @param message   The content of the message
     */
    public synchronized void broadcastMessage(String sender, String message) {
        String msg = "BROADCAST_MESSAGE|" + sender + "|" + message;
        broadcastServerWide(msg, sender);
    }

    /**
     * 
     * Sends a notification message to all connected clients except for the client
     * with the provided username.
     * 
     * @param message  The message to send.
     * @param username The username of the client who should not receive the
     *                 message.
     */
    public void broadcastServerWide(String message, String username) {
        for (ClientInfo clientInfo : clients.values()) {
            if (!clientInfo.getUsername().equals(username)) {
                sendNotification(clientInfo, message);
            }
        }
    }

    /**
     * Sends a call request to the specified callees, inviting them to join a call
     * with the caller.
     * 
     * If any of the specified callees are not connected to the server, no call
     * request is sent to them.
     * 
     * @param caller  The username of the client making the call request.
     * 
     * @param callees A string containing the usernames of the clients to invite to
     *                the call, separated by the "#" character.
     */
    public synchronized void sendCallRequest(String caller, String callees) {
        String[] calleesArr = callees.split("#");

        // Getting call ID
        int callID = 1;
        while (calls.containsKey(callID)) {
            callID++;
        }

        // Creating call
        Call call = new Call();
        call.addParticipant(caller, "ON_CALL");
        for (int i = 0; i < calleesArr.length; i++) {
            call.addParticipant(calleesArr[i], "CALLING");
        }
        calls.put(callID, call);

        // Sending call requests
        ClientInfo callerInfo = clients.get(caller);
        for (int i = 0; i < calleesArr.length; i++) {
            if (clients.containsKey(calleesArr[i])) {
                ClientInfo calleeInfo = clients.get(calleesArr[i]);
                String msg = "CALL_REQUEST|" + caller + "|" +
                        callerInfo.getSocket().getInetAddress().getHostAddress() + "|" + callerInfo.getCallingPort()
                        + "|" + callees + "|" + callID;
                updateServerLog("Client " + caller + " has requested to call " + calleeInfo.getUsername() + "\n");
                sendNotification(calleeInfo, msg);
            }
        }
    }

    /**
     * 
     * This method sends call response to the caller from callee. It handles whether
     * the callee accepts or declines
     * the call request. If the callee accepts the request, the method sends ACCEPT
     * response to the caller and sets
     * the callee's status to ON_CALL. Then, it gets the list of all other
     * participants in the call and sends join
     * call message to each of them. Finally, it sends the join call message to the
     * callee, containing the details
     * of all other participants on the call. If the callee declines the request,
     * the method sends DECLINE response
     * to the caller and removes the callee from the call.
     * 
     * @param caller   The username of the caller.
     * @param callee   The username of the callee.updateServerLog("Client " + caller
     *                 + " has requested to call " + calleeInfo.getUsername());
     * @param accepted A string indicating whether the callee accepts or declines
     *                 the call request.
     * @param callID   The ID of the call to which the callee is responding.
     */
    public synchronized void sendCallResponse(String caller, String callee, String accepted, int callID) {
        if (clients.containsKey(caller)) {
            ClientInfo callerInfo = clients.get(caller);
            ClientInfo calleeInfo = clients.get(callee);
            Call call = calls.get(callID);
            String msg;
            if (accepted.equals("ACCEPT")) {

                // Send back to caller
                msg = "CALL_RESPONSE|" + callee + "|ACCEPT|"
                        + calleeInfo.getSocket().getInetAddress().getHostAddress() + "|"
                        + calleeInfo.getCallingPort() + "|" + callID;
                sendNotification(callerInfo, msg);
                updateServerLog("Client " + calleeInfo.getUsername() + " has accepted the call from " + caller + "\n");

                // getting other participents on the call
                // make gloabl and satic
                participantsOnCall = call.getOnCalls(caller);

                // updating this callees status
                call.setStatus(callee, "ON_CALL");
                calls.put(callID, call);

                // Sending relevent IPAdresses and ports to relevent clients
                for (String participant : participantsOnCall) {

                    // send details to other participants
                    ClientInfo participantInfo = clients.get(participant);
                    msg = "JOIN_CALL|" + callee + "|" + calleeInfo.getSocket().getInetAddress().getHostAddress()
                            + "|" + calleeInfo.getCallingPort();
                    sendNotification(participantInfo, msg);

                    // send other participants details back to this client
                    msg = "JOIN_CALL|" + participant + "|"
                            + participantInfo.getSocket().getInetAddress().getHostAddress()
                            + "|" + participantInfo.getCallingPort();
                    sendNotification(calleeInfo, msg);
                }

            } else {
                call.removeParticipant(callee);
                calls.put(callID, call);
                msg = "CALL_RESPONSE|" + callee + "|" + accepted;
                updateServerLog("Client " + caller + " has declined the call from " + caller + "\n");
                sendNotification(callerInfo, msg);
            }
        }
    }

    /**
     * 
     * Adds a participant to an existing call with the given call ID. Sends a call
     * request to the new participant with details
     * of the call, including the IP address and calling port of the existing
     * participants.
     * 
     * @param caller      The username of the participant who is making the request
     *                    to add a new participant to the call.
     * @param participant The username of the participant to be added to the call.
     * @param callID      The ID of the call to which the new participant will be
     *                    added.
     */
    public synchronized void addParticipantToCall(String caller, String participant, int callID) {
        if (clients.containsKey(participant)) {
            Call call = calls.get(callID);
            String otherParticipant = call.getOnCallsStringForm(participant);
            ClientInfo callerInfo = clients.get(caller);
            ClientInfo participantInfo = clients.get(participant);
            String msg = "CALL_REQUEST|" + caller + "|" +
                    callerInfo.getSocket().getInetAddress().getHostAddress() + "|" + callerInfo.getCallingPort()
                    + "|" + otherParticipant;
            updateServerLog("Client " + caller + " requests to add " + participant + "\n");
            sendNotification(participantInfo, msg);
        }
    }

    /**
     * 
     * This method ends a call by removing the specified participant from the call
     * and sending a notification to the remaining participants that the specified
     * participant has left the call.
     * 
     * @param username The username of the participant to be removed from the call
     * @param callID   The ID of the call from which the participant is to be
     *                 removed
     */
    public synchronized void endCall(String username, int callID) {
        Call call = calls.get(callID);
        call.removeParticipant(username);
        calls.put(callID, call);
        ArrayList<String> participantsOnCall = call.getOnCalls(username);
        String msg = "LEFT_CALL|" + username;
        updateServerLog("Client " + username + " has left the call" + "\n");
        for (String participant : participantsOnCall) {
            ClientInfo participatInfo = clients.get(participant);
            sendNotification(participatInfo, msg);
        }
    }

    /**
     * 
     * Sends a notification message to a client over the socket connection.
     * 
     * @param clientInfo the client information object containing the socket
     *                   connection
     * @param message    the notification message to be sent
     */
    public synchronized void sendNotification(ClientInfo clientInfo, String message) {

        System.out.println("Sending " + message + " to " + clientInfo.getUsername());
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientInfo.getSocket().getOutputStream()));
            bw.write(message);
            bw.newLine();
            bw.flush();
            // bw.close();
        } catch (Exception e) {
            System.out.println("Clinet disconnected");
        }
    }

}
