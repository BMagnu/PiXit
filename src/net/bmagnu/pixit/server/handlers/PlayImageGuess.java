package net.bmagnu.pixit.server.handlers;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class PlayImageGuess implements ClientMessageHandler {

	@Override
	public JSONObject handle(JSONObject data, GameServer server) {
		Integer id = (Integer) data.get("imageId");
		Integer playerId = (Integer) data.get("playerId");
		
		if(server.playImageGuess(id, playerId)) {
			return getDefaultSuccess();
		} else {
			return getDefaultFail("No Image with Id");
		}
	}
}
