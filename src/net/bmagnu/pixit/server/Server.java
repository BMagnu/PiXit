package net.bmagnu.pixit.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import net.bmagnu.pixit.common.Settings;

public class Server {

	public BlockingDeque<Runnable> execute = new LinkedBlockingDeque<>();
	
	public volatile boolean isRunning = true;
	
	public static Server instance;
	
	private GameServer server;
	
	private ServerSocket socket;
	
	private List<ClientConnection> clients;
	
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
					ClientConnection client = new ClientConnection(socket.accept(), server);
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
	
	
	


	public static void addToQueue(Runnable runnable) {
		instance.execute.addLast(runnable);
	}
}