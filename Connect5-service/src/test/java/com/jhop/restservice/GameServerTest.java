package com.jhop.restservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jhop.restservice.models.Game;
import com.jhop.restservice.services.GameServer;

public class GameServerTest {

	GameServer gameServer;

	@BeforeEach
	void Setup() {
		gameServer = new GameServer();
	}

	@AfterEach
	void TearDown() {
		gameServer = null;
	}

	@Test
	void testBasic() {

		// Add the first game
		Game game = gameServer.startGame("jhop");
		assertTrue(game != null);

		// Try to add a second game, you cant
		assertTrue(gameServer.startGame("jhop") == null);

		// A new player joins the game
		assertEquals(gameServer.startGame("shop").getID(), game.getID());

		// A new player cant join a second game
		assertTrue(gameServer.startGame("shop") == null);

		// Third player joins the game
		assertNotEquals(gameServer.startGame("dhop").getID(), game.getID());

		// now that we have some games, test we can fetch it
		Game fetchGame = gameServer.getGame(game.getID());
		assertEquals(game.getID(), fetchGame.getID());

	}

	@Test
	void testQuits() {

		// Add the first game
		Game game = gameServer.startGame("jhop");
		assertTrue(game != null);

		game.quit("jhop");

		// A new player joins the game, but its already finished
		assertNotEquals(gameServer.startGame("shop").getID(), game.getID());

	}

}
