package net.bmagnu.pixit.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.scene.image.Image;

public class ServerProxy {

	private ServerConnection connection;
	
	public ServerProxy(ServerConnection connection) {
		this.connection = connection;
	}
	
	private String buildJson(JsonObject data, String id) {
		JsonObject toSend = new JsonObject();
		toSend.addProperty("id", id);
		toSend.add("data", data);
		return toSend.toString();
	}
	
	public Map<Integer, Integer> requestNewImages(){
		JsonObject request = new JsonObject();
		request.addProperty("playerId", connection.playerId);
		
		String json = buildJson(request, "requestNewImages");
		
		try {
			JsonObject response = connection.sendWaitForResponse(json);
			
			if(response.get("success").getAsBoolean()) {
				
				JsonArray slots = (JsonArray) response.get("slots");
				Map<Integer, Integer> imageSlots = new HashMap<>();
				
				for(JsonElement slotO : slots) {
					JsonObject slot = (JsonObject) slotO;
					imageSlots.put(slot.get("slot").getAsInt(), slot.get("image").getAsInt());
				}
				
				return imageSlots;
			}
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	public void playImage(int imageSlot) {
		JsonObject request = new JsonObject();
		request.addProperty("imageSlot", imageSlot);
		request.addProperty("playerId", connection.playerId);
		
		String json = buildJson(request, "playImage");
		
		try {
			JsonObject response = connection.sendWaitForResponse(json);
			
			if(response.get("success").getAsBoolean()) 
				return;
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	public void playCzarTheme(String theme) {
		JsonObject request = new JsonObject();
		request.addProperty("theme", theme);
		
		String json = buildJson(request, "playCzarTheme");
		
		try {
			JsonObject response = connection.sendWaitForResponse(json);
			
			if(response.get("success").getAsBoolean()) 
				return;
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	public void playImageGuess(int imageId) {
		JsonObject request = new JsonObject();
		request.addProperty("imageId", imageId);
		request.addProperty("playerId", connection.playerId);
		
		String json = buildJson(request, "playImageGuess");
		
		try {
			JsonObject response = connection.sendWaitForResponse(json);
			
			if(response.get("success").getAsBoolean()) 
				return;
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	public Image loadImage(int imageId) {
		
		JsonObject request = new JsonObject();
		request.addProperty("id", imageId);
		
		String json = buildJson(request, "loadImage");
		
		try {
			JsonObject response = connection.sendWaitForResponse(json);
			
			if(response.get("success").getAsBoolean()) {
				InputStream imageStream = new ByteArrayInputStream(Base64.getDecoder().decode(response.get("image").getAsString()));
				Image image = new Image(imageStream);
				imageStream.close();
				return image;
			}
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	public int registerPlayer() {
		String json = buildJson(new JsonObject(), "registerPlayer");
		
		try {
			JsonObject response = connection.sendWaitForResponse(json);
			
			if(response.get("success").getAsBoolean())
				return response.get("playerId").getAsInt();
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
}
