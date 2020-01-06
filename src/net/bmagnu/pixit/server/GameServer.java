package net.bmagnu.pixit.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.bmagnu.pixit.common.GameState;
import net.bmagnu.pixit.common.Settings;

public class GameServer {
	public Map<Integer, String> images = new HashMap<>();
	
	public List<Integer> freeImages = new ArrayList<>();
	
	public GameState gameState;
	
	public int currentPlayer = 0;
	
	public List<Player> players = new ArrayList<>();
	
	public Map<Integer, Integer> currentImages = new HashMap<>(); //ImageId | PlayerId
	
	public Map<Integer, Integer> currentImageGuesses = new HashMap<>(); //ImageId | PlayerId
	
	private Random rand = new Random();
	
	public void playCzarTheme(String theme) {
		currentImages.clear();
		
		Server.addToQueue(() -> processCzarTheme(theme));
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
			Server.addToQueue(() -> processAllImagesPlayed());
		
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
			Server.addToQueue(() -> processAllImagesGuessed());
		
		return true;
	}
	
	public int registerPlayer(Player player) {
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			return -1;
		
		player.playerId = currentPlayer;
		currentPlayer++;
		
		players.add(player);
		
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			Server.addToQueue(() -> processInitialization());
		
		return currentPlayer - 1;
	}
	
	public synchronized Map<Integer, Integer> requestNewImages(Integer playerId) {
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
		
		List<Integer> images = new ArrayList<>(currentImages.keySet());
		Collections.shuffle(images);
		
		for(int i = 0; i < players.size(); i++) {
			if(i != currentPlayer)
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_GUESS);
			else
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_GUESS_CZAR);
				
			players.get(i).proxy.notifyImages(images);
		}
	}
	
	private void processAllImagesGuessed() {
	
		Integer correctImage = currentImages.entrySet().stream().filter((entry) -> entry.getValue() == currentPlayer).findFirst().get().getKey();
		
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
			players.get(i).proxy.notifyResults(correctImage, players.get(i).points);
		}
		
		currentPlayer++;
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for(int i = 0; i < players.size(); i++) {
			if(i == currentPlayer) 
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR_YOU);
			else
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR);
		}
	}
	
	private void processCzarTheme(String theme) {
		for(int i = 0; i < players.size(); i++) {
			players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CARDS);
			players.get(i).proxy.notifyTheme(theme);
		}
	}
}
