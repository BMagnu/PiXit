package net.bmagnu.pixit.server;

import java.util.Map;

import net.bmagnu.pixit.common.PiXitImage;

public class Player {
	public ClientProxy proxy;
	
	public Integer playerId;
	
	public String name;
	
	public Map<Integer, PiXitImage> imageSlots; //ImageSlot | ImageId
	
	public int points;
}
