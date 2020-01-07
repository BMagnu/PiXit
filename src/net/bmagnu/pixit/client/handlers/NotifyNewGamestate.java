package net.bmagnu.pixit.client.handlers;

import java.util.Map;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.client.Client;
import net.bmagnu.pixit.client.ServerMessageHandler;
import net.bmagnu.pixit.common.GameState;

public class NotifyNewGamestate implements ServerMessageHandler {

	@Override
	public void handle(JSONObject data) {
		Integer id = ((Long) data.get("state")).intValue();
		GameState state = GameState.deserialize(id);
		Map<Integer, Integer> images = null;
		
		switch (state) {
			case STATE_WAITING_FOR_CARDS :
				Client.instance.controller.showCzarBox(false);
				Client.instance.controller.setInfoBox("Play a card that matches the round's theme!");
				break;
			case STATE_WAITING_FOR_CZAR :
				images = Client.instance.proxy.requestNewImages();
				
				Client.instance.controller.setNewImages(images);
				Client.instance.controller.showCzarBox(false);
				Client.instance.controller.setCzarTheme("");
				Client.instance.controller.setInfoBox("Waiting for the round master to set the round's theme!");
				break;
			case STATE_WAITING_FOR_CZAR_YOU :
				images = Client.instance.proxy.requestNewImages();
				
				Client.instance.controller.setNewImages(images);
				Client.instance.controller.showCzarBox(true);
				Client.instance.controller.setInfoBox("You are the round master! Set this round's theme!");
				break;
			case STATE_WAITING_FOR_GUESS :
				Client.instance.controller.showCzarBox(false);
				Client.instance.controller.setInfoBox("Guess which card was played by the round master!");
				break;
			case STATE_WAITING_FOR_GUESS_CZAR :
				Client.instance.controller.showCzarBox(false);
				Client.instance.controller.setInfoBox("Wait for other players to guess your card!");
				break;
			case STATE_WAITING_FOR_PLAYERS:
				throw new IllegalArgumentException();
		}
		
		Client.instance.state = state;
	}
	
}
