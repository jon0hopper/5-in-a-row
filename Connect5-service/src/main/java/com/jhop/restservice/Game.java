package com.jhop.restservice;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import lombok.Getter;

public class Game {

	private UUID gameID;

	@Getter
	private String player1 = null;
	@Getter
	private String player2 = null;

	private static int COLUMNS = 9;
	private static int ROWS = 6;
	
	Integer[][] board = new Integer[COLUMNS][ROWS];

	@Getter
	private String turn = null;

	Integer winner = null;

	public Game(String name) {
		gameID = java.util.UUID.randomUUID();
		player1 = name;

		// Fill each row with 0.
		for (Integer[] row : board) {
			Arrays.fill(row, 0);
		}
	}

	public boolean addPlayer(String name) {
		if (player2 != null || player1.equals(name)) {
			// the game is already full
			// or the same player added twice
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
		return (player1 != null & player2 != null);
	}

	public boolean hasPlayer(String name) {
		return (name.equals(player1) || name.equals(player2));
	}

	public boolean isFinnished() {
		return winner != null;
	}

	public Integer[][] getBoard() {
		return board;
	}

	public boolean makeMove(String player, Integer column) {

		if (column < 1 || column > COLUMNS) {
			// invalid column
			return false;
		}

		// check if its my turn
		if (!player.equals(turn)) {
			// Its not your turn
			return false;
		}

		// get my token
		int disk = 1;
		if (player.equals(player2)) {
			disk = 2;
		}

		// make the move
		Integer[] row = board[column - 1];

		boolean legal = false;
		for (int i = 0; i < ROWS; i++) {
			if (row[i] == 0) {
				row[i] = disk;
				legal = true;
				break;
			}
		}
		if (legal) {
			// change players turn
			if (player1.equals(player)) {
				turn = player2;
			} else {
				turn = player1;
			}
		}

		return legal;
	}

//TODO	add function to mark the game

//TODO	add funtion to make a move.
}
