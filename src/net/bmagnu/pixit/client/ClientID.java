package net.bmagnu.pixit.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Formatter;

public class ClientID {
	public static final String clientID;

	static {
		Path path = Paths.get("./clientuid");

		String id = "";
		try {
			if (Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
				try {
					id = new String(Files.readAllBytes(path));
				} catch (Exception e) {
					id = getUniqueClientID();
					Files.write(path, id.getBytes());
				}
			} else {
				id = getUniqueClientID();
				Files.write(path, id.getBytes());
			}
		} catch (IOException e1) { }
		
		if (id.isEmpty())
			throw new IllegalStateException("Couldn't generate ID");
		
		clientID = id;
	}


	private static String getUniqueClientID() {
		String time = Long.toHexString(Instant.now().toEpochMilli());
		String mac = "null";

		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] macAddr = network.getHardwareAddress();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < macAddr.length; i++) {
				sb.append(String.format("%02X%s", macAddr[i], (i < macAddr.length - 1) ? "-" : ""));
			}

			mac = sb.toString();
		} catch (Exception e) {
		}

		String hash = "";

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			Formatter formatter = new Formatter();
			for (byte b : md.digest((time + mac).getBytes())) {
				formatter.format("%02x", b);
			}
			hash = formatter.toString();
			formatter.close();
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("Unique ID Hash could not be computed");
		}

		return hash;
	}
}
