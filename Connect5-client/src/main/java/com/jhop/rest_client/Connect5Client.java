package com.jhop.rest_client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This is a simple client for connecting the the REST api service and playing
 * connect 5
 * 
 * @author jhopper
 *
 */
public class Connect5Client {

	private static final String SERVER_URL = "http://localhost:8080/";
	private static final int POLL_RATE = 500;
	private static final String FINISHED = "finished";
	private static final String ID = "id";
	private static final String PLAYER2 = "player2";
	private static final String PLAYER1 = "player1";
	private static final String TURN = "turn";

	String name = null;
	String gameID = null;
	Scanner scanner = null;

	/**
	 * This is the main function, which actually plays the game. The user can play
	 * as many games as they like
	 * 
	 * @throws IOException
	 */
	public void play(Scanner cin) throws IOException {

		scanner = cin;

		System.out.println("Welcome to 5-in-a-Row");

		try {
			// Start Game
			do {
				JSONObject game = startGame();
				gameID = game.getString(ID);

				// Wait for Opponent
				game = waitForOponent(game);

				debug("game:" + game.toString());
				System.out.println(
						String.format("Player1:%s vs Player2: %s", game.getString(PLAYER1), game.getString(PLAYER2)));

				// while game not over
				while (!game.getBoolean(FINISHED)) {
					game = waitMyTurn();
					printGame(game);
					if (!game.getBoolean(FINISHED)) {
						makeAMove();
					}
					game = getGameState(gameID);
					printGame(game);
				}

			} while (wantToContinue());
		} catch (QuitException e) {
			System.out.println(String.format("Sorry to see you go %s", name));
		} finally {
			scanner.close();
			// quit the game even if we CtlBreak
			quitGame();
		}

		System.out.println("Thanks for playing");

	}

	/**
	 * Start a new game. This function asks the user for their name, and then starts
	 * a game in that name It will re-request the name if that name is already in
	 * use.
	 */
	JSONObject startGame() throws IOException {

		JSONObject game = null;
		while (game == null) {

			if (name == null) {
				// if they already have a name, from a previous game, just re-use it
				name = getNameFromUser();
			}
			ConnectionResult result = getREST(
					"startGame?name=" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));

			if (result.getHttpCode() == 200) {
				// TODO validate the game is a real game, with the correct format
				game = new JSONObject(result.getHttpResponse());

			} else if (result.getHttpCode() == 403) {
				System.out.println(String.format("Unable to start game because name is in use"));
				name = null;
			} else {
				System.out.println(String.format("Unable to start game code %s", result.getHttpCode()));
				// TODO extra error handling here
				name = null;
			}
		}

		// an error happened
		return game;
	}

	/**
	 * The user has decided to quit the game, so do that
	 */
	void quitGame() throws IOException {

		deleteREST("Game/" + gameID + "?name=" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));
		// we are quitting dont really care about the response
	}

	/**
	 * Ask the user for their name. Using this as a small function enables mocking
	 * in unit tests
	 * 
	 * @return
	 */
	String getNameFromUser() {
		System.out.println("What is your name?");
		String newName = scanner.nextLine();
		System.out.println(String.format("Hi %s, Lets Play", newName));

		return newName;
	}

	/**
	 * Ask the user if they want to play again
	 * 
	 * Using this in a small function enables mocking in unit tests
	 * 
	 * @return
	 */
	boolean wantToContinue() {
		System.out.println("Play Again? (Y/N)");

		// TODO input validation
		String answer = scanner.next();
		return answer.equalsIgnoreCase("y");
	}

	/**
	 * After the user has created the game, they need to wait for an oponent
	 * 
	 * This polls the server to wait for someone to play against
	 * 
	 * @param game
	 * @return
	 * @throws IOException
	 */
	JSONObject waitForOponent(JSONObject game) throws IOException {
		JSONObject newGame = game;

		System.out.print(String.format("Waiting for an Oponent"));
		while (!newGame.getBoolean("started")) {
			System.out.print(".");
			// A busy poll isnt great, but it will do for now.
			try {
				Thread.sleep(POLL_RATE);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newGame = getGameState(gameID);
		}

		System.out.println();
		System.out.println(String.format("We have a Game!"));

		return newGame;
	}

	/**
	 * After we have made a move, we need to wait for the other player to make a
	 * move This polls the server, and loops until the other player plays
	 * 
	 * @return
	 * @throws IOException
	 */
	JSONObject waitMyTurn() throws IOException {
		JSONObject newGame = getGameState(gameID);
		System.out.print(String.format("Waiting for %s to move", newGame.getString(TURN)));
		while (!name.equals(newGame.getString(TURN)) && !newGame.getBoolean(FINISHED)) {
			System.out.print(".");
			// A busy poll isnt great, but it will do for now.
			try {
				Thread.sleep(POLL_RATE);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newGame = getGameState(gameID);
		}

		System.out.println();
		return newGame;
	}

	/**
	 * Ask the user for the move, and make that move on the server
	 * 
	 * It loops until the user makes a valid move.
	 * 
	 * @throws IOException
	 * @throws QuitException
	 */
	void makeAMove() throws IOException, QuitException {
		Boolean allowed = false;

		// Loop until the user makes a move that is accepted
		while (!allowed) {

			String column = null;
			do {
				System.out.print(String.format("It’s your turn %s, please enter column (1-9) (q to quit):", name));
				column = scanner.next();

				// user decides to quit
				if (column.equals("q")) {
					throw new QuitException();
				}

			} while (!StringUtils.isNumeric(column) || Integer.valueOf(column) < 1 || Integer.valueOf(column) > 9);

			// TODO object validation
			JSONObject move = new JSONObject();
			move.put("player", name);
			move.put("column", Integer.valueOf(column));

			ConnectionResult result = postREST("Game/" + gameID, move);
			JSONObject moveResult = new JSONObject(result.getHttpResponse());
			debug("result" + moveResult.toString());
			allowed = moveResult.getBoolean("allowed");
			if (!allowed) {
				System.out.println("Move Not allowed:" + moveResult.getString("errorReason"));
			}
		}
	}

	/**
	 * Returns the game as a JSON String
	 */
	JSONObject getGameState(String gameID) throws IOException {

		ConnectionResult result = getREST("Game/" + gameID);

		if (result.getHttpCode() == 200) {

			JSONObject game = new JSONObject(result.getHttpResponse());
			// TODO validate the game
			debug("getGame:" + game.toString());
			return game;
		}

		// some other code
		return null;
	}

	/**
	 * This function does a GET request to the server, and wraps up the
	 * 
	 * @param request
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	ConnectionResult getREST(String request) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_URL + request).openConnection();
		connection.setRequestMethod("GET");

		// TODO handle host not responding

		int responseCode = connection.getResponseCode();
		String response = "";

		if (responseCode == 200) {
			Scanner scanner = new Scanner(connection.getInputStream());
			while (scanner.hasNextLine()) {
				response += scanner.nextLine();
			}
			scanner.close();
		}

		debug("getRest" + response);
		return new ConnectionResult(responseCode, response);

	}

	/**
	 * This does a POST request to the Server
	 * 
	 * @param request
	 * @param body
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	ConnectionResult postREST(String request, JSONObject body) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_URL + request).openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; utf-8");

		debug("Post:" + body.toString());

		connection.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
		wr.write(body.toString());
		wr.flush();

		int responseCode = connection.getResponseCode();
		String response = "";

		Scanner input = new Scanner(connection.getInputStream());
		while (input.hasNextLine()) {
			response += input.nextLine();
		}
		input.close();

		return new ConnectionResult(responseCode, response);
	}

	/**
	 * This does a DELETE request to the server
	 * 
	 * @param request
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	ConnectionResult deleteREST(String request) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_URL + request).openConnection();
		connection.setRequestMethod("DELETE");
		connection.setRequestProperty("Content-Type", "application/json; utf-8");

		int responseCode = connection.getResponseCode();
		String response = "";

		Scanner input = new Scanner(connection.getInputStream());
		while (input.hasNextLine()) {
			response += input.nextLine();
		}
		input.close();

		return new ConnectionResult(responseCode, response);
	}

	/**
	 * Print out the state of the game, using ascii
	 * 
	 * @param game
	 * @throws IOException
	 */
	void printGame(JSONObject game) throws IOException {
		System.out.println();
		System.out.println(String.format(" %s (X) vs %s (O)", game.getString(PLAYER1), game.getString(PLAYER2)));
		printBoard(game);
		int winner = game.getInt("winner");
		if (winner != 0) {
			String winnerName = game.getString("player" + winner);
			System.out.println("----------------------------------------------");

			if (winnerName.equals(name)) {
				// I have won"
				System.out.println(String.format(" Congratulations %s! You won the game", winnerName));
			} else {
				System.out.println(String.format(" Sorry, %s has won the game", winnerName));
			}
			System.out.println("----------------------------------------------");
		}
	}

	/**
	 * Print out the rows and columns of the playing board.
	 * 
	 * @param game
	 */
	void printBoard(JSONObject game) {
		JSONArray board = game.getJSONArray("board");
		List<Object> columns = board.toList();
		ArrayList<Integer> row = (ArrayList) columns.get(0);

		for (int rowNumber = row.size() - 1; rowNumber >= 0; rowNumber--) {
			for (Object c : columns) {
				row = (ArrayList) c;
				char disk = ' ';

				switch (row.get(rowNumber)) {
				case 1:
					disk = 'X';
					break;
				case 2:
					disk = 'O';
					break;
				}

				System.out.print("[" + disk + "]");
			}
			System.out.println();
		}
	}

	/**
	 * This is a small debugging function, for logging out debug log.
	 * 
	 * I could use some sort of logging system, like log4j, but that seems excessive
	 * for this challenge
	 * 
	 * @param msg
	 */
	private void debug(String msg) {
		// System.out.println(msg);
	}

	public static void main(String[] args) throws IOException {
		Connect5Client myApp = new Connect5Client();
		try {
			myApp.play(new Scanner(System.in));
		} catch (java.net.ConnectException e) {
			System.out.println("Unable to connect.  Maybe the Server needs to be started");
		}
	}

}