
/** 
* TCP chat server that facilitates all tcp chat communication between clients
*
* numbers in square brackets (eg. [1]) are used to correlate the
* communication between the server and clients.
*
* @author Michael Bonthuys	23687770@sun.ac.za
* @author Doğan Kusluoglu	22577440@sun.ac.za
* @author Brandon Spiver	22701168@sun.ac.za
* @author Cole Kisten		22637893@sun.ac.za
* @author Shaygah Hendricks	22734775@sun.ac.za
*/

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * server class: class that handles the comunication between the clients
 */
public class server {

	private ServerSocket ss;

	public server(ServerSocket ss) {
		this.ss = ss;
	}

	/**
	 * main method: gets the server started
	 */
	public static void main(String[] args) throws IOException {

		ServerSocket serverSocket = new ServerSocket(4000);
		server s = new server(serverSocket); // (line 9)
		s.startServerSocket();// starts server
	}

	/**
	 * Server is started and and waits for clients to connect
	 */
	public void startServerSocket() {

		try {
			while (!ss.isClosed()) {

				// [1]: Server waits here for a new client to request to connect
				Socket s = ss.accept();

				/*-Client manager object is declared for each client that connects to server
				 * -constructor initialises connection (line 56) */
				clientManager cm = new clientManager(s);

				// Thread is declared for cm
				Thread t = new Thread(cm);

				// starts thread by running run procedure (line 98)
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

/**
 * Each client communicates with its own clientManager object
 */
class clientManager implements Runnable {

	// Stores List of clientManagers (each thread has access to this)
	public static ArrayList<clientManager> clients = new ArrayList<>();

	private Socket s;
	private BufferedReader br;
	private BufferedWriter bw;
	private String nickname;

	/**
	 * Constructor, used to initialise connection with client
	 */
	public clientManager(Socket s) {

		try {
			this.s = s;
			// Streams are initialised
			br = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));

			System.out.println("A new client is trying to join");

			// [2]: List of clients nicknames that are already connected are sent to client
			if (!clients.isEmpty()) {
				for (clientManager client_ : clients) {
					sendToClient(bw, client_.nickname);
					// System.out.println("sent new cliet");
				}
			}

			sendToClient(bw, "//done"); // to indicate end of list

			// [3]: nickname received
			nickname = br.readLine();

			clients.add(this);// this object instance of

			if (nickname != null)
				System.out.println("Client: " + nickname + ", has joined seccessfully!");
			else
				System.out.println("Client cancelled");

			// [5.1]: Informing other clients of new arrival
			for (clientManager client_ : clients) {
				if (!client_.nickname.equals(nickname)) {
					sendToClient(client_.bw, "##Join" + nickname);
				}
			}

		} catch (Exception e) {
			makeClose();
		}

	}

	/**
	 * In this thread messaged are received the client
	 */
	@Override
	public void run() {
		try {
			String msg, wisperMsg, wisperee;
			while (s.isConnected()) {

				// [4] waits here for message from client
				msg = br.readLine();

				// [4.2]: whispering
				if (msg.charAt(0) == '{') { // checkes if client wants to whisper
					wisperee = msg.substring(1,
							msg.indexOf('}'));
					wisperMsg = msg.substring(msg.indexOf('}') + 1);

					// [5.2]: send back to this client(whisperer)
					sendToClient(bw, "You whispered to " +
							wisperee + ":\n" + wisperMsg);

					// [5.3]: Send to whisperee
					for (clientManager client_ : clients) {
						if (client_.nickname.equals(wisperee)) {
							sendToClient(client_.bw, "Wisper from " + nickname +
									":\n" + wisperMsg);
							break;
						}
					}

					// [4.1]: group messaging messaging
				} else {
					// [5.4] send back to this client
					sendToClient(bw, "You: " + msg);
					// [5.5] send to all other clients
					for (clientManager client_ : clients) {
						if (!client_.nickname.equals(nickname)) {
							sendToClient(client_.bw, nickname + ": " + msg);
						}
					}
				}
			}
		} catch (Exception e) {
			makeClose();
		}
		// System.out.println("lets just check something here????");
	}

	/*
	 * This is called to sent message to client
	 */
	public void sendToClient(BufferedWriter bw, String msg) {
		try {
			bw.write(msg);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			makeClose();
		}

	}

	/*
	 * Gets called when client disconnects
	 * Streems get closed and other clients are infomed of the client leaving;
	 */
	public void makeClose() {
		clients.remove(this);
		if (nickname != null) {
			System.out.println("Client left: " + nickname);
		}
		try {
			// [5.6] : Inform all other clients of this client leaving
			for (clientManager client_ : clients) {
				if (!client_.nickname.equals(nickname)) {
					sendToClient(client_.bw, "##Left" + nickname);
				}
			}
			if (br != null)
				br.close();
			if (bw != null)
				bw.close();
			if (s != null)
				s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
