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
				break;
			case STATE_WAITING_FOR_CZAR :
				images = Client.instance.proxy.requestNewImages();
				
				Client.instance.controller.setNewImages(images);
				Client.instance.controller.showCzarBox(false);
				break;
			case STATE_WAITING_FOR_CZAR_YOU :
				images = Client.instance.proxy.requestNewImages();
				
				Client.instance.controller.setNewImages(images);
				Client.instance.controller.showCzarBox(true);
				break;
			case STATE_WAITING_FOR_GUESS :
			case STATE_WAITING_FOR_GUESS_CZAR :
				Client.instance.controller.showCzarBox(false);
				break;			
		}
		
		Client.instance.state = state;
	}
	
}
