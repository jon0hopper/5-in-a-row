package com.jhop.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class GameServer {

	private Integer count = 0;
	
	List<Game> games = new ArrayList<>();
	
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
		boolean nameInUse = games.stream().filter(g -> !g.isFinnished()).anyMatch(g -> g.hasPlayer(name));
		
		//If we are already playing, we cant start a new game
		if(nameInUse) {
			//log it
		} else {

			Optional<Game> gameOpt = games.stream().filter(g -> !g.isFinnished()).filter(g -> !g.isFull()).findFirst();
			if(gameOpt.isPresent()) {
				gameOpt.get().addPlayer(name);
				game = gameOpt.get();
			} else {
				//create a new game
				game = new Game(name);
				games.add(game);			
			}
		}
		
		return game;
	}
}
