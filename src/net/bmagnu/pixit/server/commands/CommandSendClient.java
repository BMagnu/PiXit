package net.bmagnu.pixit.server.commands;

import net.bmagnu.pixit.server.DebugCommandHandler;
import net.bmagnu.pixit.server.GameServer;
import net.bmagnu.pixit.server.Player;

public class CommandSendClient implements DebugCommandHandler {

	@Override
	public String handle(String command, GameServer server) {
		
		Player p = null;
		for(Player player : server.players.values()) {
			if(player.playerId.equals(Integer.parseInt(command.split(" ")[0]))) {
				p = player;
				break;
			}
		}
		
		if (p == null)
			return "Error: Player not Found";
		
		command = command.substring(command.indexOf(' ') + 1);
		
		p.proxy.sendRaw(command);
		
		return "Sent";
	}

}
