package net.bmagnu.pixit.server.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.google.gson.JsonObject;

import net.bmagnu.pixit.server.ClientMessageHandler;
import net.bmagnu.pixit.server.GameServer;

public class LoadImage implements ClientMessageHandler {

	@Override
	public JsonObject handle(JsonObject data, GameServer server) {
		Integer imageID = data.get("id").getAsInt();
		String imagePath = server.images.get(imageID);
		
		try {
			byte[] image = Files.readAllBytes(Paths.get(imagePath));
			String encodedImage = Base64.getEncoder().encodeToString(image);
			
			JsonObject json = new JsonObject();
			json.addProperty("success", true);
			json.addProperty("image", encodedImage);
			
			return json;
		} catch (IOException e) {
			e.printStackTrace();
			return getDefaultFail(e.getMessage());
		}
	}
}
