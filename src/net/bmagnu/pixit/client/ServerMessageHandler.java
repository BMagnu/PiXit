package net.bmagnu.pixit.client;

import com.google.gson.JsonObject;

public interface ServerMessageHandler {
	public void handle(JsonObject data);
}
