package net.bmagnu.pixit.server.handlers;

import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.bmagnu.pixit.common.PiXitImage;
import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class RequestNewImages implements ClientMessageHandler {

	@Override
	public JsonObject handle(JsonObject data, GameServer server) {
		Integer playerId = data.get("playerId").getAsInt();
		
		JsonObject json = new JsonObject();
		
		Map<Integer, PiXitImage> imageSlots = server.requestNewImages(playerId);
		
		JsonArray slots = new JsonArray();
		
		for(Entry<Integer, PiXitImage> imageSlot : imageSlots.entrySet()) {
			JsonObject currentSlot = new JsonObject();
			
			currentSlot.addProperty("slot", imageSlot.getKey());
			currentSlot.addProperty("image", imageSlot.getValue().imageId);
			currentSlot.addProperty("hash", imageSlot.getValue().hash);
			
			slots.add(currentSlot);
		}
		
		json.add("slots", slots);
		json.addProperty("success", true);
			
		return json;
	}
}
