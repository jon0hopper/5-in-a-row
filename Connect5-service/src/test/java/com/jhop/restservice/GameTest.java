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
	
}
