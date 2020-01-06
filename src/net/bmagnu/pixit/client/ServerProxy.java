package net.bmagnu.pixit.client;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ServerProxy {
	
	/*
	    handlers.put("loadImage", new LoadImage());
		handlers.put("playCzarTheme", new PlayCzarTheme());
		handlers.put("playImage", new PlayImage());
		handlers.put("playImageGuess", new PlayImageGuess());
		handlers.put("registerPlayer", new RegisterPlayer());
		handlers.put("requestNewImages", new RequestNewImages());
	 */

	private ServerConnection connection;
	
	public ServerProxy(ServerConnection connection) {
		this.connection = connection;
	}
	
	@SuppressWarnings ("unchecked")
	private String buildJSON(JSONObject data, String id) {
		JSONObject toSend = new JSONObject();
		toSend.put("id", id);
		toSend.put("data", data);
		return toSend.toJSONString();
	}
	
	@SuppressWarnings ("unchecked")
	public Map<Integer, Integer> requestNewImages(){
		JSONObject request = new JSONObject();
		request.put("playerId", connection.playerId);
		
		String json = buildJSON(request, "requestNewImages");
		
		try {
			JSONObject response = connection.sendWaitForResponse(json);
			
			if((Boolean)response.get("success")) {
				
				JSONArray slots = (JSONArray) response.get("slots");
				Map<Integer, Integer> imageSlots = new HashMap<>();
				
				for(Object slotO : slots) {
					JSONObject slot = (JSONObject) slotO;
					imageSlots.put((Integer)slot.get("slot"), (Integer)slot.get("image"));
				}
				
				return imageSlots;
			}
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException | ParseException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	@SuppressWarnings ("unchecked")
	public void playImage(int imageSlot) {
		JSONObject request = new JSONObject();
		request.put("imageSlot", imageSlot);
		request.put("playerId", connection.playerId);
		
		String json = buildJSON(request, "playImage");
		
		try {
			JSONObject response = connection.sendWaitForResponse(json);
			
			if((Boolean)response.get("success")) 
				return;
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException | ParseException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	@SuppressWarnings ("unchecked")
	public void playCzarTheme(String theme) {
		JSONObject request = new JSONObject();
		request.put("theme", theme);
		
		String json = buildJSON(request, "playCzarTheme");
		
		try {
			JSONObject response = connection.sendWaitForResponse(json);
			
			if((Boolean)response.get("success")) 
				return;
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException | ParseException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	@SuppressWarnings ("unchecked")
	public void playImageGuess(int imageId) {
		JSONObject request = new JSONObject();
		request.put("imageId", imageId);
		request.put("playerId", connection.playerId);
		
		String json = buildJSON(request, "playImageGuess");
		
		try {
			JSONObject response = connection.sendWaitForResponse(json);
			
			if((Boolean)response.get("success")) 
				return;
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException | ParseException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	@SuppressWarnings ("unchecked")
	public Image loadImage(int imageId) {
		
		JSONObject request = new JSONObject();
		request.put("id", imageId);
		
		String json = buildJSON(request, "loadImage");
		
		try {
			JSONObject response = connection.sendWaitForResponse(json);
			
			if((Boolean)response.get("success")) {
				InputStream imageStream = new ByteArrayInputStream(Base64.getDecoder().decode((String)response.get("image")));
				Image image = ImageIO.read(imageStream);
				imageStream.close();
				return image;
			}
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException | ParseException | IOException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
	
	public int registerPlayer() {
		String json = buildJSON(new JSONObject(), "registerPlayer");
		
		try {
			JSONObject response = connection.sendWaitForResponse(json);
			
			if((Boolean)response.get("success"))
				return ((Long)response.get("playerId")).intValue();
			else
				throw new IllegalArgumentException("Server Error");
		} catch (InterruptedException | ParseException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Client Error");
	}
}
