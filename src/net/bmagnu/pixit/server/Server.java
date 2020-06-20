package net.bmagnu.pixit.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.dosse.upnp.UPnP;

import net.bmagnu.pixit.common.PiXitImage;

public class Server {

	public BlockingDeque<Runnable> execute = new LinkedBlockingDeque<>();
	
	public static volatile boolean isRunning = true;
	public static volatile boolean shouldRestart = false;
	
	public static Server instance;
	
	private GameServer server;
	
	private ServerSocket socket;
	
	private List<ClientConnection> clients;
	
	public static void main(String[] args) throws InterruptedException, IOException {
		instance = new Server();
		do {
			shouldRestart = false;
			instance.run(args.length > 0 ? args[0] : "./sample/");
		} while (shouldRestart);
	}
	
	public void run(String imgPath) throws InterruptedException, IOException {
		server = new GameServer();
		System.out.println("GameServer started!");
		socket = new ServerSocket(Settings.PORT_SERVER);
		if(Settings.OPEN_PORT_UPNP) {
			boolean portOpen = UPnP.openPortTCP(Settings.PORT_SERVER, "PiXit Game");
			System.out.println(portOpen ? "Opened Port " + Settings.PORT_SERVER + " via UPnP" : "Opening of Port " + Settings.PORT_SERVER + " via UPnP failed");
		}
			
		clients = new ArrayList<>();
		
		initImages(imgPath);

		printIPAddr();
		
		Thread socketAcceptor = new Thread(() -> {
			System.out.println("ServerSocket started!");
			while(isRunning) {
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
		
		Thread consoleReader = new Thread(() -> {
			Scanner scan = new Scanner(System.in);
			DebugHandler handler = new DebugHandler(server, System.out);
			try {
				while(isRunning) {
					String line = scan.nextLine();
					handler.handleRecieveMessage(line);
				}
			} catch (Exception e) {}
			finally {
				try {
				scan.close();
				} catch (Exception e1) { }
			}

		});
		consoleReader.start();
		
		while(isRunning) {
			Runnable task = null;
			while((task = execute.pollFirst(1, TimeUnit.SECONDS)) != null) {
				Thread.sleep(100);
				task.run();
			}
		}
		
		socket.close();
		socketAcceptor.join();
		
		for(ClientConnection client : clients)
			client.close();
	}

	private void printIPAddr() throws SocketException {
		
		System.out.println("Local IPs:");
		
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while(networkInterfaces.hasMoreElements())
		{
		    NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
		    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
		    while (inetAddresses.hasMoreElements())
		    {
		        InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
		        if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress())
		        System.out.println(inetAddress.getHostAddress());
		    }
		}

		System.out.println();
		
		BufferedReader ipProviderData = null;
		try {
			URL ipProviderURL = new URL("http://checkip.amazonaws.com");
        
            ipProviderData = new BufferedReader(new InputStreamReader(ipProviderURL.openStream()));
            String ip = ipProviderData.readLine();
            System.out.println("Global IP:");
            System.out.println(ip);
            System.out.println();
        } catch (IOException e) {
			System.out.println("Couldn't Query Global IP");
		} finally {
            if (ipProviderData != null) {
                try {
                    ipProviderData.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	private void initImages(String path) throws IOException {
		if (path.endsWith("\"")) // Weird shell behaviour passing the trailing " to java sometimes
			path = path.substring(0, path.length() - 1);
		
		Files.walk(Paths.get(path)).filter((file) -> {
			String mime;
			try {
				mime = Files.probeContentType(file);
				return mime != null && mime.split("/")[0].equals("image");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}).forEachOrdered((file) -> {
			int imageCount = server.images.size();
			
			try {
				PiXitImage image = new PiXitImage(PiXitImage.makeHash(file.toFile()), imageCount);
				
				server.imagesById.put(imageCount, image);
				server.freeImages.add(image);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			server.images.put(imageCount, file.toFile().getAbsolutePath());
		});
		
		for (Entry<Integer, String> file : server.images.entrySet()) {
			System.out.println("Image " + file.getKey() + ": " + file.getValue());
			System.out.println("With Hash " + server.imagesById.get(file.getKey()).hash);
			System.out.println();
		}
	}

	public static void addToQueue(Runnable runnable) {
		instance.execute.addLast(runnable);
	}
}