package net.bmagnu.pixit.server;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.bmagnu.pixit.common.GameState;

public class ClientProxy {
	
	ClientConnection client;
	
	public ClientProxy(ClientConnection client) {
		this.client = client;
	}
	
	@SuppressWarnings ("unchecked")
	private String buildJSON(JSONObject data, String id) {
		JSONObject toSend = new JSONObject();
		toSend.put("id", id);
		toSend.put("data", data);
		return toSend.toJSONString();
	}
	
	@SuppressWarnings ("unchecked")
	public void notifyImages(List<Integer> images) {
		JSONObject request = new JSONObject();
		
		JSONArray imagesJson = new JSONArray();
		
		for(Integer image : images) {
			imagesJson.add(image);
		}
		
		request.put("images", imagesJson);
		
		client.send(buildJSON(request, "images"));
	}
	
	@SuppressWarnings ("unchecked")
	public void notifyResults(int correctImage, int points) {
		JSONObject request = new JSONObject();
		request.put("correctImageId", correctImage);
		request.put("totalPoints", points);
		
		client.send(buildJSON(request, "results"));
	}
	
	@SuppressWarnings ("unchecked")
	public void notifyTheme(String theme) {
		JSONObject request = new JSONObject();
		request.put("theme", theme);
		
		client.send(buildJSON(request, "theme"));
	}
	
	@SuppressWarnings ("unchecked")
	public void notifyNewGamestate(GameState state) {
		JSONObject request = new JSONObject();
		request.put("state", state.serialize());
		
		client.send(buildJSON(request, "gamestate"));
	}
}
