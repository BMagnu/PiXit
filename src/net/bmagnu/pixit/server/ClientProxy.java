package net.bmagnu.pixit.server;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.bmagnu.pixit.common.GameState;

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
	
	public void notifyImages(List<Integer> images) {
		JsonObject request = new JsonObject();
		
		JsonArray imagesJson = new JsonArray();
		
		for(Integer image : images) {
			imagesJson.add(image);
		}
		
		request.add("images", imagesJson);
		
		client.send(buildJson(request, "images"));
	}
	
	public void notifyResults(int correctImage, int points) {
		JsonObject request = new JsonObject();
		request.addProperty("correctImageId", correctImage);
		request.addProperty("totalPoints", points);
		
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
}
