package net.bmagnu.pixit.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientConnection extends Thread {
	private Socket socket;
	
	private PrintWriter out;
	
	private GameServer server;
	
	public ClientConnection(Socket socket, GameServer server) {
		this.socket = socket;
		this.server = server;
	}
	
	public synchronized void send(String toSend) {
		System.out.println("Sent message to " + socket.getInetAddress());
		//System.out.println(toSend);
		out.println(toSend);
	}
	
	@Override
	public void run() {
		try {
			System.out.println("New Connection by " + socket.getInetAddress());
			
			out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			ClientHandler client = new ClientHandler(server, this);
			
			String lineIn;
			
			while((lineIn = in.readLine()) != null) {
				System.out.println("Got message from " + socket.getInetAddress());
				System.out.println(lineIn);
				
				JsonObject jsonIn = (JsonObject) JsonParser.parseString(lineIn);
				JsonObject jsonOut = client.handleRecieveMessage(jsonIn);
				send(jsonOut.toString());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}