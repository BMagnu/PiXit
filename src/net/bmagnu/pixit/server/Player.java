package net.bmagnu.pixit.server;

import java.util.Map;

public class Player {
	public ClientProxy proxy;
	
	public Integer playerId;
	
	public Map<Integer, Integer> imageSlots; //ImageSlot | ImageId
	
	public Integer points;
}
