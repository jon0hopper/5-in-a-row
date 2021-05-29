package com.jhop.restservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class GameServer {

	private Integer count = 0;
	
	Map<String, Game> games = new HashMap<>();
	
	Integer nextInt() {
		return count++;
	}
	
	/*
	 * This starts a game for the user.
	 * 
	 * If there is someone else waiting for a game, match them up.
	 * if there is not, create a new game.
	 */
	public Game startGame(String name){
		Game game =null;
		//Check if the name is already in the list.
		boolean nameInUse = games.values().stream().filter(g -> !g.isFinished()).anyMatch(g -> g.hasPlayer(name));
		
		//If we are already playing, we cant start a new game
		if(nameInUse) {
			//log it
		} else {

			Optional<Game> gameOpt = games.values().stream().filter(g -> !g.isFinished()).filter(g -> !g.isStarted()).findFirst();
			if(gameOpt.isPresent()) {
				gameOpt.get().addPlayer(name);
				game = gameOpt.get();
			} else {
				//create a new game
				game = new Game(name);
				games.put(game.getID(),game);			
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
