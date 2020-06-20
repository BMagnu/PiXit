package net.bmagnu.pixit.server.commands;

import java.util.Map.Entry;

import net.bmagnu.pixit.common.PiXitImage;
import net.bmagnu.pixit.server.DebugCommandHandler;
import net.bmagnu.pixit.server.GameServer;
import net.bmagnu.pixit.server.Player;

public class CommandShowState implements DebugCommandHandler {

	@Override
	public String handle(String command, GameServer server) {
		boolean verbose = command.startsWith("-v");
		
		StringBuilder string = new StringBuilder(128);
		
		string.append("Images: ");
		string.append(server.freeImages.size());
		string.append(" / ");
		string.append(server.images.size());
		string.append("\r\n");

		string.append("Current Player: ");
		string.append(server.currentPlayer);
		string.append("\r\n");
		
		string.append("Current Round: ");
		string.append(server.rounds);
		string.append("\r\n");
		
		string.append("Registered Players: ");
		string.append(server.players.size());
		string.append("\r\n");
		if(verbose) {
			for(Entry<String, Player> player : server.players.entrySet()) {
				string.append("\tID: ");
				string.append(player.getValue().playerId);
				string.append(", Name: ");
				string.append(player.getValue().name);
				string.append(", Points: ");
				string.append(player.getValue().points);
				string.append(", State: ");
				string.append(player.getValue().state.name());
				string.append(", Connection: ");
				string.append(player.getKey());
				string.append("\r\n");
			}
			string.append("\r\n");
		}
		
		string.append("Submitted Images: ");
		string.append(server.currentImages.size());
		string.append("\r\n");
		if(verbose) {
			for(Entry<PiXitImage, Integer> image : server.currentImages.entrySet()) {
				string.append("\tImage ID: ");
				string.append(image.getKey().imageId);
				string.append(", Played By: ");
				string.append(image.getValue());
				string.append(", Hash: ");
				string.append(image.getKey().hash);
				string.append("\r\n");
			}
			string.append("\r\n");
		}
		
		string.append("Guessed Images: ");
		string.append(server.currentImageGuesses.size());
		string.append("\r\n");
		if(verbose) {
			for(Entry<Integer, Integer> image : server.currentImageGuesses.entrySet()) {
				string.append("\tGuesser: ");
				string.append(image.getKey());
				string.append(", Image ID: ");
				string.append(image.getValue());
				string.append("\r\n");
			}
			string.append("\r\n");
		}
		
		return string.toString();
	}

}
