package net.bmagnu.pixit.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.application.Platform;

public class ServerConnection extends Thread {
	
	private String serverIp;
	private int port;
	private volatile boolean shouldClose = false;
	private volatile boolean requireResponse = false;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private ServerHandler handler = new ServerHandler();
	public int playerId = -1;
	
	public BlockingDeque<String> messages = new LinkedBlockingDeque<>();
	
	public ServerConnection(String serverIp, int port) {
		this.serverIp = serverIp;
		this.port = port;
	}
	
	public void shutdownSocket() {
		shouldClose = true;
		try {
			if(clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void send(String toSend) {
		System.out.println("Sent message to Server");
		out.println(toSend);
	}
	
	public synchronized JSONObject sendWaitForResponse(String toSend) throws InterruptedException, ParseException {
		requireResponse = true;
		send(toSend);
		String response = messages.takeFirst();
		System.out.println("Got Response");
		requireResponse = false;
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(response);
	}
	
	private void handleMessages() {
		String message;
		while((message = messages.pollFirst()) != null) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject jsonIn = (JSONObject) parser.parse(message);
				handler.handleRecieveMessage(jsonIn);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		try {
			clientSocket = new Socket(serverIp, port);
			
			System.out.println("Socket Open");
			
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String lineIn;
			while(!shouldClose && (lineIn = in.readLine()) != null) {
				System.out.println("Got message from Server");
				
				messages.putLast(lineIn);
				
				if(!requireResponse)
					Platform.runLater(() -> handleMessages());
			}
			
			in.close();
	        out.close();
	        clientSocket.close();
		} catch (IOException | InterruptedException e) {
			if(!shouldClose)
				e.printStackTrace();
			else {
				System.out.println("Shut down Socket");
			}
		}
	}
}