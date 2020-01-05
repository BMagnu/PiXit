package net.bmagnu.pixit.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.bmagnu.pixit.common.Settings;

public class Client extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 805316169294862575L;

	private Connection connection;
	
	public ServerProxy proxy;
	
	public static Client instance;
	
	public static void main(String[] args) {
		JFrame window = new Client(args.length > 1 ? args[1] : "127.0.0.1");
		window.setVisible(true);
	}
	
	public Client(String serverIp) {
		instance = this;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					connection.shutdownSocket();
					connection.join();
					System.out.println("Shut down Socket");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			    System.exit(0);
			}
		});
		setSize(400,500);
		setLayout(null);
		setTitle("PiXit");
		
		connection = new Connection(serverIp);
		proxy = new ServerProxy(connection);
		
		connection.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Registering at Server...");
		connection.playerId = proxy.registerPlayer();
		System.out.println("Player Id: " + connection.playerId);
		
	}

	public class Connection extends Thread {
	
		private String serverIp;
		private volatile boolean shouldClose = false;
		private volatile boolean requireResponse = false;
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		private ServerHandler handler = new ServerHandler();
		public int playerId = -1;
		
		public BlockingDeque<String> messages = new LinkedBlockingDeque<>();
		
		public Connection(String serverIp) {
			this.serverIp = serverIp;
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
				clientSocket = new Socket(serverIp, Settings.PORT_SERVER);
				
				System.out.println("Socket Open");
				
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				String lineIn;
				while(!shouldClose && (lineIn = in.readLine()) != null) {
					System.out.println("Got message from Server");
					
					messages.putLast(lineIn);
					
					if(!requireResponse)
						handleMessages();
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
}
