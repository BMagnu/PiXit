package net.bmagnu.pixit.server.handlers;

import com.google.gson.JsonObject;

import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class PlayImage implements ClientMessageHandler {

	@Override
	public JsonObject handle(JsonObject data, GameServer server) {
		Integer id = data.get("imageSlot").getAsInt();
		Integer playerId = data.get("playerId").getAsInt();
		
		if(server.playImage(id, playerId)) {
			return getDefaultSuccess();
		} else {
			return getDefaultFail("No Image in Defined Slot");
		}
	}
}
