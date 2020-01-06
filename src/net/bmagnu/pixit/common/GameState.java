package net.bmagnu.pixit.common;

import java.util.HashMap;
import java.util.Map;

public enum GameState {

	STATE_WAITING_FOR_CARDS(0),
	STATE_WAITING_FOR_CZAR(1),
	STATE_WAITING_FOR_CZAR_YOU(2),
	STATE_WAITING_FOR_GUESS(3),
	STATE_WAITING_FOR_GUESS_CZAR(4);

	private int serialId;
	private static Map<Integer, GameState> serializationMap = new HashMap<>();
	
	private GameState(int serialId){
		this.serialId = serialId;
	}
	
	public static GameState deserialize(int serialId) {
		return serializationMap.get(serialId);
	}
	
	public int serialize() {
		return serialId;
	}
	
	static {
		for(GameState state : GameState.values()) {
			serializationMap.put(state.serialId, state);
		}
	}
}
