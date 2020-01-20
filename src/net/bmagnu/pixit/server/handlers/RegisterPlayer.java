package net.bmagnu.pixit.server.handlers;

import java.util.HashMap;

import com.google.gson.JsonObject;

import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.ClientProxy;
import net.bmagnu.pixit.server.GameServer;
import net.bmagnu.pixit.server.Player;

import net.bmagnu.pixit.server.ClientConnection;

public class RegisterPlayer implements ClientMessageHandler {

	@Override
	public JsonObject handle(JsonObject data, GameServer server, ClientConnection socket) {
		Player player = new Player();
		
		int playerId = server.registerPlayer(player);
		player.proxy = new ClientProxy(socket);
		player.imageSlots = new HashMap<>();
		player.points = 0;
		player.name = data.get("name").getAsString();
		
		JsonObject json = new JsonObject();
		json.addProperty("success", true);
		json.addProperty("playerId", playerId);
			
		return json;
	
	}
	
	@Override
	public JsonObject handle(JsonObject data, GameServer server) {
		throw new IllegalStateException("Can't Register Player without Socket");
	}
}
