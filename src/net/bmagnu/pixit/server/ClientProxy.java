package net.bmagnu.pixit.server;

import java.util.Set;

import net.bmagnu.pixit.common.GameState;
import net.bmagnu.pixit.server.Server.Connection;

public class ClientProxy {
	
	Connection client;
	
	public ClientProxy(Connection client) {
		this.client = client;
	}
	
	public void notifyImages(Set<Integer> images) {
		
	}
	
	public void notifyResults(int correctImage, int points) {
		
	}
	
	public void notifyTheme(String theme) {
		
	}
	
	public void notifyNewGamestate(GameState state) {
		
	}
}
