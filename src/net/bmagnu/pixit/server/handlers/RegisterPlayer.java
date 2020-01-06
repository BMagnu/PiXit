package net.bmagnu.pixit.server.handlers;

import java.util.HashMap;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.ClientProxy;
import net.bmagnu.pixit.server.GameServer;
import net.bmagnu.pixit.server.Player;

import net.bmagnu.pixit.server.ClientConnection;

public class RegisterPlayer implements ClientMessageHandler {

	@SuppressWarnings ("unchecked")
	@Override
	public JSONObject handle(JSONObject data, GameServer server, ClientConnection socket) {
		Player player = new Player();
		
		int playerId = server.registerPlayer(player);
		player.proxy = new ClientProxy(socket);
		player.imageSlots = new HashMap<>();
		player.points = 0;
		
		JSONObject json = new JSONObject();
		json.put("success", true);
		json.put("playerId", playerId);
			
		return json;
	
	}
	
	@Override
	public JSONObject handle(JSONObject data, GameServer server) {
		throw new IllegalStateException("Can't Register Player without Socket");
	}
}
