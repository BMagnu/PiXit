package net.bmagnu.pixit.server.handlers;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.server.ClientHandler;
import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class PlayImage implements ClientMessageHandler {

	@Override
	public JSONObject handle(JSONObject data, GameServer server) {
		Integer id = (Integer) data.get("imageSlot");
		Integer playerId = (Integer) data.get("playerId");
		
		if(server.playImage(id, playerId)) {
			return getDefaultSuccess();
		} else {
			return getDefaultFail("No Image in Defined Slot");
		}
	}

	static {
		ClientHandler.registerHandler("playImage", new PlayImage());
	}
}
