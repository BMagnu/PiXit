package net.bmagnu.pixit.server.handlers;

import com.google.gson.JsonObject;

import net.bmagnu.pixit.common.Settings;
import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class PlayCzarTheme implements ClientMessageHandler {

	@Override
	public JsonObject handle(JsonObject data, GameServer server) {
		String theme = data.get("theme").getAsString();
		
		if(!Settings.ALLOW_EMPTY_THEME && theme.isEmpty()) {
			return getDefaultFail("Theme cannot be empty!");
		}
		
		server.playCzarTheme(theme);
		
		return getDefaultSuccess();
	}
}
