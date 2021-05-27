package com.jhop.restservice;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import lombok.Getter;

public class Game {
	
	private UUID gameID;

	@Getter
	private String player1=null;
	@Getter
	private String player2=null;
	
	Integer[][] board = new Integer[5][6];
	
	@Getter
	private String turn=null;
	
	Integer winner = null;

	public Game(String name) {
		gameID = java.util.UUID.randomUUID();
		player1 = name;
		
        // Fill each row with 10. 
        for (Integer[] row : board) {
            Arrays.fill(row, 0);
        }   
	}
	
	public boolean addPlayer(String name) {
		if (player2 != null) {
			// the game is already full
			return false;
		} else {
			player2 = name;

			// Lets make it random who starts
			Random rd = new Random();
			if (rd.nextBoolean()) {
				turn = player2;
			} else {
				turn = player1;
			}

			return true;
		}
	}
	
	public String getID() {
		return gameID.toString();
	}
	
	
	public boolean isStarted() {
		return (player1!=null & player2!=null);
	}

	public boolean hasPlayer(String name) {
		return (name.equals(player1) || name.equals(player2));
	}
	
	public boolean isFinnished() {
		return winner!=null;
	}

	public Integer[][] getBoard(){
		return board;
	}
	
	public boolean makeMove(String player, Integer column) {
		//TODO take real turns
		if(!player.equals(turn)) {
			//Its not your turn
			return false;
		}
		
		if(player1.equals(player)) {
			turn = player2;
		} else {
			turn = player1;
		}
		
		return true;
	}
	
//TODO	add function to mark the game
	
//TODO	add funtion to make a move.
}
