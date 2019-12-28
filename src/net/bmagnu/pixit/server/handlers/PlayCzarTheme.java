package net.bmagnu.pixit.server.handlers;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.common.Settings;
import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class PlayCzarTheme implements ClientMessageHandler {

	@Override
	public JSONObject handle(JSONObject data, GameServer server) {
		String theme = (String) data.get("theme");
		
		if(!Settings.ALLOW_EMPTY_THEME && theme.isEmpty()) {
			return getDefaultFail("Theme cannot be empty!");
		}
		
		
		return null;
	}

}
