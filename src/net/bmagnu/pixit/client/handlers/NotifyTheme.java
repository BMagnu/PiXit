package net.bmagnu.pixit.client.handlers;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.ServerMessageHandler;

public class NotifyTheme implements ServerMessageHandler {

	@Override
	public void handle(JSONObject data) {
		String theme = (String) data.get("theme");
		Client.instance.controller.setCzarTheme(theme);
	}
	
}
