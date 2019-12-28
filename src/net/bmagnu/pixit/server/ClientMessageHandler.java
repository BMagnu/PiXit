package net.bmagnu.pixit.server;

import org.json.simple.JSONObject;

public interface ClientMessageHandler {
	public JSONObject handle(JSONObject data, GameServer server);
	
	default JSONObject getDefaultFail(String reason) {
		JSONObject fail = new JSONObject();
		fail.put("success", false);
		fail.put("failReason", reason);
		
		return fail;
	}
}
