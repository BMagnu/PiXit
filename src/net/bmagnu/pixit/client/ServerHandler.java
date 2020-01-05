package net.bmagnu.pixit.client;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class ServerHandler {
	
	private static Map<String, ServerMessageHandler> handlers = new HashMap<>();
	
	public ServerHandler() {
		System.out.println("Created new ServerHandler with " + handlers.size() + " Handlers registered!");
	}
	
	
	public void handleRecieveMessage(JSONObject json) {
		handlers.get((String) json.get("id")).handle((JSONObject) json.get("data"));
	}	
}
