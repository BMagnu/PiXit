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
		String clientId = data.get("id").getAsString();
		
		Player player = server.players.get(clientId);
		
		JsonObject json = new JsonObject();
		
		if(player == null) {
			player = new Player();
			
			int playerId = server.registerPlayer(player, clientId);
			player.proxy = new ClientProxy(socket);
			player.imageSlots = new HashMap<>();
			player.points = 0;
			player.name = data.get("name").getAsString();
			
			json.addProperty("playerId", playerId);
		}
		else {
			player.proxy = new ClientProxy(socket);
			int playerId = server.reconnectPlayer(player);
			
			json.addProperty("playerId", playerId);
		}
		
		json.addProperty("success", true);

			
		return json;
	}
	
	@Override
	public JsonObject handle(JsonObject data, GameServer server) {
		throw new IllegalStateException("Can't Register Player without Socket");
	}
}
