package net.bmagnu.pixit.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	public Map<Integer, PiXitImageRequest> requestNewImages(){
		JsonObject request = new JsonObject();
		request.addProperty("playerId", connection.playerId);
		
		String json = buildJson(request, "requestNewImages");
		
		try {
			JsonObject response = connection.sendWaitForResponse(json);
			
			if(response.get("success").getAsBoolean()) {
				
				JsonArray slots = (JsonArray) response.get("slots");
				Map<Integer, PiXitImageRequest> imageSlots = new HashMap<>();
				
				for(JsonElement slotO : slots) {
					JsonObject slot = (JsonObject) slotO;
					
					PiXitImageRequest image = new PiXitImageRequest();
					image.id = slot.get("image").getAsInt();
					image.hash = slot.get("hash").getAsString();
					
					imageSlots.put(slot.get("slot").getAsInt(), image);
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
	
	public Image loadImage(PiXitImageRequest imageReq) {
		
		JsonObject request = new JsonObject();
		request.addProperty("id", imageReq.id);
		
		String json = buildJson(request, "loadImage");
		
		try {
			JsonObject response = connection.sendWaitForResponse(json);
			
			if(response.get("success").getAsBoolean()) {
				byte [] imageData = Base64.getDecoder().decode(response.get("image").getAsString());
				InputStream imageStream = new ByteArrayInputStream(imageData);
				Image image = new Image(imageStream);
				imageStream.close();
				
				if (Client.instance.cacheImages) {
					File imageCacheFile = new File("./cache/" + imageReq.hash);
				    OutputStream outStream = new FileOutputStream(imageCacheFile);
				    outStream.write(imageData);
				    outStream.close();
				}

				return image;
			}
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	public int registerPlayer(String name, String id) {
		JsonObject request = new JsonObject();
		request.addProperty("name", name);
		request.addProperty("id", name + id);
		
		String json = buildJson(request, "registerPlayer");
		
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
