package net.bmagnu.pixit.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.activation.MimetypesFileTypeMap;

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
		instance.run(args.length > 1 ? args[1] : "./sample/");
	}
	
	public void run(String imgPath) throws InterruptedException, IOException {
		server = new GameServer();
		System.out.println("GameServer started!");
		socket = new ServerSocket(Settings.PORT_SERVER);
		clients = new ArrayList<>();
		
		initImages(imgPath);
		
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
	
	private void initImages(String path) throws IOException {
	
		Files.walk(Paths.get(path)).filter((file) -> {
			String mime = new MimetypesFileTypeMap().getContentType(file.toFile());
			return mime.split("/")[0].equals("image");
		}).forEachOrdered((file) -> {
			int imageCount = server.images.size();
			server.freeImages.add(imageCount);
			server.images.put(imageCount, file.toFile().getAbsolutePath());
		});
		
		for(String file : server.images.values()) {
			System.out.println("Image: " + file);
		}
	}

	public static void addToQueue(Runnable runnable) {
		instance.execute.addLast(runnable);
	}
}