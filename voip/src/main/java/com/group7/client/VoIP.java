package com.group7.client;

import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.*;

public class VoIP {

    // VoIP Section

    private int callID;
    private volatile boolean calling;
    private volatile int port; // receiving voip on this port
    public int totalOnCall;
    private boolean muted = false;
    private static ArrayList<String> clientList = new ArrayList<>();
    private static HashMap<String, Integer> ports = new HashMap<>();
    private static HashMap<String, String> addresses = new HashMap<>();

    DatagramSocket datSendSock;
    AudioFormat audioForm = new AudioFormat(48000, 16, 1, true, false);
    TargetDataLine targLine;
    DatagramSocket datRecSock;
    SourceDataLine sourDatLine;

    /**
     * Initializes a new instance of the VoIP class with the specified port number,
     * sets the 'calling' flag to true, and sets the 'totalOnCall' count to 0.
     *
     * @param port the port number to be used for the VoIP instance.
     */
    public VoIP(int port) {
        this.port = port;
        calling = true;
        totalOnCall = 0;
    }

    /**
     * Adds a client to the list of clients, increments the 'totalOnCall' count, and
     * maps the client's username, address, and port to their respective HashMaps.
     *
     * @param username the username of the client to be added.
     * @param address  the IP address of the client to be added.
     * @param port     the port number of the client to be added.
     */
    public void addClient(String username, String address, int port) {
        System.out.println("Adding to Call: " + username + " with IP: " + address + " with port:" + port);
        totalOnCall++;
        clientList.add(username);
        ports.put(username, port);
        addresses.put(username, address);
    }

    /**
     * Removes a client from the list of clients, decrements the 'totalOnCall'
     * count, and removes the client's username, address, and port from their
     * respective HashMaps.
     *
     * @param username the username of the client to be removed.
     */
    public void removeClient(String username) {
        totalOnCall--;
        clientList.remove(username);
        ports.remove(username);
        addresses.remove(username);
    }

    /**
     * Sets the call ID to the specified value.
     *
     * @param callID the call ID to be set.
     */
    public void setCallID(int callID) {
        this.callID = callID;
    }

    /**
     * Returns the current call ID.
     *
     * @return the current call ID.
     */
    public int getCallID() {
        return callID;
    }

    public void mute() {
        if (muted) {
            muted = false;
        } else {
            muted = true;
        }
    }

    /**
     * Receives voice communication
     */
    public void receiveVoIP() {

        calling = true;
        // Separate Thread for Receiving needed to concurrently receive a single user's
        // Audio
        new Thread(new Runnable() {

            public void run() {

                System.out.println("recieveVoip started on: " + port);
                try {
                    datRecSock = new DatagramSocket(port);
                    byte[] byt = new byte[4096];

                    DataLine.Info datInfo = new DataLine.Info(SourceDataLine.class, audioForm);
                    sourDatLine = (SourceDataLine) AudioSystem.getLine(datInfo);

                    // Opens
                    sourDatLine.open(audioForm);
                    sourDatLine.start();
                    while (calling) {
                        DatagramPacket datPack = new DatagramPacket(byt, byt.length);
                        try {
                            datRecSock.receive(datPack);
                            sourDatLine.write(datPack.getData(), 0, 4096);
                        } catch (Exception ex) {

                        }
                        // System.out.println("Receiving voip on port: " + port);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends voice communication
     */
    public void sendVoIP() {

        calling = true;
        new Thread(new Runnable() {

            public void run() {
                try {
                    System.out.println("sendVoip started");
                    datSendSock = new DatagramSocket();

                    DataLine.Info datInfo = new DataLine.Info(TargetDataLine.class, audioForm);
                    targLine = (TargetDataLine) AudioSystem.getLine(datInfo);
                    targLine.open();

                    // The TargetDataLine connects to AudioInputStream from a mic
                    AudioInputStream audioIn = new AudioInputStream(targLine);
                    targLine.start();

                    // Unsure if while loop is needed while in thread run()
                    while (calling) {

                        // Reads 4096 bytes at a time (Subject to change but that seems fine)
                        // and sends to the relavant place;
                        byte[] byt = audioIn.readNBytes(4096);
                        if (!muted) {
                            for (String participant : clientList) {
                                try {
                                    DatagramPacket dataPack = new DatagramPacket(byt, byt.length,
                                            InetAddress.getByName(addresses.get(participant)), ports.get(participant));
                                    datSendSock.send(dataPack);
                                } catch (Exception e) {
                                    // Incase this client left before removing
                                }
                                // System.out.println("Sending voip to: " + participant + " with IP: "
                                // + addresses.get(participant) + " with port:" + ports.get(participant));
                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    /**
     * This method stops the audio transmission
     * 
     * @throws IOException if an I/O error occurs while closing the send and receive
     *                     sockets.
     */
    public void stop() {
        calling = false;
        sourDatLine.stop();
        sourDatLine.close();
        targLine.stop();
        targLine.close();
        datSendSock.close();
        datRecSock.close();
        System.out.println("stopped voip!");
    }
}
