package net.bmagnu.pixit.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.bmagnu.pixit.common.GameState;
import net.bmagnu.pixit.common.Settings;

public class GameServer {
	public Map<Integer, String> images;
	
	public List<Integer> freeImages;
	
	public GameState gameState;
	
	public int currentPlayer = 0;
	
	public List<Player> players = new ArrayList<>();
	
	public Map<Integer, Integer> currentImages = new HashMap<>(); //ImageId | PlayerId
	
	public Map<Integer, Integer> currentImageGuesses = new HashMap<>(); //ImageId | PlayerId
	
	private Random rand = new Random();
	
	public void playCzarTheme(String theme) {
		currentImages.clear();
		
		Server.execute.addLast(() -> processCzarTheme(theme));
	}
	
	public boolean playImage(int imageSlot, Integer playerId) {
		Player p = null;
		for(Player player : players) {
			if(player.playerId == playerId) {
				p = player;
				break;
			}
		}
		
		if(p == null)
			return false;
		
		Integer imageId = p.imageSlots.get(imageSlot);
		
		if(imageId == null)
			return false;
		
		currentImages.put(imageId, playerId);
		
		p.imageSlots.put(imageSlot, null);
		
		if(currentImages.size() >= players.size())
			Server.execute.addLast(() -> processAllImagesPlayed());
		
		return true;
	}
	
	public boolean playImageGuess(int imageId, Integer playerId) {
		Player p = null;
		for(Player player : players) {
			if(player.playerId == playerId) {
				p = player;
				break;
			}
		}
		
		if(playerId == currentPlayer || p == null)
			return false;
		
		currentImageGuesses.put(imageId, playerId);
		
		if(currentImageGuesses.size() >= players.size() - 1)
			Server.execute.addLast(() -> processAllImagesGuessed());
		
		return true;
	}
	
	public int registerPlayer(Player player) {
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			return -1;
		
		player.playerId = currentPlayer;
		currentPlayer++;
		
		players.add(player);
		
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			Server.execute.addLast(() -> processInitialization());
		
		return currentPlayer - 1;
	}
	
	public Map<Integer, Integer> requestNewImages(Integer playerId) {
		Player player = players.get(playerId);
		
		for(int i = 0; i < Settings.IMAGE_COUNT; i++) {
			if(player.imageSlots.get(i) != null)
				continue;
			
			int newImage = rand.nextInt(freeImages.size());
			
			Integer image = freeImages.get(newImage);
			freeImages.remove(newImage);
			
			player.imageSlots.put(i, image);
		}
		
		return player.imageSlots;
	}
	
	private void processInitialization() {
		currentPlayer = 0;
		
		//Send each player all player info
		for(int i = 0; i < players.size(); i++) {
			if(i == currentPlayer) 
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR_YOU);
			else
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR);
		}
	}
	
	private void processAllImagesPlayed() {
		currentImageGuesses.clear();
		
		Set<Integer> images = currentImages.keySet();
		
		for(int i = 0; i < players.size(); i++) {
			players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_GUESS);
			players.get(i).proxy.notifyImages(images);
		}
	}
	
	private void processAllImagesGuessed() {
		currentImageGuesses.clear();
		
		Integer correctImage = currentImages.get(currentPlayer);
		
		int numCorrectGuess = 0;
		
		for(Entry<Integer, Integer> guess : currentImageGuesses.entrySet()) {
			//Guess Image | Player Guessed
			if(guess.getKey() == correctImage) {
				numCorrectGuess++;
				players.get(guess.getValue()).points += Settings.POINTS_CORRECT_GUESS;
			}
			else {
				int playerImageOriginator = currentImages.get(guess.getKey());
				players.get(playerImageOriginator).points += Settings.POINTS_GUESSED;
			}
			
		}
		
		if(numCorrectGuess > Settings.MIN_CZAR_DELTA && ((players.size() - 1) - numCorrectGuess) > Settings.MIN_CZAR_DELTA)
			players.get(currentPlayer).points += Settings.POINTS_GOOD_CZAR;
		
		for(int i = 0; i < players.size(); i++) {
			players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_GUESS);
			players.get(i).proxy.notifyResults(correctImage, players.get(i).points);
		}
		
		currentPlayer++;
	}
	
	private void processCzarTheme(String theme) {
		for(int i = 0; i < players.size(); i++) {
			players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CARDS);
			players.get(i).proxy.notifyTheme(theme);
		}
	}
}
