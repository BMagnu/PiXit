package net.bmagnu.pixit.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.bmagnu.pixit.common.GameState;
import net.bmagnu.pixit.common.PiXitImage;

public class GameServer {
	public Map<Integer, String> images = new HashMap<>();
	
	public Map<Integer, PiXitImage> imagesById = new HashMap<>();
	
	public List<PiXitImage> freeImages = new ArrayList<>();
	
	public GameState gameState;
	
	public int currentPlayer = 0;
	
	public int rounds = 0;
	
	public List<Player> players = new ArrayList<>();
	
	public Map<PiXitImage, Integer> currentImages = new HashMap<>(); //ImageId | PlayerId
	
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
		
		Integer imageId = p.imageSlots.get(imageSlot).imageId;
		
		currentImages.put(imagesById.get(imageId), playerId);
		
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
		
		if(imagesById.get(imageId) == null)
			return false;
		
		currentImageGuesses.put(playerId, imageId);
		
		if(currentImageGuesses.size() >= players.size() - 1)
			Server.addToQueue(() -> processAllImagesGuessed());
		
		return true;
	}
	
	public synchronized int registerPlayer(Player player) {
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			return -1;
		
		player.playerId = currentPlayer;
		currentPlayer++;
		
		players.add(player);
		
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			Server.addToQueue(() -> processInitialization());
		
		return currentPlayer - 1;
	}
	
	public synchronized Map<Integer, PiXitImage> requestNewImages(Integer playerId) {
		Player player = players.get(playerId);
		
		for(int i = 0; i < Settings.IMAGE_COUNT; i++) {
			if(player.imageSlots.get(i) != null)
				continue;
			
			int newImage = rand.nextInt(freeImages.size());
			
			PiXitImage image = freeImages.get(newImage);
			freeImages.remove(newImage);
			
			player.imageSlots.put(i, image);
		}
		
		return player.imageSlots;
	}
	
	protected void processInitialization() {
		currentPlayer = 0;
		
		Map<String, Integer> points = new HashMap<>();
		int anonCnt = 0;
		for(int i = 0; i < players.size(); i++) {
			String playerName = players.get(i).name.isBlank() ? "Anon " + (++anonCnt) : players.get(i).name;
			points.put(playerName, players.get(i).points);
		}
		
		for(int i = 0; i < players.size(); i++) {
			players.get(i).proxy.notifyResults(-1, players.get(i).points, points);
		}
		
		//Send each player all player info
		for(int i = 0; i < players.size(); i++) {
			if(i == currentPlayer) 
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR_YOU);
			else
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR);
		}
	}
	
	protected void processAllImagesPlayed() {
		currentImageGuesses.clear();
		
		List<PiXitImage> images = new ArrayList<>(currentImages.keySet());
		Collections.shuffle(images);
		
		for(int i = 0; i < players.size(); i++) {
			if(i != currentPlayer)
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_GUESS);
			else
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_GUESS_CZAR);
				
			players.get(i).proxy.notifyImages(images);
		}
	}
	
	protected void processAllImagesGuessed() {
	
		int correctImage = currentImages.entrySet().stream().filter((entry) -> entry.getValue() == currentPlayer).findFirst().get().getKey().imageId;
		
		int numCorrectGuess = 0;
		
		for(Entry<Integer, Integer> guess : currentImageGuesses.entrySet()) {
			//Guess Image | Player Guessed
			if(guess.getValue() == correctImage) {
				numCorrectGuess++;
				players.get(guess.getKey()).points += Settings.POINTS_CORRECT_GUESS;
			}
			else {
				int playerImageOriginator = currentImages.get(imagesById.get(guess.getValue()));
				if(playerImageOriginator != guess.getKey())
					players.get(playerImageOriginator).points += Settings.POINTS_GUESSED;
			}
			
		}
		
		if(numCorrectGuess > Settings.MIN_CZAR_DELTA && (players.size() - numCorrectGuess) > Settings.MIN_CZAR_DELTA)
			players.get(currentPlayer).points += Settings.POINTS_GOOD_CZAR;
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Map<String, Integer> points = new HashMap<>();
		int anonCnt = 0;
		for(int i = 0; i < players.size(); i++) {
			String playerName = players.get(i).name.isBlank() ? "Anon " + (++anonCnt) : players.get(i).name;
			points.put(playerName, players.get(i).points);
		}
		
		for(int i = 0; i < players.size(); i++) {
			players.get(i).proxy.notifyResults(correctImage, players.get(i).points, points);
		}
		
		currentPlayer++;
		
		if(currentPlayer >= players.size())
			currentPlayer = 0;
		
		try {
			Thread.sleep(Settings.POST_ROUND_WAIT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(!Settings.PLAY_FULL_ROUNDS || currentPlayer == 0) {
			int neededImages = Settings.PLAY_FULL_ROUNDS ? players.size() * players.size() : players.size();
			
			System.out.println(freeImages.size() + " Images left; " + neededImages + " needed");
			
			if(currentPlayer == 0)
				rounds++;
			
			if(freeImages.size() < neededImages || rounds >= Settings.MAX_ROUNDS) {
				for(int i = 0; i < players.size(); i++) {
					players.get(i).proxy.notifyNewGamestate(GameState.STATE_GAME_OVER);
				}
				Server.addToQueue(() -> restartServer());
				return;
			}
		}
		
		for(int i = 0; i < players.size(); i++) {
			if(i == currentPlayer) 
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR_YOU);
			else
				players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR);
		}
	}
	
	protected void processCzarTheme(String theme) {
		for(int i = 0; i < players.size(); i++) {
			players.get(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CARDS);
			players.get(i).proxy.notifyTheme(theme);
		}
	}
	
	protected void restartServer() {
		//Server.shouldRestart = true;
		Server.isRunning = false;
	}
}
