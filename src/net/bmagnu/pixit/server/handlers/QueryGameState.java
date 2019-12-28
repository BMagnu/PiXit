package net.bmagnu.pixit.server.handlers;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.server.ClientHandler;
import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class QueryGameState implements ClientMessageHandler {

	@Override
	public JSONObject handle(JSONObject data, GameServer server) {
		JSONObject json = new JSONObject();
		
		json.put("state", server.gameState.serialize());
		
		return json;
	}
	
	static {
		ClientHandler.registerHandler("queryGameState", new QueryGameState());
	}

}
