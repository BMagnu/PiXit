package net.bmagnu.pixit.server.commands;

import java.util.Map.Entry;

import net.bmagnu.pixit.server.DebugCommandHandler;
import net.bmagnu.pixit.server.GameServer;
import net.bmagnu.pixit.server.Player;

public class CommandKickPlayer implements DebugCommandHandler {

	@Override
	public String handle(String command, GameServer server) {
		int id = Integer.parseInt(command);
		
		for(Entry<String, Player> player : server.players.entrySet()) {
			if(id == player.getValue().playerId) {
				server.players.remove(player.getKey());
				return "Kicked Player " + player.getValue().name + " with id " + player.getValue().playerId;
			}
		}
		
		return "Player not Found!";
	}

}
