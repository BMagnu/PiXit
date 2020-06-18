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
	
	public Map<String, Player> players = new HashMap<>();
	
	public Map<PiXitImage, Integer> currentImages = new HashMap<>(); //ImageId | PlayerId
	
	public Map<Integer, Integer> currentImageGuesses = new HashMap<>(); //PlayerId | ImageId
	
	private Random rand = new Random();
	
	public void playCzarTheme(String theme) {
		currentImages.clear();
		
		Server.addToQueue(() -> processCzarTheme(theme));
	}
	
	public boolean playImage(int imageSlot, Integer playerId) {
		Player p = findPlayerById(playerId);
		
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
		Player p = findPlayerById(playerId);
		
		if(playerId == currentPlayer || p == null)
			return false;
		
		if(imagesById.get(imageId) == null)
			return false;
		
		currentImageGuesses.put(playerId, imageId);
		
		if(currentImageGuesses.size() >= players.size() - 1)
			Server.addToQueue(() -> processAllImagesGuessed());
		
		return true;
	}
	
	public synchronized int registerPlayer(Player player, String id) {
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			return -1;
		
		player.playerId = currentPlayer;
		currentPlayer++;
		
		players.put(id, player);
		
		if(players.size() >= Settings.NUM_PLAYERS_TO_START)
			Server.addToQueue(() -> processInitialization());
		
		return currentPlayer - 1;
	}
	
	public synchronized int reconnectPlayer(Player player) {

		player.proxy.notifyNewGamestate(gameState);
		
		Map<String, Integer> points = new HashMap<>();
		int anonCnt = 0;
		for(int i = 0; i < players.size(); i++) {
			Player p = findPlayerById(i);
			String playerName = p.name.isBlank() ? "Anon " + (++anonCnt) : p.name;
			points.put(playerName, p.points);
		}

		player.proxy.notifyResults(-1, player.points, points);
		player.proxy.notifyMiscInfo(findPlayerById(currentPlayer).name, freeImages.size(), images.size());
		
		
		return player.playerId;
	}
	
	public synchronized Map<Integer, PiXitImage> requestNewImages(Integer playerId) {
		//TODO Fix all player access by index
		Player player = findPlayerById(playerId);
		
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
			Player p = findPlayerById(i);
			String playerName = p.name.isBlank() ? "Anon " + (++anonCnt) : p.name;
			points.put(playerName, p.points);
		}
		
		for(int i = 0; i < players.size(); i++) {
			Player p = findPlayerById(i);
			p.proxy.notifyResults(-1, p.points, points);
			p.proxy.notifyMiscInfo("", images.size(), images.size());
		}
		
		//Send each player all player info
		for(int i = 0; i < players.size(); i++) {
			if(i == currentPlayer) 
				findPlayerById(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR_YOU);
			else
				findPlayerById(i).proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR);
		}
	}
	
	protected void processAllImagesPlayed() {
		currentImageGuesses.clear();
		
		List<PiXitImage> images = new ArrayList<>(currentImages.keySet());
		Collections.shuffle(images);
		
		for(int i = 0; i < players.size(); i++) {
			Player p = findPlayerById(i);
			
			if(i != currentPlayer)
				p.proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_GUESS);
			else
				p.proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_GUESS_CZAR);
				
			p.proxy.notifyImages(images);
		}
	}
	
	protected void processAllImagesGuessed() {
	
		int correctImage = currentImages.entrySet().stream().filter((entry) -> entry.getValue() == currentPlayer).findFirst().get().getKey().imageId;
		
		int numCorrectGuess = 0;
		
		for(Entry<Integer, Integer> guess : currentImageGuesses.entrySet()) {
			//Guess Image | Player Guessed
			if(guess.getValue() == correctImage) {
				numCorrectGuess++;
				findPlayerById(guess.getKey()).points += Settings.POINTS_CORRECT_GUESS;
			}
			else {
				int playerImageOriginator = currentImages.get(imagesById.get(guess.getValue()));
				if(playerImageOriginator != guess.getKey())
					findPlayerById(playerImageOriginator).points += Settings.POINTS_GUESSED;
			}
			
		}
		
		if(numCorrectGuess > Settings.MIN_CZAR_DELTA && (players.size() - numCorrectGuess) > Settings.MIN_CZAR_DELTA)
			findPlayerById(currentPlayer).points += Settings.POINTS_GOOD_CZAR;
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Map<String, Integer> points = new HashMap<>();
		int anonCnt = 0;
		for(int i = 0; i < players.size(); i++) {
			Player p = findPlayerById(i);
			String playerName = p.name.isBlank() ? "Anon " + (++anonCnt) : p.name;
			points.put(playerName, p.points);
		}
		
		for(int i = 0; i < players.size(); i++) {
			Player p = findPlayerById(i);
			p.proxy.notifyResults(correctImage, p.points, points);
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
					findPlayerById(i).proxy.notifyNewGamestate(GameState.STATE_GAME_OVER);
				}
				Server.addToQueue(() -> restartServer());
				return;
			}
		}
		
		Player current = findPlayerById(currentPlayer);
		for(int i = 0; i < players.size(); i++) {
			Player p = findPlayerById(i);
			p.proxy.notifyMiscInfo(current.name, freeImages.size(), images.size());
			
			if(i == currentPlayer) 
				p.proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR_YOU);
			else
				p.proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CZAR);
		}
	}
	
	protected void processCzarTheme(String theme) {
		for(int i = 0; i < players.size(); i++) {
			Player p = findPlayerById(i);
			p.proxy.notifyNewGamestate(GameState.STATE_WAITING_FOR_CARDS);
			p.proxy.notifyTheme(theme);
		}
	}
	
	protected void restartServer() {
		//Server.shouldRestart = true;
		Server.isRunning = false;
	}
	
	private Player findPlayerById(int id) {
		Player p = null;
		for(Player player : players.values()) {
			if(player.playerId == id) {
				p = player;
				break;
			}
		}
		
		return p;
	}
}
