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

	//Player 0 ie no winner yet
	@Getter
	Integer winner = 0;

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
		return winner != 0;
	}

	public Integer[][] getBoard() {
		return board;
	}

	/**
	 * Make a move.
	 * 
	 * @param player
	 * @param column 1-9
	 * @return true if its this players turn, and the move is legal
	 */
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
		// Move up through the rows, and put my disk in the first free spot
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
			
			//mark the game
			winner = getWinner(board);
		}

		return legal;
	}

	/**
	 * This runs through the game to see if anyone won A winner is someone with 5 in
	 * a row.
	 * 
	 * @return
	 */
	Integer getWinner(Integer[][] aBoard) {
		Integer winner = 0;

		int inARow = 5;
		// Rows
		Integer last = 0;
		Integer matches = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (aBoard[x][y] == last && last != 0) {
					matches++;
				} else {
					//there is always 1 matching
					matches = 1;
				}
				last = aBoard[x][y];
				if (matches >= inARow) {
					// We have a winner!
					return last;
				}
			}
		}

		// Columns
		last = 0;
		matches = 0;
		for (int x = 0; x < COLUMNS; x++) {
			for (int y = 0; y < ROWS; y++) {
				if (aBoard[x][y] == last && last != 0) {
					matches++;
				} else {
					//there is always 1 matching
					matches = 1;
				}
				last = aBoard[x][y];
				if (matches >= inARow) {
					// We have a winner!
					return last;
				}
			}
		}
			
		// diagonals

		
		//Nooone one yet
		return winner;

	}

//TODO	add function to mark the game

}
