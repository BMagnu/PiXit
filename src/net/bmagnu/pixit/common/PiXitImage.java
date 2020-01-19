package net.bmagnu.pixit.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class PiXitImage {
	

	public final int imageId;
	
	public final String hash;
	
	public PiXitImage(String hash, int imageId) {
		this.imageId = imageId;
		this.hash = hash;
	}
	
	public static String makeHash(File file) throws IOException {
		InputStream imageStream = new FileInputStream(file);
		
		byte[] imageData = imageStream.readAllBytes();
		String hash = "";
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			Formatter formatter = new Formatter();
			for (byte b : md.digest(imageData)) {
			    formatter.format("%02x", b);
			}
			hash = formatter.toString();
			formatter.close();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		
		imageStream.close();
		
		return hash;
	}
	
}
