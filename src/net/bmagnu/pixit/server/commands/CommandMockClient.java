package net.bmagnu.pixit.server.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.bmagnu.pixit.server.ClientHandler;
import net.bmagnu.pixit.server.DebugCommandHandler;
import net.bmagnu.pixit.server.GameServer;

public class CommandMockClient implements DebugCommandHandler {

	@Override
	public String handle(String command, GameServer server) {
		JsonObject jsonIn = (JsonObject) JsonParser.parseString(command);
		JsonObject jsonOut = new ClientHandler(server, null).handleRecieveMessage(jsonIn);
		return jsonOut.toString();
	}

}
