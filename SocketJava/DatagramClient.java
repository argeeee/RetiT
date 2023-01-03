package SocketJava;

import java.io.*;
import java.net.*;

public class DatagramClient {
	public static void main(String[] args) {
		InetAddress addr = null;
		int serverPort = -1;

		// Check args
		try {
			if (args.length == 2) {
				addr = InetAddress.getByName(args[0]);
				serverPort = Integer.parseInt(args[1]);
				System.out.println("Server" + args[0] + ":" + serverPort);
			} else {
				System.out.println("Usage: java DatagramClient ipServer serverPort");
				System.exit(1);
			}
		}
		catch (Exception e) {
			System.out.println("Issues:");
			e.printStackTrace();
			System.exit(1);
		}

		// Init
		DatagramSocket datagramSocket = null;
		DatagramPacket datagramPacket = null;
		byte[] buf = new byte[256];
		try {
			datagramSocket = new DatagramSocket();
			datagramSocket.setSoTimeout(30000);
			datagramPacket = new DatagramPacket(buf, buf.length, addr, serverPort);
			System.out.println("\nDatagramClient: started");
			System.out.println("Socket created: " + datagramSocket);
		}
		catch (SocketException e) {
			System.out.println("Error on socket creation: ");
			e.printStackTrace();
			System.out.println("DatagramClient: stop running...");
			System.exit(1);
		}

		ByteArrayOutputStream boStream = null;
		DataOutputStream doStream = null;
		
		ByteArrayInputStream biStream = null;
		DataInputStream diStream = null;
		byte[] data = null;

		String request = "[Request]";
		String response = null;
		
		// BASIC Example
		/*
		try {
			// Send (empty) request
			try {
				boStream = new ByteArrayOutputStream();
				doStream = new DataOutputStream(boStream);
				doStream.writeUTF(request);
				data = boStream.toByteArray();
				datagramPacket.setData(data);
				datagramSocket.send(datagramPacket);
				System.out.println("Request sent to: " + addr + ":" + serverPort);
			}
			catch (IOException e) {
				System.out.println("Error sending request: ");
				e.printStackTrace();
			}

			// Receiving
			try {
				datagramPacket.setData(buf);
				datagramSocket.receive(datagramPacket);
				/// Docs:
				/// sospensiva solo per i millisecondi indicati, dopo solleva una SocketException
			}
			catch (IOException e) {
				System.out.println("Error receiving response: ");
				e.printStackTrace();
			}

			// Extracting response
			try {
				biStream = new ByteArrayInputStream(datagramPacket.getData(), 0, datagramPacket.getLength());
				diStream = new DataInputStream(biStream);
				response = diStream.readUTF();
				System.out.println("response: " + response);
			}
			catch (IOException e) {
				System.out.println("error while reading response: ");
				e.printStackTrace();
			}

			// Put your logic here 
			
			// use this to close socket if server send us an error
			// datagramSocket.close();
			// System.exit(4);
			

			// end logic
		}
		catch (Exception e) {
			System.out.println("Error while receiving response from server: quitting...");
			/// Advice:
			/// si potrebbe gestire altrimenti l'eccezione, ad esempio tentando nuovamente
			e.printStackTrace();
			System.exit(5);
		}
		*/

		try {
			// Init
			boStream = new ByteArrayOutputStream();
			doStream = new DataOutputStream(boStream);
			buf = new byte[256];
			datagramPacket = new DatagramPacket(buf, buf.length, addr, serverPort);

			// To read from input use this
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Insert data or Ctrl+D(Unix)/Ctrl+Z(Win)+enter to quit:");
			// Process input
			String input = null;
			while ((input = stdIn.readLine()) != null) {
				// Create request message
				request = input;

				// Send request
				try {					
					doStream.writeUTF(request);
					data = boStream.toByteArray();
					datagramPacket.setData(data);
					datagramSocket.send(datagramPacket);
					System.out.println("Request sent to " + addr + ":" + serverPort);
				} catch (IOException e) {
					System.out.println("Error while sending request to server: ");
					e.printStackTrace();

					// Continue cycle of reading and sending
					System.out.println("Insert data or Ctrl+D(Unix)/Ctrl+Z(Win)+enter to quit:");
					continue;
				}

				// set buffer and receive answer
				try {
					datagramPacket.setData(buf);
					datagramSocket.receive(datagramPacket);
					/// sospensiva solo per i millisecondi indicati, dopo solleva una SocketException
				} catch (IOException e) {
					System.out.println("Error receiving datagram: ");
					e.printStackTrace();
					
					// Continue cycle of reading and sending
					System.out.println("Insert data or Ctrl+D(Unix)/Ctrl+Z(Win)+enter to quit:");
					continue;
				}
				try {
					biStream = new ByteArrayInputStream(datagramPacket.getData(), 0, datagramPacket.getLength());
					diStream = new DataInputStream(biStream);
					response = diStream.readUTF();
					System.out.println("Response: " + response);
					
				} catch (IOException e) {
					System.out.println("Error reading response: ");
					e.printStackTrace();
					
					// Continue cycle of reading and sending
					System.out.println("Insert data or Ctrl+D(Unix)/Ctrl+Z(Win)+enter to quit:");
					continue;
				}

				// Ok: sending new request
				System.out.println("Insert data or Ctrl+D(Unix)/Ctrl+Z(Win)+enter to quit:");
			} // while cycle of reading from input
		}
		// Catching all other exceptions
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\nDatagramClient: stop running...");
		datagramSocket.close();
	}
}
