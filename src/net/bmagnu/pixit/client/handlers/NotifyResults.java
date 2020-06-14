package net.bmagnu.pixit.client.handlers;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.ServerMessageHandler;

public class NotifyResults implements ServerMessageHandler {

	@Override
	public void handle(JsonObject data) {
		//Integer points = data.get("totalPoints").getAsInt();
		Integer correctImg = data.get("correctImageId").getAsInt();
		
		Map<String, Integer> playerPoints = new HashMap<>();
		
		for(JsonElement point : (JsonArray)data.get("points")) {
			playerPoints.put(((JsonObject) point).get("player").getAsString(), ((JsonObject) point).get("points").getAsInt());
		}
		
		//No dedicated own points will be shown anymore
		//Client.instance.controller.setPoints(points);
		Client.instance.controller.highlightImageById(correctImg);
		Client.instance.controller.setInfoBox("All players have guessed! This was the correct image!");
		Client.instance.controller.setPlayersPoints(playerPoints);
		System.out.println("Correct Image was " + correctImg);
	}
	
}
