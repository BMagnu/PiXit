package net.bmagnu.pixit.server.handlers;

import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class RequestNewImages implements ClientMessageHandler {

	@SuppressWarnings ("unchecked")
	@Override
	public JSONObject handle(JSONObject data, GameServer server) {
		Integer playerId = (Integer) data.get("playerId");
		
		JSONObject json = new JSONObject();
		
		Map<Integer, Integer> imageSlots = server.requestNewImages(playerId);
		
		JSONArray slots = new JSONArray();
		
		for(Entry<Integer, Integer> imageSlot : imageSlots.entrySet()) {
			JSONObject currentSlot = new JSONObject();
			
			currentSlot.put("slot", imageSlot.getKey());
			currentSlot.put("image", imageSlot.getValue());
			
			slots.add(currentSlot);
		}
		
		json.put("slots", slots);
		json.put("success", true);
			
		return json;
	}
}
