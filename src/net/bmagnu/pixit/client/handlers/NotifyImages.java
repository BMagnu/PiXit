package net.bmagnu.pixit.client.handlers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.PiXitImageRequest;
import net.bmagnu.pixit.client.ServerMessageHandler;

public class NotifyImages implements ServerMessageHandler {

	@Override
	public void handle(JsonObject data) {
		JsonArray images = (JsonArray) data.get("images");
		
		List<PiXitImageRequest> imageIds = new ArrayList<>();
		
		for(JsonElement image : images) {
			
			PiXitImageRequest imageReq = new PiXitImageRequest();
			
			JsonObject imageJson = (JsonObject) image;
			imageReq.id = imageJson.get("id").getAsInt();
			imageReq.hash = imageJson.get("hash").getAsString();
			
			imageIds.add(imageReq);
		}
		
		Client.instance.controller.setNewImages(imageIds);
	}
	
}
