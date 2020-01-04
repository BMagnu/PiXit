package net.bmagnu.pixit.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class ClientHandler {
	
	private static Map<String, ClientMessageHandler> handlers = new HashMap<>();
	
	private GameServer gameserver;
	
	private Socket socket;
	
	public ClientHandler(GameServer gameserver, Socket socket) {
		this.gameserver = gameserver;
		this.socket = socket;
		System.out.println("Created new ClientHandler with " + handlers.size() + " Handlers registered!");
	}
	
	
	public JSONObject handleRecieveMessage(JSONObject json) {
		JSONObject response = handlers.get((String) json.get("id")).handle((JSONObject) json.get("data"), gameserver, socket);
		return response;
	}
	
	
	public static void registerHandler(String id, ClientMessageHandler handler) {
		handlers.put(id, handler);
	}
	
}
