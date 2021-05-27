package com.jhop.restservice;

import java.util.UUID;

public class Game {
	
	private UUID gameID;

	private String player1=null;
	private String player2=null;;
	
	Integer winner = null;
	
//TODO	add the game board state.  its a list of lists. 
	
	public Game(String name) {
		gameID = java.util.UUID.randomUUID();
		player1 = name;
	}
	
	public boolean addPlayer(String name) {
		if (player2!=null) {
			//the game is already full
			return false;
		} else {
			player2 = name;
			return true;
		}
	}
	
	public String getID() {
		return gameID.toString();
	}
	
	public boolean isFull() {
		return (player1!=null & player2!=null);
	}

	public boolean hasPlayer(String name) {
		return (name.equals(player1) || name.equals(player2));
	}
	
	public boolean isFinnished() {
		return winner!=null;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Game) {
			Game that = (Game) obj;
			this.getID().equals(that.getID());
		}
		return false;
	}
	
	
//TODO	add function to mark the game
	
//TODO	add funtion to make a move.
}
