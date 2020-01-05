package net.bmagnu.pixit.server.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.json.simple.JSONObject;

import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class LoadImage implements ClientMessageHandler {

	@SuppressWarnings ("unchecked")
	@Override
	public JSONObject handle(JSONObject data, GameServer server) {
		Integer imageID = ((Long) data.get("id")).intValue();
		String imagePath = server.images.get(imageID);
		
		try {
			byte[] image = Files.readAllBytes(Paths.get(imagePath));
			String encodedImage = Base64.getEncoder().encodeToString(image);
			
			JSONObject json = new JSONObject();
			json.put("success", true);
			json.put("image", encodedImage);
			
			return json;
		} catch (IOException e) {
			e.printStackTrace();
			return getDefaultFail(e.getMessage());
		}
	}
}
