package net.bmagnu.pixit.client;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.client.handlers.*;

public class ServerHandler {
	
	private static Map<String, ServerMessageHandler> handlers = new HashMap<>();
	
	public ServerHandler() {
		handlers.put("images", new NotifyImages());
		handlers.put("gamestate", new NotifyNewGamestate());
		handlers.put("results", new NotifyResults());
		handlers.put("theme", new NotifyTheme());
		
		System.out.println("Created new ServerHandler with " + handlers.size() + " Handlers registered!");
	}
	
	
	public void handleRecieveMessage(JSONObject json) {
		handlers.get((String) json.get("id")).handle((JSONObject) json.get("data"));
	}	
}
