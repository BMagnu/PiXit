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

	private ServerConnection connection;
	
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
		
		connection = new ServerConnection(serverIp);
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
}
