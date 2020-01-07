package net.bmagnu.pixit.common;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Settings {
	
	private static boolean loadFromFile = true;
	
	public static final boolean ALLOW_EMPTY_THEME;
	public static final int PORT_SERVER;
	public static final short NUM_PLAYERS_TO_START;
	
	public static final short POINTS_CORRECT_GUESS;
	public static final short POINTS_GOOD_CZAR;
	public static final short POINTS_GUESSED;
	
	public static final short MIN_CZAR_DELTA;
	public static final short IMAGE_COUNT;
	
	public static final short POST_ROUND_WAIT;
	
	static {
		//Load Config from File
		
		boolean ALLOW_EMPTY_THEME_Loc = false;
		int PORT_SERVER_Loc = 53415;
		short NUM_PLAYERS_TO_START_Loc = 4;
		
		short POINTS_CORRECT_GUESS_Loc = 3;
		short POINTS_GOOD_CZAR_Loc = 2;
		short POINTS_GUESSED_Loc = 1;
		
		short MIN_CZAR_DELTA_Loc = 1;
		short IMAGE_COUNT_Loc = 7;
		
		short POST_ROUND_WAIT_Loc = 5000;
		
		if(loadFromFile) {
			try {
				String data = new String(Files.readAllBytes(Paths.get("./config.json")));
				
				JSONParser parser = new JSONParser();
				JSONObject jsonIn = (JSONObject) parser.parse(data);
				
				ALLOW_EMPTY_THEME_Loc = (boolean) jsonIn.get("allowEmptyTheme");
				PORT_SERVER_Loc = ((Long) jsonIn.get("portServer")).intValue();
				NUM_PLAYERS_TO_START_Loc = ((Long) jsonIn.get("numPlayers")).shortValue();
				
				POINTS_CORRECT_GUESS_Loc = ((Long) jsonIn.get("pointsCorrectGuess")).shortValue();
				POINTS_GOOD_CZAR_Loc = ((Long) jsonIn.get("pointsGoodCzar")).shortValue();
				POINTS_GUESSED_Loc = ((Long) jsonIn.get("pointsGotGuessed")).shortValue();
				
				MIN_CZAR_DELTA_Loc = ((Long) jsonIn.get("minimumCzarDelta")).shortValue();
				IMAGE_COUNT_Loc = ((Long) jsonIn.get("imagesPerPlayer")).shortValue();
				
				POST_ROUND_WAIT_Loc = ((Long) jsonIn.get("postRoundWait")).shortValue();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		ALLOW_EMPTY_THEME = ALLOW_EMPTY_THEME_Loc;
		PORT_SERVER = PORT_SERVER_Loc;
		NUM_PLAYERS_TO_START = NUM_PLAYERS_TO_START_Loc;
			
		POINTS_CORRECT_GUESS = POINTS_CORRECT_GUESS_Loc;
		POINTS_GOOD_CZAR = POINTS_GOOD_CZAR_Loc;
		POINTS_GUESSED = POINTS_GUESSED_Loc;
		
		MIN_CZAR_DELTA = MIN_CZAR_DELTA_Loc;
		IMAGE_COUNT = IMAGE_COUNT_Loc; 
		
		POST_ROUND_WAIT = POST_ROUND_WAIT_Loc;
	}
	
	
}
