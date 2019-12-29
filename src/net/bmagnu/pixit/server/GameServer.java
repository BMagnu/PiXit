package net.bmagnu.pixit.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.bmagnu.pixit.common.GameState;
import net.bmagnu.pixit.common.Settings;

public class GameServer {
	public Map<Integer, String> images;
	
	public GameState gameState;
	
	public int currentPlayer = 0;
	
	public List<Player> players = new ArrayList<>();
	
	public void processCzarTheme(String theme) {
		
	}
	
	public void processImageSelect(int imageID) {
		
	}
	
	public void registerPlayer(Player player) {
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			return;
		
		players.add(player);
		
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			Server.execute.addLast(() -> processInitialization());
		
	}
	
	private void processInitialization() {
		//Send each player all player info
		for(int i = 0; i < players.size(); i++) {
			if(i == currentPlayer) 
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR_YOU);
			else
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR);
		}
	}
}
