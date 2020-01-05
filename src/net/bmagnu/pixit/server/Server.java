package net.bmagnu.pixit.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.bmagnu.pixit.common.Settings;

public class Server {

	public BlockingDeque<Runnable> execute = new LinkedBlockingDeque<>();
	
	public volatile boolean isRunning = true;
	
	public static Server instance;
	
	private GameServer server;
	
	private ServerSocket socket;
	
	private List<Connection> clients;
	
	public static void main(String[] args) throws InterruptedException, IOException {
		instance = new Server();
		instance.run();
	}
	
	public void run() throws InterruptedException, IOException {
		server = new GameServer();
		System.out.println("GameServer started!");
		socket = new ServerSocket(Settings.PORT_SERVER);
		clients = new ArrayList<>();
		
		Thread socketAcceptor = new Thread(() -> {
			System.out.println("ServerSocket started!");
			while(true) {
				try {
					Connection client = new Connection(socket.accept());
					clients.add(client);
					client.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		socketAcceptor.start();
		
		while(isRunning) {
			Runnable task = null;
			while((task = execute.pollFirst(1, TimeUnit.SECONDS)) != null) {
				Thread.sleep(10);
				task.run();
			}
		}
	}
	
	
	public class Connection extends Thread {
		private Socket socket;
		
		private PrintWriter out;
		
		public Connection(Socket socket) {
			this.socket = socket;
		}
		
		public synchronized void send(String toSend) {
			System.out.println("Sent message to " + socket.getInetAddress());
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


	public static void addToQueue(Runnable runnable) {
		instance.execute.addLast(runnable);
	}
}