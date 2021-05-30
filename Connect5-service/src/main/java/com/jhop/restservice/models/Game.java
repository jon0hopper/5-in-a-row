package com.jhop.restservice.models;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import lombok.Getter;

/**
 * This is the Connect5 Game. This is the state of the game, and the logic to
 * make it playable
 * 
 * @author jhopper
 *
 */
public class Game {

	private UUID gameID;

	@Getter
	private String player1 = null;
	@Getter
	private String player2 = null;

	private static int COLUMNS = 9;
	private static int ROWS = 6;

	@Getter
	Integer[][] board = new Integer[COLUMNS][ROWS];

	@Getter
	private String turn = null;

	// Player 0 ie no winner yet
	@Getter
	Integer winner = 0;

	/**
	 * Construct a new game, and give it a random ID
	 * 
	 * @param name
	 */
	public Game(String name) {
		gameID = java.util.UUID.randomUUID();
		player1 = name;

		// Fill each row with 0.
		for (Integer[] row : board) {
			Arrays.fill(row, 0);
		}
	}

	/**
	 * Add a player to the game. This is always player2, because the constructor
	 * sets player1
	 * 
	 * Once a second player is added, the game starts
	 * 
	 * @param name
	 * @return false if the player is already playing, or the game is full
	 */
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
		// Actually we could just check player2, but this is more readable
		return (player1 != null & player2 != null);
	}

	/**
	 * Is this player one of the two players on this game?
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasPlayer(String name) {
		return (name.equals(player1) || name.equals(player2));
	}

	public boolean isFinished() {
		return winner != 0;
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

			// mark the game
			winner = getWinner(board);
		}

		return legal;
	}

	/**
	 * This runs through the game to see if anyone won. A winner is someone with 5
	 * in a row. up, across, or diagonal
	 * 
	 * @return The winner. Returns 0 if there is no winner
	 */
	Integer getWinner(Integer[][] aBoard) {
		Integer winner = 0;

		int inARow = 5;
		// Rows
		winner = markRows(aBoard, inARow);
		if (winner != 0) {
			return winner;
		}
		// Columns
		winner = markColumns(aBoard, inARow);
		if (winner != 0) {
			return winner;
		}
		// diagonals
		winner = markUpDiagonals(aBoard, inARow);
		if (winner != 0) {
			return winner;
		}

		// down lines
		winner = markDownDiagonals(aBoard, inARow);

		return winner;

	}

	/**
	 * This looks for a winner in rows
	 * 
	 * @param aBoard
	 * @param inARow the number needed in a row to win
	 * @return
	 */
	private Integer markRows(Integer[][] aBoard, int inARow) {
		Integer last = 0;
		Integer matches = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (aBoard[x][y] == last && last != 0) {
					matches++;
				} else {
					// there is always 1 matching
					matches = 1;
				}
				last = aBoard[x][y];
				if (matches >= inARow) {
					// We have a winner!
					return last;
				}
			}
		}
		// noone won
		return 0;
	}

	/**
	 * This looks for a winner in Columns
	 * 
	 * @param aBoard
	 * @param inARow
	 * @return
	 */
	private Integer markColumns(Integer[][] aBoard, int inARow) {
		Integer last = 0;
		Integer matches = 0;
		for (int x = 0; x < COLUMNS; x++) {
			for (int y = 0; y < ROWS; y++) {
				if (aBoard[x][y] == last && last != 0) {
					matches++;
				} else {
					// there is always 1 matching
					matches = 1;
				}
				last = aBoard[x][y];
				if (matches >= inARow) {
					// We have a winner!
					return last;
				}
			}
		}
		// noone won
		return 0;
	}

	/**
	 * This looks for a winner in Diagonals going up Marking diagonals is harder. Up
	 * lines can only start in:
	 * 
	 * 1) columns up to COLUMNS-inARow 2) rows up to Rows-inARow
	 * 
	 * @param aBoard
	 * @param inARow
	 * @return
	 */
	private Integer markUpDiagonals(Integer[][] aBoard, int inARow) {

		for (int x = 0; x <= COLUMNS - inARow + 1; x++) {
			for (int y = 0; y <= ROWS - inARow; y++) {
				// This is the possible start of the row
				Integer disk = aBoard[x][y];
				if (disk == 0) {
					// its an empty slot, move to next slot
					continue;
				}

				boolean won = true;
				for (int n = 1; n < inARow; n++) {
					if (disk != aBoard[x + n][y + n]) {
						// didnt match, dont continue
						won = false;
						break;
					}
				}
				if (won) {
					return disk;
				}
			}
		}
		// noone won
		return 0;
	}

	/**
	 * This looks for a winner in Diagonals going down Marking diagonals is harder.
	 * Down lines can only start in:
	 * 
	 * 1) columns up to Columns-inARow 2) rows up from inARow-1;
	 * 
	 * @param aBoard
	 * @param inARow
	 * @return
	 */
	private Integer markDownDiagonals(Integer[][] aBoard, int inARow) {

		for (int x = 0; x <= COLUMNS - inARow; x++) {
			for (int y = inARow - 1; y < ROWS; y++) {
				// This is the possible start of the row
				Integer disk = aBoard[x][y];
				if (disk == 0) {
					// its an empty slot, move to next slot
					continue;
				}

				boolean won = true;
				for (int n = 1; n < inARow; n++) {
					if (disk != aBoard[x + n][y - n]) {
						// didnt match, dont continue
						won = false;
						break;
					}
				}
				if (won) {
					return disk;
				}
			}
		}
		// no one won
		return 0;
	}

	/**
	 * This player wants to quit the game. The other player will win.
	 * 
	 * @param name
	 */
	public void quit(String name) {
		if (player1.equals(name)) {
			winner = 2;
		} else if (player2.equals(name)) {
			winner = 1;
		}
	}

}
