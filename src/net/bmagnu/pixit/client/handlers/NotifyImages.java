package net.bmagnu.pixit.client.handlers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.ServerMessageHandler;

public class NotifyImages implements ServerMessageHandler {

	@Override
	public void handle(JsonObject data) {
		JsonArray images = (JsonArray) data.get("images");
		
		List<Integer> imageIds = new ArrayList<>();
		
		for(JsonElement image : images) {
			
			imageIds.add(image.getAsInt());
		}
		
		Client.instance.controller.setNewImages(imageIds);
	}
	
}
