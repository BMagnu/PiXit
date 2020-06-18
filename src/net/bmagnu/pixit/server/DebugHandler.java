package net.bmagnu.pixit.server;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import net.bmagnu.pixit.server.commands.CommandKickPlayer;
import net.bmagnu.pixit.server.commands.CommandMockClient;
import net.bmagnu.pixit.server.commands.CommandSendClient;
import net.bmagnu.pixit.server.commands.CommandShowState;

public class DebugHandler {

private static Map<String, DebugCommandHandler> handlers = new HashMap<>();
	
	private GameServer gameserver;
	private PrintStream out;
	
	public DebugHandler(GameServer gameserver, PrintStream out) {
		this.gameserver = gameserver;
		this.out = out;
		
		handlers.put("mockClient", new CommandMockClient());
		handlers.put("sendClient", new CommandSendClient());
		handlers.put("state", new CommandShowState());
		handlers.put("kick", new CommandKickPlayer());
		
		System.out.println("Created new DebugHandler with " + handlers.size() + " Handlers registered!");
	}
	
	
	public void handleRecieveMessage(String command) {
		String response = handlers.get(command.split(" ")[0]).handle(command.substring(command.indexOf(' ') + 1), gameserver);
		out.println(response);
	}
	
}
