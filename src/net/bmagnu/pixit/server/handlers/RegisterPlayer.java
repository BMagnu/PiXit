package net.bmagnu.pixit.server.handlers;

import java.net.Socket;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.server.ClientHandler;
import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;
import net.bmagnu.pixit.server.Player;

public class RegisterPlayer implements ClientMessageHandler {

	@Override
	public JSONObject handle(JSONObject data, GameServer server, Socket socket) {
		Player player = new Player();
		
		int playerId = server.registerPlayer(player);
		
		JSONObject json = new JSONObject();
		json.put("success", true);
		json.put("playerId", playerId);
			
		return json;
	
	}
	
	@Override
	public JSONObject handle(JSONObject data, GameServer server) {
		throw new IllegalStateException("Can't Register Player without Socket");
	}

	static {
		ClientHandler.registerHandler("registerPlayer", new RegisterPlayer());
	}
}
