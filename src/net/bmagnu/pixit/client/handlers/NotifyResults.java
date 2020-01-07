package net.bmagnu.pixit.client.handlers;

import com.google.gson.JsonObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.ServerMessageHandler;

public class NotifyResults implements ServerMessageHandler {

	@Override
	public void handle(JsonObject data) {
		Integer points = data.get("totalPoints").getAsInt();
		Integer correctImg = data.get("correctImageId").getAsInt();
		
		Client.instance.controller.setPoints(points);
		Client.instance.controller.highlightImageById(correctImg);
		Client.instance.controller.setInfoBox("All guessed! This was the correct image!");
		System.out.println("Correct Image was " + correctImg);
		//TODO proper Correct Image Highlighting
	}
	
}
