package SocketJava;

import java.net.*;
import java.io.*;

public class Client {
	public static void main(String[] args) throws IOException {
		InetAddress addr = null;
		int port = -1;

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String folderName = null;
		int bufferSize = 4096;

		try {
			if (args.length == 2) {
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
				if (port < 1024 || port > 65535) {
					System.out.println("Usage: java Client serverAddr serverPort");
					System.exit(1);
				}
			} else {
				System.out.println("Usage: java Client serverAddr serverPort");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("error: ");
			e.printStackTrace();
			System.out.println("Usage: java Client serverAddr serverPort");
			System.exit(2);
		}

		// Variables for reading and transferring files
		byte[] buffer = new byte[bufferSize];
		int count = 0;
		int readBytes = 0;
		long toTransfer = 0;
		DataInputStream srcStream = null;

		// Variables for sockets
		Socket socket = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;

		// Socket creation
		try {
			socket = new Socket(addr, port);
			socket.setSoTimeout(30000);
			System.out.println("Socket created: " + socket);
			inSock = new DataInputStream(socket.getInputStream());
			outSock = new DataOutputStream(socket.getOutputStream());
		} catch (IOException ioe) {
			System.out.println("error while creating stream on socket: ");
			ioe.printStackTrace();
			System.exit(1);
		}

		System.out.print("\n^D(Unix)/^Z(Win)+enter to quit, otherwise insert folder name: ");

		while ((folderName = stdIn.readLine()) != null) {
			File dirCorr = new File(folderName);
			if (dirCorr.exists() && dirCorr.isDirectory()) {
				File[] files = dirCorr.listFiles();
				for (int i = 0; i < files.length; i++) {
					File fileCorr = files[i];
					if (fileCorr.isFile() /*  && minDim <= fileCorr.length() */) {
						System.out.println("file: " + fileCorr.getName());
						outSock.writeUTF(fileCorr.getName());
						String result = inSock.readUTF();
						if (result.equals("[activate]")) {
							System.out.println("File " + fileCorr.getName() + " is NOT already in server: transfering...");
							toTransfer = fileCorr.length();
							outSock.writeLong(toTransfer);
							// transfering file
							srcStream = new DataInputStream(new FileInputStream(fileCorr.getAbsolutePath()));
							// cycle of reading from source and writing on destination
							try {
								// quitting from cycle when ended bytes
								count = 0;
								while (count < toTransfer) {
									readBytes = srcStream.read(buffer);
									outSock.write(buffer, 0, readBytes);
									count += readBytes;
								}
								outSock.flush();
								System.out.println("Transfered bytes: " + count);
							}
							// exception can happen only if expected bytes are differend from readed bytes
							catch (EOFException e) {
								System.out.println("error: ");
								e.printStackTrace();
							}
						} else if (result.equals("[skip]"))
							System.out.println("File " + fileCorr.getName()
									+ " is already on server");
						else {
							System.out.println("Client: protocoll error...");
							System.exit(4);
						}
					} else { // if file is file (not folder)
						System.out.println("Skipped file");
					}
				}
			} else {
				System.out.println("Error: the specified directory either does not exist or is not a directory");
			}

			System.out.print("\n^D(Unix)/^Z(Win)+enter to quit, otherwise insert folder name: ");
		} // while 

	}
}
