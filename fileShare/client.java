import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
//
//POSSIBLE BONUS FEATURES:
//-chat room with whisper
//-allow multiple users to send and receive at the same time

//NEED TO CHECK:
//-client can leave while sending/receiveing
public class client extends JFrame implements ActionListener {

	public volatile ArrayList<String> clients = new ArrayList<>();

	private Socket s = null;
	private BufferedReader br = null;
	private BufferedWriter bw = null;
	private String nickname;
	public volatile JTextArea list;
	public static volatile JTextArea fileList;
	private JButton button;
	private JButton whisperButton;
	private JButton searchButton;
	private JButton downloadButton;
	private JButton pauseButton;
	private JTextField input;
	public volatile JTextArea output;
	public static volatile JProgressBar bar1;
	private static JProgressBar bar2;
	private static String serverAddress;
	private static int serverPort;
	private static int receiverPort;
	private static String randomKey;
	public static volatile Boolean receiveing = false;
	public static volatile Boolean pause = false;
	public static volatile int valueprogress;
	public JScrollPane scrollPane;
	public JScrollPane scrollPane2;
	public JScrollPane scrollPane3;
	private static DatagramSocket ds;

	public static void main(String[] args) {

		try {
			serverAddress = args[0];
			serverPort = Integer.parseInt(args[1]);
			receiverPort = Integer.parseInt(args[2]);
		} catch (Exception e) {
			System.out.println("Client failed!\nPlease run client with:\n" +
					"java client <server address> <server port> <client port>, see README.md for more information");
			System.exit(0);
		}

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter a unique username to enter the chat: ");
		String nickname = sc.nextLine();
		sc.close();

		try {
			Socket s = new Socket(serverAddress, serverPort);
			client c = new client(s, nickname);
			c.messagesFromServer();
			c.sendNickname();
			c.receiver();
		} catch (Exception e) {
			System.out.println("Server is not online");
			System.exit(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == button) {
				if (input.getText().equals(""))
					JOptionPane.showMessageDialog(this,
							"Input text is empty!", "Error!",
							JOptionPane.ERROR_MESSAGE);
				else {
					sendToServer("MESSAGE|" + input.getText());
					output.append("You: " + input.getText() + "\n");
					input.setText("");
				}
			} else if (e.getSource() == whisperButton) {
				if (input.getText().equals(""))
					JOptionPane.showMessageDialog(this,
							"Input text is empty!", "Error!",
							JOptionPane.ERROR_MESSAGE);
				else {
					String whisperee;
					while (true) {
						whisperee = JOptionPane.showInputDialog(this,
								"Enter the nickname you want to" +
										" whisper to.",
								"Whisper",
								JOptionPane.INFORMATION_MESSAGE);
						if (clients.contains(whisperee) || whisperee.equals(
								null))
							break;
						else
							JOptionPane.showMessageDialog(this,
									"The nickname you entered does " +
											"not exits.",
									whisperee + " does not exist",
									JOptionPane.ERROR_MESSAGE);
					}
					sendToServer("WHISPER|" + whisperee + "|" + input.getText());
					output.append("You to " + whisperee + ": " + input.getText() + "\n");
					input.setText("");
				}
			} else if (e.getSource() == searchButton) {
				String search = JOptionPane.showInputDialog(null,
						"Enter the name of the file you want " +
								"to search for or part of the name",
						"File Search", JOptionPane.INFORMATION_MESSAGE);
				sendToServer("SEARCH_REQUEST|" + search);
			} else if (e.getSource() == downloadButton) {
				String downloadFileName = JOptionPane.showInputDialog(this,
						"Enter the full name of the file(including extension) " +
								"you want to download",
						"File Download",
						JOptionPane.INFORMATION_MESSAGE);
				String targetNickname = JOptionPane.showInputDialog(this,
						"Enter the username of the client you want to " +
								"download from.",
						"File Download",
						JOptionPane.INFORMATION_MESSAGE);
				if (clients.contains(targetNickname)) {
					randomKey = generateKey();
					Thread.sleep(100);
					sendToServer("DOWNLOAD_REQUEST|" + targetNickname + "|" + downloadFileName
							+ "|" + randomKey + "|" + receiverPort);
				} else {
					JOptionPane.showMessageDialog(this,
							"The username you entered does not exits.",
							targetNickname + " does not exist",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				if (pause) {
					pause = false;
					pauseButton.setText("Pause");
				} else {
					pause = true;
					pauseButton.setText("Resume");
				}
			}
		} catch (Exception ex) {
			System.out.println("Server dsconnected");
			makeClose(s, br, bw);
		}
	}

	private void sendToServer(String message) {

		System.out.println("Line to server: " + message);
		try {
			bw.write(message);
			bw.newLine();
			bw.flush();
		} catch (Exception ex) {
			makeClose(s, br, bw);
		}
	}

	public client(Socket s, String nickname) {
		try {

			this.s = s;
			this.br = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			this.bw = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			this.nickname = nickname;

		} catch (IOException e) {
			makeClose(s, br, bw);
		}

		JFrame frame = new JFrame(nickname);
		frame.setResizable(false);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(0, 0, 500, 600);
		frame.getContentPane().setBackground(
				new java.awt.Color(94, 206, 244));
		Container container = frame.getContentPane();
		container.setLayout(null);
		list = new JTextArea();
		list.setBounds(10, 10, 150, 100);
		list.setLineWrap(true);
		list.setWrapStyleWord(true);
		list.setEditable(false);
		fileList = new JTextArea();
		fileList.setEditable(false);
		input = new JTextField();
		input.setBounds(30, 500, 230, 30);
		input.setText("");
		input.setEditable(true);
		button = new JButton("Send");
		button.setBounds(270, 500, 80, 30);
		button.addActionListener(this);
		whisperButton = new JButton("Whisper");
		whisperButton.setBounds(370, 500, 100, 30);
		whisperButton.addActionListener(this);
		output = new JTextArea();
		scrollPane = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 10, 150, 100);
		scrollPane2 = new JScrollPane(fileList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane2.setBounds(200, 10, 250, 100);
		scrollPane3 = new JScrollPane(output,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane3.setBounds(30, 330, 410, 150);
		output.setEditable(false);
		output.setLineWrap(true);
		output.setWrapStyleWord(true);
		downloadButton = new JButton("Download");
		downloadButton.setBounds(300, 120, 110, 30);
		downloadButton.addActionListener(this);
		bar1 = new JProgressBar();
		bar1.setBounds(30, 180, 400, 30);
		bar1.setMaximum(100);
		bar1.setMinimum(0);
		bar1.setStringPainted(true);
		bar2 = new JProgressBar();
		bar2.setBounds(30, 240, 400, 30);
		bar2.setMaximum(100);
		bar2.setMinimum(0);
		bar2.setStringPainted(true);
		pauseButton = new JButton("Pause");
		pauseButton.setBounds(175, 280, 95, 30);
		pauseButton.addActionListener(this);
		searchButton = new JButton("Search");
		searchButton.setBounds(50, 120, 95, 30);
		searchButton.addActionListener(this);
		Label downloadText = new Label("Downloading");
		Label uploadingText = new Label("Uploading");
		downloadText.setFont(getFont());
		uploadingText.setFont(getFont());
		downloadText.setBounds(195, 160, 100, 20);
		uploadingText.setBounds(200, 220, 100, 20);
		container.add(scrollPane);
		container.add(scrollPane2);
		container.add(scrollPane3);
		container.add(input);
		container.add(button);
		container.add(whisperButton);
		container.add(scrollPane);
		container.add(downloadButton);
		container.add(bar1);
		container.add(bar2);
		container.add(pauseButton);
		container.add(searchButton);
		container.add(downloadText);
		container.add(uploadingText);
		frame.setVisible(true);
	}

	public void sendNickname() throws Exception {
		try {
			bw.write(nickname);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			makeClose(s, br, bw);
		}
	}

	public void messagesFromServer() {
		new Thread(new Runnable() {
			public void run() {

				System.out.println("/==Active Users==");

				String msg; // message from server

				//
				while (s.isConnected()) {
					try {
						msg = br.readLine();
						System.out.println("Line fron server: " + msg);
						processCommand(msg);
						updateClientListGUI();
						updateFileList();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						makeClose(s, br, bw);
					}
				}

			}
		}).start();

	}

	private void processCommand(String msg) {
		String[] parts = new String[1];
		try {
			parts = msg.split("\\|");
		} catch (Exception e) {
			System.out.println("Server disconnected");
			makeClose(s, br, bw);
		}
		String command = parts[0];

		switch (command) {
			case "TAKEN":
				System.out.println("Username taken, try again with a different one");
				break;
			case "ONLINE_USERS":
				for (int i = 1; i < parts.length; i++) {
					clients.add(parts[i]);
					System.out.println(parts[i]);
				}
				break;
			case "MESSAGE":
				String sender = parts[1];
				String message = parts[2];
				output.append(sender + ": " + message + "\n");
				break;
			case "WHISPER":
				String whisperer = parts[1];
				String whisper = parts[2];
				output.append(whisperer + " to you: " + whisper + "\n");
				break;
			case "SEARCH_REQUEST":
				String search = parts[1];
				String searcher = parts[2];
				sendToServer("SEARCH_RESULTS|" + searchForFiles(search) + "|" + searcher);
				break;
			case "SEARCH_RESULTS":
				String searchResults = parts[1];
				String searchee = parts[2];
				if (searchResults.contains(";")) {
					output.append("Search results from " +
							searchee + ":\n");
					String[] searchResultList = searchResults.split(
							";");
					for (int i = 0; i < searchResultList.length; i++) {
						output.append(">" + searchResultList[i]
								+ "\n");
					}
				} else {
					output.append("Search result from " +
							searchee + ":\n>"
							+ searchResults + "\n");
				}
				break;
			case "DOWNLOAD_REQUEST":
				String downloader = parts[1];
				String fileName = parts[2];
				String keyString = parts[3];
				String recAddress = parts[4];
				int port = Integer.parseInt(parts[5]);

				if (searchForFiles(fileName).equals(fileName)) {
					int input = JOptionPane.showConfirmDialog(
							null, downloader +
									" would like to download "
									+ fileName + " from your" +
									" local files.",
							"Download request",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE,
							null);
					if (input == 0) {
						sender(fileName, keyString, recAddress, port);
					} else {
						sendToServer("DOWNLOAD_RESPONSE|" + downloader + "|d|" + fileName);
					}
				} else {
					sendToServer("DOWNLOAD_RESPONSE|" + downloader + "|n|" + fileName);
				}
				break;
			case "DOWNLOAD_RESPONSE":
				String username = parts[1];
				fileName = parts[2];
				if (parts[2].equals("n")) {
					JOptionPane.showMessageDialog(
							null, username +
									" does not have a file called" + fileName,
							"File does not exist",
							JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(
							null, username +
									" declined your request to download" + fileName,
							"Download Declined",
							JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "CLIENT_JOIN":
				username = parts[1];
				clients.add(username);
				output.append(username + " joined" + "\n");
				break;
			case "CLIENT_LEFT":
				username = parts[1];
				clients.remove(username);
				output.append(username + " left" + "\n");
				break;
			default:
				System.out.println("Unknown command: " + command);
				break;
		}
	}

	/**
	 * Closes the provided socket, BufferedReader, and BufferedWriter, if they are
	 * not null.
	 * If an IOException is caught, it is ignored.
	 * Exits the program with status code 0.
	 *
	 * @param s  The socket to close.
	 * @param br The BufferedReader to close.
	 * @param bw The BufferedWriter to close.
	 */
	public void makeClose(Socket s, BufferedReader br,
			BufferedWriter bw) {
		try {

			if (br != null)
				br.close();
			if (bw != null)
				bw.close();
			if (s != null)
				s.close();
		} catch (IOException e) {
		}
		System.exit(0);
	}

	/**
	 * Updates the GUI's client list with the current list of clients.
	 * Assumes that the client list GUI element is named "list".
	 */
	public void updateClientListGUI() {
		list.setText("Active Client List:\n");
		for (String client_ : clients) {
			list.append(client_ + "\n");
		}
	}

	/**
	 * Updates the GUI's file list with the current list of files in the "files"
	 * directory.
	 * Assumes that the file list GUI element is named "fileList".
	 */
	public static void updateFileList() {
		fileList.setText("Local files:\n");
		File dir = new File("files");
		File[] list = dir.listFiles();
		if (list != null) {
			for (File file_ : list) {
				fileList.append(file_.getName() + "\n");
			}
		}
	}

	/**
	 * Searches the "files" directory for files whose names contain the provided
	 * search string.
	 *
	 * @param search The string to search for in file names.
	 * @return A semicolon-separated string containing the names of files that match
	 *         the search string,
	 *         or "--" if no files match.
	 */
	public String searchForFiles(String search) {
		String string = "";
		File dir = new File("files");
		File[] list = dir.listFiles();
		if (list != null) {
			for (File file_ : list) {
				String fileName = file_.getName();
				fileName = fileName.toUpperCase();
				search = search.toUpperCase();
				if (fileName.contains(search)) {
					string = string + file_.getName() + ";";
				}
			}
			if (string.equals(""))
				string = "--;";
			string = string.substring(0, string.length() - 1);
		} else {
			string = "--";
		}

		return string;
	}

	/**
	 * Generates a random 6-character hexadecimal key.
	 *
	 * @return The generated key.
	 */
	public String generateKey() {
		String key = "";
		Random rn = new Random();
		for (int i = 0; i < 6; i++) {
			int n = rn.nextInt(255);
			key = key + String.format("%02x", n);
		}
		return key;
	}

	/*
	 * 
	 */
	public void receiver() {
		new Thread(() -> {
			try {
				System.out.println("Receiver is live on port: " + receiverPort);
				ds = new DatagramSocket(receiverPort);
				while (s.isConnected()) {
					byte[] fileNameSizeBytes = new byte[1024];
					DatagramPacket packet = new DatagramPacket(fileNameSizeBytes,
							fileNameSizeBytes.length);
					ds.receive(packet);
					byte[] data = packet.getData();
					String string = new String(data, 0, packet.getLength());
					String[] parts = string.split("#");
					String fileName = parts[0];
					int fileSize = Integer.parseInt(parts[1]);
					if (!parts[2].equals(randomKey)) {
						ds.close();
						return;
					}
					System.out.println("Receiving file size: " + fileSize);
					Float progressSize = 0.0F;
					progressSize = progressSize + 1021.0F;
					File f = new File("files/" + fileName); // Creating the file
					FileOutputStream fos = new FileOutputStream(f);
					boolean reachedEnd;
					int sqNumber = 0;
					int prevSqNumber = 0;

					while (true) {
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

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								bar1.setValue(prog);
								try {
									Thread.sleep(0);

								} catch (Exception e) {
								}
							}
						});
						while (pause) {
							try {
								Thread.sleep(100);
							} catch (Exception e) {

							}
						}

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
					updateFileList();
				}
				ds.close();
				updateFileList();
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(0);
			}

		}).start();

	}

	/* */
	public void sender(String fName, String key, String recAddress,
			int recPort) {
		new Thread(() -> {
			System.out.println("Sending to address: " + recAddress +
					" and port: " + recPort);
			try {
				DatagramSocket ds = new DatagramSocket();
				InetAddress address = InetAddress.getByName(recAddress);

				File f = new File("files/" + fName);
				byte[] fileBytes = new byte[(int) f.length()];
				int fileSize = fileBytes.length;
				String string = fName + "#" + Integer.toString(fileSize) + "#" +
						key;
				byte[] fileNameSizeBytes = string.getBytes();
				DatagramPacket pt = new DatagramPacket(fileNameSizeBytes,
						fileNameSizeBytes.length, address, recPort);
				ds.send(pt);

				if (key.equals("-")) {
					ds.close();
					return;
				}

				System.out.println("Sent file name: " + fName +
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
							message.length, address, recPort);
					ds.send(sendPacket); // Sending the data
					System.out.println("Sending packet: " + sqNumber);

					double total = Math.ceil(fileSize / 1024);
					int prog = (int) Math.floor(sqNumber / total * 100);
					System.out.println("this value " + prog);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							bar2.setValue(prog);
							try {
								Thread.sleep(0);

							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					});
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
}