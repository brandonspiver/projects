package com.group7.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.*;

public class FileTransfer {
    private int fileTransferPort;
    private DatagramSocket ds;

    public FileTransfer(int port) {
        fileTransferPort = port;
    }

    public void sendFile(File f, String receiveAddress, int receivePort) {
        new Thread(() -> {
            System.out.println("Sending to address: " + receiveAddress +
                    " and port: " + receivePort);
            try {
                DatagramSocket ds = new DatagramSocket();
                InetAddress address = InetAddress.getByName(receiveAddress);

                byte[] fileBytes = new byte[(int) f.length()];
                int fileSize = fileBytes.length;
                String fileName = f.getName();
                String string = fileName + "#" + Integer.toString(fileSize);
                byte[] fileNameSizeBytes = string.getBytes();
                DatagramPacket pt = new DatagramPacket(fileNameSizeBytes,
                        fileNameSizeBytes.length, address, receivePort);
                ds.send(pt);

                System.out.println("Sent file name: " + fileName +
                        " with file size: " + fileBytes.length);
                FileInputStream fis = null;
                fis = new FileInputStream(f);
                fis.read(fileBytes);
                fis.close();
                int sqNumber = 0;
                boolean flag;
                int sqNr = 0;
                for (int i = 0; i < fileBytes.length; i = i + 1021) {
                    sqNumber = sqNumber + 1;

                    byte[] message = new byte[1024];
                    message[0] = (byte) (sqNumber >> 8);
                    message[1] = (byte) (sqNumber);

                    if ((i + 1021) >= fileBytes.length) {
                        flag = true;
                        message[2] = (byte) (1);
                    } else {
                        flag = false;
                        message[2] = (byte) (0);
                    }

                    if (!flag) {
                        System.arraycopy(fileBytes, i, message, 3,
                                1021);
                    } else {
                        System.arraycopy(fileBytes, i, message, 3,
                                fileBytes.length - i);
                    }

                    DatagramPacket sendPacket = new DatagramPacket(message,
                            message.length, address, receivePort);
                    ds.send(sendPacket); // Sending the data
                    System.out.println("Sending packet: " + sqNumber);

                    double total = Math.ceil(fileSize / 1024);
                    int prog = (int) Math.floor(sqNumber / total * 100);
                    System.out.println("this value " + prog);

                    boolean check;

                    while (true) {
                        byte[] sqNrRec = new byte[2];
                        DatagramPacket packet = new DatagramPacket(sqNrRec,
                                sqNrRec.length);

                        try {
                            Thread.sleep(50);
                            ds.receive(packet);
                            sqNr = ((sqNrRec[0] & 0xff) << 8) + (sqNrRec[1]
                                    & 0xff);
                            check = true;
                        } catch (SocketTimeoutException e) {
                            check = false; //
                        }

                        if ((sqNr == sqNumber) && (check)) {
                            break;
                        } else {
                            ds.send(sendPacket);
                        }
                    }
                }
                ds.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }

        }).start();
    }

    public void receiveFile() {
        new Thread(() -> {
            try {
                System.out.println("Receiver is live on port: " + fileTransferPort);
                ds = new DatagramSocket(fileTransferPort);
                while (true) {
                    byte[] fileNameSizeBytes = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(fileNameSizeBytes,
                            fileNameSizeBytes.length);
                    ds.receive(packet);
                    byte[] data = packet.getData();
                    String string = new String(data, 0, packet.getLength());
                    String[] parts = string.split("#");
                    String fileName = parts[0];
                    int fileSize = Integer.parseInt(parts[1]);

                    System.out.println("Receiving file size: " + fileSize);
                    Float progressSize = 0.0F;
                    progressSize = progressSize + 1021.0F;
                    File f = new File("files/" + fileName); // Creating the file
                    FileOutputStream fos = new FileOutputStream(f);
                    boolean reachedEnd;
                    int sqNumber = 0;
                    int prevSqNumber = 0;

                    byte[] msg = new byte[1024];
                    byte[] filePartBytes = new byte[1021];

                    DatagramPacket receivedPacket = new DatagramPacket(msg,
                            msg.length);
                    ds.receive(receivedPacket);
                    msg = receivedPacket.getData();

                    InetAddress address = receivedPacket.getAddress();
                    int sendPort = receivedPacket.getPort();

                    sqNumber = ((msg[0] & 0xff) << 8) + (msg[1] & 0xff);
                    reachedEnd = (msg[2] & 0xff) == 1;

                    progressSize += 1024;

                    double total = Math.ceil(fileSize / 1024);
                    int prog = (int) Math.floor(prevSqNumber / total * 100);
                    System.out.println("this value " + prog);

                    if ((sqNumber - 1) == prevSqNumber) {

                        prevSqNumber++;

                        System.arraycopy(msg, 3, filePartBytes,
                                0, 1021);

                        fos.write(filePartBytes);
                        System.out.println("Correct packet received: "
                                + prevSqNumber);
                        byte[] sqPacket = new byte[2];
                        sqPacket[0] = (byte) (sqNumber >> 8);
                        sqPacket[1] = (byte) (sqNumber);
                        DatagramPacket acknowledgement = new DatagramPacket(
                                sqPacket, sqPacket.length, address, sendPort);
                        ds.send(acknowledgement);
                    } else {
                        System.out.println(
                                "Did not get sequennce Number"
                                        + (prevSqNumber + 1) + " but got "
                                        + sqNumber);

                        byte[] sqPacket = new byte[2];
                        sqPacket[0] = (byte) (prevSqNumber >> 8);
                        sqPacket[1] = (byte) (prevSqNumber);
                        DatagramPacket acknowledgement = new DatagramPacket(
                                sqPacket, sqPacket.length, address, sendPort);
                        ds.send(acknowledgement);
                        System.out.println("Sent ack: Sequence Number = " +
                                prevSqNumber);

                    }

                    if (reachedEnd) {
                        fos.close();
                        break;
                    }
                }
                ds.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }

        }).start();

    }

}
