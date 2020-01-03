package net.bmagnu.pixit.server;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class Server {

	public static Deque<Runnable> execute = new LinkedBlockingDeque<>();
	
	public static volatile boolean isRunning = true;
	
	public static void main(String[] args) throws InterruptedException {
		GameServer server = new GameServer();
		
		while(isRunning) {
			Thread.sleep(1000);
			
			Runnable task = null;
			while((task = execute.pollFirst()) != null) {
				task.run();
			}
		}
	}
	
}