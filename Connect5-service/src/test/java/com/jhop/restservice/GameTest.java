package com.jhop.restservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {

	@BeforeEach
	void Setup() {

	}

	@AfterEach
	void TearDown() {
	}

	@Test
	void testPlayColumn() {

		Game game = new Game("tim");

		// it isnt started yet, there is only one player
		assertFalse(game.isStarted());

		assertFalse(game.addPlayer("tim"));

		// tim cant play twice
		assertFalse(game.isStarted());

		assertTrue(game.addPlayer("tam"));
		// now we have two players
		assertTrue(game.isStarted());

		// fill a column
		for (int i = 0; i < 6; i++) {
			assertTrue(game.makeMove(game.getTurn(), 1));
		}

		// column is full now
		assertFalse(game.makeMove(game.getTurn(), 1));
	}

	
	@Test
	void testQuit() {

		Game game = new Game("tim");

		// it isnt started yet, there is only one player
		assertFalse(game.isStarted());

		assertTrue(game.addPlayer("tam"));
		// now we have two players
		assertTrue(game.isStarted());

		//play a little
		for (int i = 0; i < 3; i++) {
			assertTrue(game.makeMove(game.getTurn(), 1));
		}

		//quit wrong name
		game.quit("tom");

		//Tom wasnt playing
		assertFalse(game.isFinished());
		assertEquals(0,game.getWinner());

		//quit tam
		game.quit("tam");

		assertTrue(game.isFinished());
		assertEquals(1,game.getWinner());
	}

	@Test
	void testQuit2() {

		Game game = new Game("tim");

		// it isnt started yet, there is only one player
		assertFalse(game.isStarted());

		assertTrue(game.addPlayer("tam"));
		// now we have two players
		assertTrue(game.isStarted());

		//play a little
		for (int i = 0; i < 3; i++) {
			assertTrue(game.makeMove(game.getTurn(), 1));
		}

		//Its ok now
		assertFalse(game.isFinished());
		assertEquals(0,game.getWinner());

		//quit tim
		game.quit("tim");

		assertTrue(game.isFinished());
		assertEquals(2,game.getWinner());
	}
	
	@Test
	void testNoWinnerColumn() {
		Integer[][] board = { { 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(0,winner);
	}

	
	@Test
	void testWinner1Column() {
		Integer[][] board = { { 0,0,0,0,0,0},{ 1,1,1,1,1,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(1,winner);
	}

	@Test
	void testWinner2Column() {
		Integer[][] board = { { 0,0,0,0,0,0},{ 1,1,2,1,1,0},{ 0,2,2,2,2,2},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(2,winner);
	}

	@Test
	void testWinnerLastColumn1() {
		Integer[][] board = { { 0,0,0,0,0,0},{ 1,1,2,1,1,0},{ 0,2,2,1,2,2},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,1,1,1,1,1} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(1,winner);
	}

	@Test
	void testWinnerLastColumn2() {
		Integer[][] board = { { 0,0,0,0,0,0},{ 1,1,2,1,1,0},{ 0,2,2,1,2,2},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 2,2,2,2,2,1} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(2,winner);
	}
	
	
	@Test
	void testWinnerRow1() {
		Integer[][] board = { { 1,0,0,0,0,0},{ 1,1,2,1,1,0},{ 1,2,2,1,2,2},{ 1,0,0,0,0,0},{ 1,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 2,2,2,2,2,1} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(1,winner);
	}

	@Test
	void testWinnerRow2() {
		Integer[][] board = { { 1,0,0,0,0,0},{ 1,1,2,1,1,0},{ 2,2,2,1,2,2},{ 1,0,0,0,0,0},{ 1,0,0,0,0,2},{ 0,0,0,0,0,2},{ 0,0,0,0,0,2},{ 0,0,0,0,0,2},{ 2,2,2,2,2,2} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(2,winner);
	}

	
	@Test
	void testWinnerUpDiagonal() {
		//Diagonal starting at 0,0
		Integer[][] board = { { 1,0,0,0,0,0},{ 0,1,0,0,0,0},{ 0,0,1,0,0,0},{ 0,0,0,1,0,0},{ 0,0,0,0,1,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(1,winner);
	}

	@Test
	void testWinnerUpDiagonal1() {
		//Diagonal starting at 0,0
		Integer[][] board = { { 1,0,0,0,0,0},{ 0,1,0,0,0,0},{ 0,0,2,0,0,0},{ 0,0,0,1,0,0},{ 0,0,0,0,1,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(0,winner);
	}
	
	@Test
	void testWinnerUpDiagonal2() {
		//Diagonal starting at 0,1
		Integer[][] board = { { 1,2,0,0,0,0},{ 0,2,2,0,0,0},{ 0,0,1,2,0,0},{ 0,0,0,1,2,0},{ 0,0,0,0,1,2},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(2,winner);
	}
	
	@Test
	void testWinnerUpDiagonal3() {
		//Diagonal starting at 4,1
		Integer[][] board = { { 1,2,0,0,0,0},{ 0,2,1,0,0,0},{ 0,0,1,2,0,0},{ 0,0,0,1,2,0},{ 0,2,0,0,1,2},{ 0,0,2,0,0,0},{ 0,0,0,2,0,0},{ 0,0,0,0,2,0},{ 0,0,0,0,0,2} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(2,winner);
	}	

	@Test
	void testWinnerUpDiagonal4() {
		//Diagonal starting at 4,0
		Integer[][] board = { { 1,2,0,0,0,0},{ 0,2,1,0,0,0},{ 0,0,1,2,0,0},{ 0,0,0,1,2,0},{ 1,1,0,0,1,2},{ 0,1,2,0,0,0},{ 0,0,1,2,0,0},{ 0,0,0,1,2,0},{ 0,0,0,0,1,2} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(1,winner);
	}	



	@Test
	void testWinnerDownDiagonal() {
		//Diagonal starting at 0,4
		Integer[][] board = { { 0,0,0,0,1,0},{ 0,0,0,1,0,0},{ 0,0,1,1,0,0},{ 0,1,1,0,0,0},{ 1,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(1,winner);
	}

	@Test
	void testWinnerDownDiagonal1() {
		//Diagonal starting at 0,4
		Integer[][] board = { { 0,0,0,0,1,0},{ 0,0,0,1,0,0},{ 0,0,2,1,0,0},{ 0,1,1,0,0,0},{ 1,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(0,winner);
	}
	@Test
	void testWinnerDownDiagonal2() {
		//Diagonal starting at 0,5
		Integer[][] board = { { 0,0,0,0,1,2},{ 0,0,0,2,2,0},{ 0,0,0,2,0,0},{ 0,0,2,0,0,0},{ 0,2,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(2,winner);
	}
	
	@Test
	void testWinnerDownDiagonal3() {
		//Diagonal starting at 4,5
		Integer[][] board = { { 0,0,0,0,1,1},{ 0,0,0,2,2,0},{ 0,0,0,2,0,0},{ 0,0,2,0,0,0},{ 0,2,0,0,0,2},{ 0,0,2,0,2,0},{ 0,0,0,2,0,0},{ 0,0,2,0,2,0},{ 0,2,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(2,winner);
	}

	@Test
	void testWinnerDownDiagonal4() {
		//Diagonal starting at 4,4
		Integer[][] board = { { 0,0,0,0,1,1},{ 0,0,0,2,2,0},{ 0,0,0,2,0,0},{ 0,0,2,0,0,0},{ 0,2,0,0,1,0},{ 0,0,1,1,0,0},{ 0,0,1,2,0,0},{ 0,1,0,0,2,0},{ 1,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(1,winner);
	}

	@Test
	void testWinnerDownDiagonal5() {
		//Diagonal starting at 4,4, one short
		Integer[][] board = { { 0,0,0,0,1,1},{ 0,0,0,2,2,0},{ 0,0,0,2,0,0},{ 0,0,2,0,0,0},{ 0,2,0,0,1,0},{ 0,0,1,1,0,0},{ 0,0,1,2,0,0},{ 0,1,0,0,2,0},{ 0,0,0,0,0,0} };

		Game game = new Game("tim");

		Integer winner = game.getWinner(board);
		assertEquals(0,winner);
	}
	
}
