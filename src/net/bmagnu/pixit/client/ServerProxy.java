package net.bmagnu.pixit.client;

import java.awt.Image;
import java.util.Map;

import net.bmagnu.pixit.common.GameState;

public class ServerProxy {
	
	private ClientMeta client;

	public ServerProxy(ClientMeta client) {
		this.client = client;
	}
	
	public Map<Integer, Integer> requestNewImages(){
		
		
		
		return null;
	}
	
	public void playImage(int imageSlot) {
		
	}
	
	public void playCzarTheme(String theme) {
		
	}
	
	public void playImageGuess(int imageId) {
		
	}
	
	public GameState queryGameState() {
		return null;
	}
	
	public Image loadImage() {
		
		return null;
	}
	
	public void registerPlayer() {
		
	}
}
