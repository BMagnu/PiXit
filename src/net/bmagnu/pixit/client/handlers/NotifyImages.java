package net.bmagnu.pixit.client.handlers;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.ServerMessageHandler;

public class NotifyImages implements ServerMessageHandler {

	@Override
	public void handle(JSONObject data) {
		JSONArray images = (JSONArray) data.get("images");
		
		List<Integer> imageIds = new ArrayList<>();
		
		for(Object imageO : images) {
			
			imageIds.add(((Long) imageO).intValue());
		}
		
		Client.instance.controller.setNewImages(imageIds);
	}
	
}
