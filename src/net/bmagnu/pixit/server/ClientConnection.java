package net.bmagnu.pixit.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
				try {
					System.out.println("Got message from " + socket.getInetAddress());
					System.out.println(lineIn);
					
					JSONParser parser = new JSONParser();
					JSONObject jsonIn = (JSONObject) parser.parse(lineIn);
					JSONObject jsonOut = client.handleRecieveMessage(jsonIn);
					send(jsonOut.toJSONString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}