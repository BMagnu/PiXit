package net.bmagnu.pixit.server;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.bmagnu.pixit.common.GameState;
import net.bmagnu.pixit.common.PiXitImage;

public class ClientProxy {
	
	ClientConnection client;
	
	public ClientProxy(ClientConnection client) {
		this.client = client;
	}
	
	private String buildJson(JsonObject data, String id) {
		JsonObject toSend = new JsonObject();
		toSend.addProperty("id", id);
		toSend.add("data", data);
		return toSend.toString();
	}
	
	public void notifyImages(List<PiXitImage> images) {
		JsonObject request = new JsonObject();
		
		JsonArray imagesJson = new JsonArray();
		
		for(PiXitImage image : images) {
			JsonObject imageJson = new JsonObject();
			imageJson.addProperty("id", image.imageId);
			imageJson.addProperty("hash", image.hash);
			
			imagesJson.add(imageJson);
		}
		
		request.add("images", imagesJson);
		
		client.send(buildJson(request, "images"));
	}
	
	public void notifyResults(int correctImage, int points, Map<String, Integer> pointsPlayers) {
		JsonObject request = new JsonObject();
		request.addProperty("correctImageId", correctImage);
		request.addProperty("totalPoints", points);
		
		JsonArray pointsArray = new JsonArray();
		
		for(Entry<String, Integer> point : pointsPlayers.entrySet()) {
			JsonObject pointsJson = new JsonObject();
			pointsJson.addProperty("player", point.getKey());
			pointsJson.addProperty("points", point.getValue());
			
			pointsArray.add(pointsJson);
		}
		
		request.add("points", pointsArray);
		
		client.send(buildJson(request, "results"));
	}
	
	public void notifyTheme(String theme) {
		JsonObject request = new JsonObject();
		request.addProperty("theme", theme);
		
		client.send(buildJson(request, "theme"));
	}
	
	public void notifyNewGamestate(GameState state) {
		JsonObject request = new JsonObject();
		request.addProperty("state", state.serialize());
		
		client.send(buildJson(request, "gamestate"));
	}
	
	public void notifyMiscInfo(String currentCzar, int deckCurrent, int deckMaximum) {
		JsonObject request = new JsonObject();
		request.addProperty("currentCzar", currentCzar);
		request.addProperty("deckCurrent", deckCurrent);
		request.addProperty("deckMax", deckMaximum);
		
		client.send(buildJson(request, "info"));
	}

	public void sendRaw(String command) {
		client.send(command);
	}
}
