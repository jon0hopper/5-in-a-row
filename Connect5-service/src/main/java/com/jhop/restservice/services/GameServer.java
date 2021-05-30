package com.jhop.restservice.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jhop.restservice.models.Game;

@Service
public class GameServer {

	// Store the games in a HashMap. Ideally there would be some persistent storage,
	// but we don't need that for this challenge
	Map<String, Game> games = new HashMap<>();

	/**
	 * This starts a game for the user.
	 * 
	 * If there is someone else waiting for a game, match them up. if there is not,
	 * create a new game.
	 */
	public Game startGame(String name) {
		Game game = null;
		// Check if the name is already in the list.
		boolean nameInUse = games.values().stream().filter(g -> !g.isFinished()).anyMatch(g -> g.hasPlayer(name));

		// If we are already playing, we can't start a new game
		if (!nameInUse) {

			// Try to join an existing game.
			// Get the first game that isn't finished or started
			Optional<Game> gameOpt = games.values().stream().filter(g -> !g.isFinished()).filter(g -> !g.isStarted())
					.findFirst();
			if (gameOpt.isPresent()) {
				gameOpt.get().addPlayer(name);
				game = gameOpt.get();
			} else {
				// no game was found to join, so create a new game
				// for this user
				game = new Game(name);
				games.put(game.getID(), game);
			}
		}

		return game;
	}

	/**
	 * Returns the game, or null if none found
	 */
	public Game getGame(String id) {
		return games.get(id);
	}

}
