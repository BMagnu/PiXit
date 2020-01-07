package net.bmagnu.pixit.client.handlers;

import com.google.gson.JsonObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.ServerMessageHandler;

public class NotifyTheme implements ServerMessageHandler {

	@Override
	public void handle(JsonObject data) {
		String theme = data.get("theme").getAsString();
		Client.instance.controller.setCzarTheme(theme);
	}
	
}
