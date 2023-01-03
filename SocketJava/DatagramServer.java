package SocketJava;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class DatagramServer {

	private static int PORT = -1;

	public static void main(String[] args) {
		// Checking arguments
		if (args.length != 1) {
			System.out.println("Usage: java DatagramServer PORT"); 
			System.exit(1);
		}

		try {
			PORT = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.out.println("The following port is not a valid integer: "+ args[0]);
			System.exit(2);
		}

		if (PORT <= 1024 || PORT > 65535) {
			System.out.println("The server port is not valid: " + args[0]);
			System.exit(2);
		}

		// Init
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		byte[] buf = new byte[256];

		try {
			socket = new DatagramSocket(PORT);
			packet = new DatagramPacket(buf, buf.length);
			System.out.println("DatagramServer running on port: " + socket.getLocalPort()); 
		} catch (SocketException e) {
			System.out.println("Issues creating socket: ");
			e.printStackTrace();
			System.exit(1);
		}

		try {
			String request = null;
			String response = null;
			
			ByteArrayInputStream biStream = null;
			DataInputStream diStream = null;
			
			ByteArrayOutputStream boStream = null;
			DataOutputStream doStream = null;
			
			byte[] data = null;

			while (true) {
				System.out.println("\nWaiting requests...");

				// Receiving datagram
				try {
					packet.setData(buf);
					socket.receive(packet);
				} catch (IOException e) {
					System.err.println("Issues receiving datagram: "+ e.getMessage());
					e.printStackTrace();
					continue;
					// the server does not stop running
				}

				try {
					biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
					diStream = new DataInputStream(biStream);
					request = diStream.readUTF();
					System.out.println("Request from client: " + request);
				} catch (Exception e) {
					System.err.println("Issues reading request: ");
					e.printStackTrace();
					continue;
					// the server does not stop running
				}

				// Prepare response (use your logic)
				response = "[Response]";
				System.out.println("Response to client: " + response);

				// Send response
				try {
					boStream = new ByteArrayOutputStream();
					doStream = new DataOutputStream(boStream);
					doStream.writeUTF(response);
					data = boStream.toByteArray();
					packet.setData(data, 0, data.length);
					socket.send(packet);
				} catch (IOException e) {
					System.err.println("Issues sending response: "+ e.getMessage());
					e.printStackTrace();
					continue;
					// the server does not stop running
				}
			} // while
		}
		// Catching all other exceptions
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("DatagramServer: stop running...");
		socket.close();
	}
}
