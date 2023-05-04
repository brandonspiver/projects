package com.group7.server;

import java.net.Socket;

public class ClientInfo {
    private String username;
    private Socket socket;
    private int callingPort;
    private int fileTransferPort;
    private String avatar;

    public ClientInfo(String username, Socket socket, int callingPort, int fileTransferPort, String avatar) {
        this.username = username;
        this.socket = socket;
        this.callingPort = callingPort;
        this.fileTransferPort = fileTransferPort;
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getCallingPort() {
        return callingPort;
    }

    public int getFileTransferPort() {
        return fileTransferPort;
    }

    public String getAvatar() {
        return avatar;
    }
}
