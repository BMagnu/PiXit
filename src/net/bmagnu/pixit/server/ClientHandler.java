package net.bmagnu.pixit.server;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import net.bmagnu.pixit.server.handlers.*;

public class ClientHandler {
	
	private static Map<String, ClientMessageHandler> handlers = new HashMap<>();
	
	private GameServer gameserver;
	
	private ClientConnection socket;
	
	public ClientHandler(GameServer gameserver, ClientConnection socket) {
		this.gameserver = gameserver;
		this.socket = socket;
		
		handlers.put("loadImage", new LoadImage());
		handlers.put("playCzarTheme", new PlayCzarTheme());
		handlers.put("playImage", new PlayImage());
		handlers.put("playImageGuess", new PlayImageGuess());
		handlers.put("registerPlayer", new RegisterPlayer());
		handlers.put("requestNewImages", new RequestNewImages());
		
		System.out.println("Created new ClientHandler with " + handlers.size() + " Handlers registered!");
	}
	
	
	public JsonObject handleRecieveMessage(JsonObject json) {
		JsonObject response = handlers.get(json.get("id").getAsString()).handle((JsonObject) json.get("data"), gameserver, socket);
		return response;
	}
}
