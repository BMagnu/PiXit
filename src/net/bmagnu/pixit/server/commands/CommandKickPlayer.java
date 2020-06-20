package net.bmagnu.pixit.server.commands;

import java.util.Map.Entry;

import net.bmagnu.pixit.common.GameState;
import net.bmagnu.pixit.server.DebugCommandHandler;
import net.bmagnu.pixit.server.GameServer;
import net.bmagnu.pixit.server.Player;

public class CommandKickPlayer implements DebugCommandHandler {

	@Override
	public String handle(String command, GameServer server) {
		int id = Integer.parseInt(command);
		
		GameState state = GameState.STATE_GAME_OVER;
		
		for(Entry<String, Player> player : server.players.entrySet()) {
			if(id == player.getValue().playerId) {
				state = player.getValue().state;
				
				if(state == GameState.STATE_WAITING_FOR_GUESS_CZAR ||
				  (state == GameState.STATE_WAITING_FOR_CARDS && server.currentPlayer == id))
					return "Can't kick player that is czar after a topic has been picked!";
				
				server.players.remove(player.getKey());
				return "Kicked Player " + player.getValue().name + " with id " + player.getValue().playerId;
			}
		}
		
		for(Entry<String, Player> player : server.players.entrySet()) {
			if(id < player.getValue().playerId) {
				player.getValue().playerId--;
				//TODO Reassign IDs at Client
			}
		}
		
		switch(state) {
		case STATE_GAME_OVER:
		case STATE_WAITING_FOR_CZAR:
		case STATE_WAITING_FOR_PLAYERS:
		case STATE_WAITING_FOR_GUESS_CZAR:
			//Nothing
			break;
		case STATE_WAITING_FOR_CZAR_YOU:
			server.currentPlayer++;

			if (server.currentPlayer >= server.players.size())
				server.currentPlayer = 0;
			
			
			break;

		case STATE_WAITING_FOR_GUESS:
			//Test if all guessed
			break;
			
		case STATE_WAITING_FOR_CARDS:
			//Test if all picked
			break;
		}
		
		return "Player not Found!";
	}

}
