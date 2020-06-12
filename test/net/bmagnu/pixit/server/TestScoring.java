package net.bmagnu.pixit.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.LinkedBlockingDeque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.bmagnu.pixit.common.PiXitImage;

public class TestScoring {

	GameServer server;
	Player p1, p2, p3, p4;
	
	@BeforeEach
	void setupMock() {
		
		ClientProxy mockedProxy = mock(ClientProxy.class); //Player Proxy
		Server.instance = mock(Server.class);
		Server.instance.execute = new LinkedBlockingDeque<Runnable>();

		server = new GameServer();
		server.currentPlayer = 0;
		
		p1 = new Player();
		p1.proxy = mockedProxy;
		p1.name = "A";
		p1.playerId = 0;
		p1.points = 0;
		server.players.add(p1);
		
		p2 = new Player();
		p2.proxy = mockedProxy;
		p2.name = "B";
		p2.playerId = 1;
		p2.points = 0;
		server.players.add(p2);
		
		p3 = new Player();
		p3.proxy = mockedProxy;
		p3.name = "C";
		p3.playerId = 2;
		p3.points = 0;
		server.players.add(p3);
		
		p4 = new Player();
		p4.proxy = mockedProxy;
		p4.name = "D";
		p4.playerId = 3;
		p4.points = 0;
		server.players.add(p4);
		
		PiXitImage img1 = new PiXitImage("", 0);
		server.currentImages.put(img1, 0);
		server.imagesById.put(0,  img1);
		
		PiXitImage img2 = new PiXitImage("", 1);
		server.currentImages.put(img2, 1);
		server.imagesById.put(1,  img2);
		
		PiXitImage img3 = new PiXitImage("", 2);
		server.currentImages.put(img3, 2);
		server.imagesById.put(2,  img3);
		
		PiXitImage img4 = new PiXitImage("", 3);
		server.currentImages.put(img4, 3);
		server.imagesById.put(3,  img4);
	}
	
	@Test
    void checkScoringAllWrong() {
		server.currentImageGuesses.put(1, 3);
		server.currentImageGuesses.put(2, 1);
		server.currentImageGuesses.put(3, 1);
		
		server.processAllImagesGuessed();
		
		assertEquals(0, p1.points);
        assertEquals(Settings.POINTS_GUESSED * 2, p2.points);
        assertEquals(0, p3.points);
        assertEquals(Settings.POINTS_GUESSED, p4.points);
    }
	
	@Test
    void checkScoringAllRight() {
		server.currentImageGuesses.put(1, 0);
		server.currentImageGuesses.put(2, 0);
		server.currentImageGuesses.put(3, 0);
		
		server.processAllImagesGuessed();
		
		assertEquals(0, p1.points);
        assertEquals(Settings.POINTS_CORRECT_GUESS, p2.points);
        assertEquals(Settings.POINTS_CORRECT_GUESS, p3.points);
        assertEquals(Settings.POINTS_CORRECT_GUESS, p4.points);
    }
	
	@Test
    void checkScoringSomeRight() {
		server.currentImageGuesses.put(1, 0);
		server.currentImageGuesses.put(2, 0);
		server.currentImageGuesses.put(3, 1);
		
		server.processAllImagesGuessed();
		
		assertEquals(Settings.POINTS_GOOD_CZAR, p1.points);
        assertEquals(Settings.POINTS_CORRECT_GUESS + Settings.POINTS_GUESSED, p2.points);
        assertEquals(Settings.POINTS_CORRECT_GUESS, p3.points);
        assertEquals(0, p4.points);
    }
	
}
