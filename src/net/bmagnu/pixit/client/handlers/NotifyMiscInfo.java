package net.bmagnu.pixit.client.handlers;

import com.google.gson.JsonObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.ServerMessageHandler;

public class NotifyMiscInfo  implements ServerMessageHandler{

	@Override
	public void handle(JsonObject data) {
		String currentCzar = data.get("currentCzar").getAsString();
		Client.instance.controller.highlightPlayer(currentCzar);
		
		Integer deckCurrent = data.get("deckCurrent").getAsInt();
		Integer deckMaximum = data.get("deckMax").getAsInt();
		
		Client.instance.controller.setDeckStats(deckCurrent, deckMaximum);
	}

}
