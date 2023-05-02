import java.net.*;
import java.io.*;
import java.util.*;

/*
 * 
 */
public class server {

	private ServerSocket ss;

	/**
	 * Initializes the server with the provided ServerSocket.
	 * 
	 * @param ss the ServerSocket to use for the server.
	 */
	public server(ServerSocket ss) {
		this.ss = ss;
	}

	/**
	 * The main method that starts the server on the specified port.
	 * 
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {
		ServerSocket serverSocket;
		while (true) {
			try {
				int port = Integer.parseInt(args[0]);
				serverSocket = new ServerSocket(port);
				System.out.println("Server started");
				break;
			} catch (Exception e) {
				System.out.println("Port not available or invalid port,\nRun with java server <port>");
				System.exit(0);
			}
		}
		server s = new server(serverSocket);
		s.startServerSocket();
	}

	/**
	 * Starts the server socket to listen for incoming client connections.
	 */
	public void startServerSocket() {
		try {
			while (!ss.isClosed()) {
				Socket s = ss.accept();
				System.out.println("A Client has joined");
				ClientManager cm = new ClientManager(s);
				Thread t = new Thread(cm);
				t.start();
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Ends the server socket.
	 */
	public void endServerSocket() {
		try {
			if (ss != null)
				ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

/*
 * 
 */
class ClientManager implements Runnable {

	public static ArrayList<ClientManager> clients = new ArrayList<>();
	private Socket s;
	private BufferedReader br;
	private BufferedWriter bw;
	private String nickname;

	/**
	 * Constructs a new instance of the ClientManager class using the provided
	 * Socket object.
	 * 
	 * @param s The Socket object representing the connection to the client
	 */
	public ClientManager(Socket s) {
		try {
			this.s = s;
			this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			this.bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			this.nickname = br.readLine();

			System.out.println("Client with nickname: " + nickname +
					", addrees: " + s.getInetAddress().getHostAddress() + ", port: " + s.getPort()
					+ " and local port:" + s.getLocalPort());

			Boolean taken = false;
			for (ClientManager client_ : clients) {
				if (client_.nickname.equals(nickname)) {
					taken = true;
					break;
				}
			}

			if (taken) {
				sendToClient(bw, "TAKEN");
				Thread.sleep(100);
				makeCloseUnsuccesful(s, br, bw);
			} else {
				String clientsString = "ONLINE_USERS";
				for (ClientManager client_ : clients) {
					clientsString = clientsString + "|" + client_.nickname;
				}
				sendToClient(bw, clientsString);

				System.out.println("Debugging");

				for (ClientManager client_ : clients) {
					sendToClient(client_.bw, "CLIENT_JOIN|" + nickname);
				}

				clients.add(this);

			}

		} catch (Exception e) {
			makeClose(s, br, bw);
		}
	}

	/**
	 * Runs the thread that manages the client's connection.
	 * Reads the data sent by the client and calls the method processCommand to
	 * handle the received message.
	 */
	@Override
	public void run() {
		String msg; // messaage received from a client
		while (s.isConnected()) {
			try {
				msg = br.readLine();
				System.out.println("Line from " + nickname + ": " + msg);
				processCommand(msg);
			} catch (Exception e) {
				makeClose(s, br, bw);
				break;
			}
		}
	}

	/**
	 * Processes the received command from the client by parsing the message and
	 * executing the corresponding action.
	 * 
	 * @param msg The message received from the client
	 */
	private void processCommand(String msg) {
		String[] parts = msg.split("\\|");
		String command = parts[0];
		switch (command) {
			case "MESSAGE":
				String message = parts[1];
				for (ClientManager client_ : clients) {
					if (!client_.nickname.equals(nickname)) {
						sendToClient(client_.bw, "MESSAGE|" + nickname + "|"
								+ message);
					}
				}
				break;
			case "WHISPER":
				String wisperee = parts[1];
				String wisper = parts[2];
				for (ClientManager client_ : clients) {
					if (client_.nickname.equals(wisperee)) {
						sendToClient(client_.bw, "WHISPER|" + nickname +
								"|" + wisper);
					}
				}
				break;
			case "SEARCH_REQUEST":
				String string = parts[1];
				string = string.replace(" ", "");
				System.out.println(nickname + " is searching for: " +
						string);
				for (ClientManager client_ : clients) {
					if (!client_.nickname.equals(nickname)) {
						sendToClient(client_.bw, "SEARCH_REQUEST|" + string + "|" +
								nickname);
					}
				}
				break;
			case "SEARCH_RESULTS":
				String searchResults = parts[1];
				String searcher = parts[2];
				for (ClientManager client_ : clients) {
					if (client_.nickname.equals(searcher)) {
						sendToClient(client_.bw, "SEARCH_RESULTS|" + searchResults + "|" + nickname);
					}
				}
				break;
			case "DOWNLOAD_REQUEST":
				String downloadee = parts[1];
				for (ClientManager client_ : clients) {
					if (client_.nickname.equals(downloadee)) {
						sendToClient(client_.bw, "DOWNLOAD_REQUEST|" +
								nickname + "|" + parts[2] + "|" + parts[3] + "|" +
								s.getInetAddress().getHostName() + "|" + parts[4]);
					}
				}
				break;
			case "DOWNLOAD_RESPONSE":
				downloadee = parts[1];
				String response = parts[2];
				for (ClientManager client_ : clients) {
					if (client_.nickname.equals(downloadee)) {
						sendToClient(client_.bw, "DOWNLOAD_RESPONSE|" + nickname
								+ "|" + response);
					}
				}
				break;
			default:
				System.out.println("Unknown command: " + command);

				break;

		}
	}

	/**
	 * 
	 * Sends a message to the client using the provided BufferedWriter
	 * 
	 * @param bWriter The BufferedWriter used to send the message to the client
	 * @param message The message to be sent to the client
	 */
	public void sendToClient(BufferedWriter bWriter, String message) {
		System.out.println("Line to client");
		try {
			bWriter.write(message);
			bWriter.newLine();
			bWriter.flush();
		} catch (Exception e) {
			makeClose(s, br, bw);
		}
	}

	/**
	 * This method is responsible for handling the closing of a client connection.
	 * It removes the current client from the list of clients, notifies the
	 * remaining clients
	 * 
	 * @param s  the socket associated with the client connection
	 * @param br the BufferedReader associated with the client connection
	 * @param bw the BufferedWriter associated with the client connection
	 */
	public void makeClose(Socket s, BufferedReader br, BufferedWriter bw) {
		clients.remove(this);
		System.out.println("A Client has left");
		for (ClientManager client_ : clients) {
			try {
				if (!client_.nickname.equals(nickname))
					sendToClient(client_.bw, "CLIENT_LEFT|" + nickname);
			} catch (Exception e) {
				makeClose(s, br, bw);
			}
		}

		try {
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

	/**
	 * This method is responsible for handling the closing of a client connection.
	 * 
	 * @param s  the socket associated with the client connection
	 * @param br the BufferedReader associated with the client connection
	 * @param bw the BufferedWriter associated with the client connection
	 */
	public void makeCloseUnsuccesful(Socket s, BufferedReader br, BufferedWriter bw) {
		clients.remove(this);
		System.out.println("Client failed to join");
		try {
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
