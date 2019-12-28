package net.bmagnu.pixit.server;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class ClientHandler {
	
	private static Map<String, ClientMessageHandler> handlers = new HashMap<>();
	
	private GameServer gameserver;
	
	public ClientHandler(GameServer gameserver) {
		this.gameserver = gameserver;
		System.out.println("Created new ClientHandler with " + handlers.size() + " Handlers registered!");
	}
	
	
	public JSONObject handleRecieveMessage(JSONObject json) {
		JSONObject response = handlers.get((String) json.get("id")).handle((JSONObject) json.get("data"), gameserver);
		return response;
	}
	
	
	public static void registerHandler(String id, ClientMessageHandler handler) {
		handlers.put(id, handler);
	}
	
}
