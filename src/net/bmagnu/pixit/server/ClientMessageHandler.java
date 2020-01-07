package net.bmagnu.pixit.server;

import com.google.gson.JsonObject;

public interface ClientMessageHandler {
	public default JsonObject handle(JsonObject data, GameServer server, ClientConnection socket) {
		return handle(data, server);
	}
	
	JsonObject handle(JsonObject data, GameServer server);
	
	default JsonObject getDefaultFail(String reason) {
		JsonObject fail = new JsonObject();
		fail.addProperty("success", false);
		fail.addProperty("failReason", reason);
		
		return fail;
	}
	
	default JsonObject getDefaultSuccess() {
		JsonObject success = new JsonObject();
		success.addProperty("success", true);
		
		return success;
	}
}
