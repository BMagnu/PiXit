package net.bmagnu.pixit.client.handlers;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.ServerMessageHandler;

public class NotifyResults implements ServerMessageHandler {

	@Override
	public void handle(JSONObject data) {
		Integer points = ((Long) data.get("totalPoints")).intValue();
		Integer correctImg = ((Long) data.get("correctImageId")).intValue();
		
		Client.instance.controller.setPoints(points);
		Client.instance.controller.highlightImageById(correctImg);
		Client.instance.controller.setInfoBox("All guessed! This was the correct image!");
		System.out.println("Correct Image was " + correctImg);
		//TODO proper Correct Image Highlighting
	}
	
}
