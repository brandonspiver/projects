
package com.group7.server;

import java.io.*;
import java.net.*;

/**
 * 
 * This class represents a thread for handling incoming client requests.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private BufferedReader in;
    private String username;

    /**
     * Constructor for ClientHandler.
     * 
     * @param clientSocket the socket for the client connection
     * @param server       the Server instance
     */
    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    /**
     * Run method for the thread.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("reading input line " + inputLine);
                processClientRequest(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.unregisterClient(username);
                if (in != null) {
                    in.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Processes the incoming client request.
     * 
     * @param request the client request
     */
    private void processClientRequest(String request) {
        String[] parts = request.split("\\|");
        String command = parts[0];
        String username;
        int callID;
        switch (command) {
            case "REGISTER":
                // System.out.println("here\n");
                username = parts[1];
                this.username = parts[1];
                int callingPort = Integer.parseInt(parts[2]);
                int fileTransferPort = Integer.parseInt(parts[3]);
                String avatar = parts[4];
                server.registerClient(username, clientSocket, callingPort, fileTransferPort, avatar);
                break;
            case "UNREGISTER":
                username = parts[1];
                server.unregisterClient(username);
                break;
            case "CALL_REQUEST":
                username = parts[1];
                String callees = parts[2];
                server.sendCallRequest(username, callees);
                break;
            case "CALL_RESPONSE":
                username = parts[1];
                String caller = parts[2];
                String accepted = parts[3];
                callID = Integer.parseInt(parts[4]);
                server.sendCallResponse(caller, username, accepted, callID);
                break;
            case "END_CALL":
                username = parts[1];
                callID = Integer.parseInt(parts[2]);
                server.endCall(username, callID);
                break;
            case "ADD_PARTICIPANT":
                username = parts[1];
                String participant = parts[2];
                callID = Integer.parseInt(parts[3]);
                server.addParticipantToCall(username, participant, callID);
                break;
            case "GET_CONNECTED_USERS":
                username = parts[1];
                server.sendConnectedUsers(username);
                break;
            case "DIRECT_MESSAGE":
                username = parts[1];
                server.sendDirectMessage(username, parts[2], parts[3]);
                break;
            case "BROADCAST_MESSAGE":
                username = parts[1];
                server.broadcastMessage(username, parts[2]);
                break;

            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }
}