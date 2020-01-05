package net.bmagnu.pixit.client;

import org.json.simple.JSONObject;

public interface ServerMessageHandler {
	public void handle(JSONObject data);
}
