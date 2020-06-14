package net.bmagnu.pixit.client.handlers;

import java.util.Map;

import com.google.gson.JsonObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.PiXitImageRequest;
import net.bmagnu.pixit.client.ServerMessageHandler;
import net.bmagnu.pixit.common.GameState;

public class NotifyNewGamestate implements ServerMessageHandler {

	@Override
	public void handle(JsonObject data) {
		Integer id = data.get("state").getAsInt();
		GameState state = GameState.deserialize(id);
		Map<Integer, PiXitImageRequest> images = null;
		
		switch (state) {
			case STATE_WAITING_FOR_CARDS :
				Client.instance.controller.showCzarBox(false);
				Client.instance.controller.setInfoBox("Play a card that matches the round's theme!");
				Client.instance.playSound("plop");
				break;
			case STATE_WAITING_FOR_CZAR :
				images = Client.instance.proxy.requestNewImages();
				
				Client.instance.controller.setNewImagesCleanup(images);
				Client.instance.controller.showCzarBox(false);
				Client.instance.controller.setCzarTheme("");
				Client.instance.controller.setInfoBox("Waiting for the round master to set the round's theme!");
				break;
			case STATE_WAITING_FOR_CZAR_YOU :
				images = Client.instance.proxy.requestNewImages();
				
				Client.instance.controller.setNewImagesCleanup(images);
				Client.instance.controller.showCzarBox(true);
				Client.instance.controller.setInfoBox("You are the round master! Set this round's theme!");
				Client.instance.playSound("swish");
				break;
			case STATE_WAITING_FOR_GUESS :
				Client.instance.controller.showCzarBox(false);
				Client.instance.controller.setInfoBox("Guess which card was played by the round master!");
				Client.instance.playSound("plop");
				break;
			case STATE_WAITING_FOR_GUESS_CZAR :
				Client.instance.controller.showCzarBox(false);
				Client.instance.controller.setInfoBox("Wait for other players to guess your card!");
				break;
			case STATE_GAME_OVER:
				Client.instance.controller.setMainText("The game is over!");
				Client.instance.controller.setInfoBox("");
				break;
			case STATE_WAITING_FOR_PLAYERS:
				throw new IllegalArgumentException();
		}
		
		Client.instance.state = state;
	}
	
}
