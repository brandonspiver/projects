package com.group7.server;

import java.util.ArrayList;
import java.util.HashMap;

public class Call {
    private ArrayList<String> participants = new ArrayList<>();
    private HashMap<String, String> status = new HashMap<>();

    public Call() {

    }

    /**
     * Adds a participant to the list of participants.
     *
     * @param username the username of the participant to be added.
     * @param status   the status of the participant to be added.
     */
    public void addParticipant(String username, String status) {
        participants.add(username);
        this.status.put(username, status);
    }

    /**
     * Removes a participant from the list of participants.
     *
     * @param username the username of the participant to be removed.
     */
    public void removeParticipant(String username) {
        participants.remove(username);
    }

    /**
     * Sets the status of a participant.
     *
     * @param username the username of the participant whose status is to be set.
     * @param status   the status to be set for the participant.
     */
    public void setStatus(String username, String status) {
        this.status.put(username, status);
    }

    /**
     * Returns an ArrayList of participants who are currently on call, excluding the
     * participant with the given username.
     *
     * @param username the username of the participant for whom the list of
     *                 participants on call is being retrieved.
     * @return an ArrayList of participants who are currently on call, excluding the
     *         participant with the given username.
     */
    public ArrayList<String> getOnCalls(String username) {
        ArrayList<String> participantsOnCall = new ArrayList<>();
        for (String client : participants) {
            try {
                if (status.get(client).equals("ON_CALL") && !client.equals(username)) {
                    participantsOnCall.add(client);
                }
            } catch (NullPointerException ex) {

            }
        }
        return participantsOnCall;
    }

    /**
     * Returns a string representation of the list of participants who are currently
     * on call, excluding the participant with the given username.
     *
     * @param username the username of the participant for whom the list of
     *                 participants on call is being retrieved.
     * @return a string representation of the list of participants who are currently
     *         on call, excluding the participant with the given username.
     */
    public String getOnCallsStringForm(String username) {
        ArrayList<String> participantsOnCall = getOnCalls(username);
        String string = "";
        for (String client : participantsOnCall) {
            string = string + client + "#";
        }
        string = string.substring(0, string.length() - 2);
        return string;
    }

    public void printAll() {
        for (String participant : participants) {
            System.out.println("__" + participant + " " + status.get(participant));

        }
        System.out.println("\n");
    }

}
