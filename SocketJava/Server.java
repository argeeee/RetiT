package SocketJava;

import java.io.*;
import java.net.*;

/*
 * 
 * Multi thread
 */
public class Server {
	public static void main(String[] args) throws IOException {
		int port = -1;

		try {
			if (args.length == 1) {
				port = Integer.parseInt(args[0]);
				// countrollo che la porta sia nel range consentito 1024-65535
				if (port < 1024 || port > 65535) {
					System.out.println("Usage: java Server [serverPort>1024]");
					System.exit(1);
				}
			} else {
				System.out.println("Usage: java Server port");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("error: ");
			e.printStackTrace();
			System.out.println("Usage: java Server port");
			System.exit(1);
		}

		ServerSocket serverSocket = null;
		Socket clientSocket = null;

		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
			System.out.println("Server: started ");
			System.out.println("Server: socket created: " + serverSocket);
		} catch (Exception e) {
			System.err.println("Server: error while creating socket: " + e.getMessage());
			e.printStackTrace();
			serverSocket.close();
			System.exit(1);
		}

		try {
			while (true) {
				System.out.println("Server: waiting requests...\n");

				try {
					clientSocket = serverSocket.accept(); // bloccante!!!
					System.out.println("Server: accepted connection: " + clientSocket);
				} catch (Exception e) {
					System.err.println("Server: error while accepting connection: " + e.getMessage());
					e.printStackTrace();
					continue;
				}

				try {
					new ServerThread(clientSocket).start();
				} catch (Exception e) {
					System.err.println("Server: error on ServerThread: " + e.getMessage());
					e.printStackTrace();
					continue;
				}
			} // while true
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Server: stop running...");
			System.exit(2);
		}
	}
}

class ServerThread extends Thread {
	private Socket clientSocket = null;
	private int bufferSize = 4096;

	public ServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		System.out.println("Thread started: " + Thread.currentThread().getName());

		DataInputStream inSock;
		DataOutputStream outSock;

		byte[] buffer = new byte[bufferSize];
		int count = 0;
		int readBytes = 0;
		DataOutputStream destStream = null;
		
		// Init
		try {
			inSock = new DataInputStream(clientSocket.getInputStream());
			outSock = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("error in creation of input/output stream on socket: ");
			e.printStackTrace();
			return;
		}

		try {
			try {
				String receivedFileName;
				long numeroByte;
				File fileCorr;
				FileOutputStream outFileCorr;

				while ((receivedFileName = inSock.readUTF()) != null) {
					fileCorr = new File(receivedFileName);
					if (fileCorr.exists()) {
						outSock.writeUTF("[skip]");
					} else {
						outSock.writeUTF("[activate]");
						numeroByte = inSock.readLong();
						System.out.println("Writing file " + receivedFileName + " of " + numeroByte + " bytes");
						outFileCorr = new FileOutputStream(receivedFileName);

						// Ricevo il file (in linea)
						destStream = new DataOutputStream(outFileCorr);
						count = 0;
						try {
							// esco dal ciclo quando ho letto il numero di byte da trasferire
							while (count < numeroByte) {
								readBytes = inSock.read(buffer);
								destStream.write(buffer, 0, readBytes);
								count += readBytes;
							}
							destStream.flush();
							System.out.println("transfered: " + count);
						}
						// l'eccezione dovrebbe scattare solo se ci aspettiamo un numero sbagliato di
						// byte da leggere
						catch (EOFException e) {
							System.out.println("error: ");
							e.printStackTrace();
						}

						outFileCorr.close();
					}
				} // while
			} catch (EOFException eof) {
				System.out.println("Reached end of file, quitting...");
				clientSocket.close();
				System.out.println("ServerThread: stop running...");
				System.exit(0);
			} catch (SocketTimeoutException ste) {
				System.out.println("Timeout triggered: ");
				ste.printStackTrace();
				clientSocket.close();
				System.exit(1);
			} catch (Exception e) {
				System.out.println("error: ");
				e.printStackTrace();
				System.out.println("Closing and quitting...");
				clientSocket.close();
				System.exit(2);
			}
		} catch (IOException ioe) {
			System.out.println("error while closing socket: ");
			ioe.printStackTrace();
			System.out.println("Closing and quitting...");
			System.exit(3);
		}
	}

} // ServerThread
