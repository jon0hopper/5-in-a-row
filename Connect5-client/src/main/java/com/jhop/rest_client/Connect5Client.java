package com.jhop.rest_client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Connect5Client {

	private static final int POLL_RATE = 500;
	private static final String FINNISHED = "finnished";
	private static final String ID = "id";
	private static final String PLAYER2 = "player2";
	private static final String PLAYER1 = "player1";
	private static final String TURN = "turn";

	String name = null;
	String gameID = null;
	Scanner scanner = null;

	public void play() throws IOException {

		scanner = new Scanner(System.in);

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
				while (!game.getBoolean(FINNISHED)) {
					game = waitMyTurn();
					printGame(game);
					if (!game.getBoolean(FINNISHED)) {
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
			
			//quit the game even if we CtlBreak
				
		}

		// TODO quit the game
		// TODO just leave game
		// TODO more unit tests especially for client
		// TODO tidy up and comment
		// TODO write up a document


		System.out.println("Thanks for playing");

	}

	/*
	 * Returns the game as a JSON String This also sets the name
	 */
	public JSONObject startGame() throws IOException {

		JSONObject game = null;
		while (game == null) {

			if (name == null) {
				// if they already have a name, just re-use it
				name = getNameFromUser();
			}
			ConnectionResult result = getREST("startGame?name=" + name);

			if (result.getHttpCode() == 200) {

				// TODO validate the game
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

	String getNameFromUser() {
		System.out.println("What is your name?");
		String newName = scanner.nextLine();
		System.out.println(String.format("Hi %s, Lets Play", newName));

		return newName;
	}

	boolean wantToContinue() {
		System.out.println("Play Again? (Y/N)");

		// TODO input validation
		String answer = scanner.next();
		return answer.equalsIgnoreCase("y");
	}

	private JSONObject waitForOponent(JSONObject game) throws IOException {
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

	private JSONObject waitMyTurn() throws IOException {
		JSONObject newGame = getGameState(gameID);
		System.out.print(String.format("Waiting for %s to move", newGame.getString(TURN)));
		while (!name.equals(newGame.getString(TURN))) {
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

	private void makeAMove() throws IOException, QuitException {
		Boolean allowed = false;
		while (!allowed) {

			System.out.print(String.format("It’s your turn %s, please enter column (1-9) (q to quit):", name));

			// TODO input validation
			String column = scanner.next();

			if (column.equals("q")) {
				throw new QuitException();
			}

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

	/*
	 * Returns the game as a JSON String
	 */
	public JSONObject getGameState(String gameID) throws IOException {

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

	protected ConnectionResult getREST(String request) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/" + request).openConnection();
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

	protected ConnectionResult postREST(String request, JSONObject body) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/" + request).openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; utf-8");

		debug("Post:" + body.toString());

		connection.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
		wr.write(body.toString());
		wr.flush();

		int responseCode = connection.getResponseCode();
		String response = "";

		if (responseCode == 200) {
			Scanner input = new Scanner(connection.getInputStream());
			while (input.hasNextLine()) {
				response += input.nextLine();
			}
			input.close();
		}

		return new ConnectionResult(responseCode, response);
	}

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

				System.out.print("[ " + disk + " ] ");
			}
			System.out.println();
		}
	}

	private void debug(String msg) {
		// System.out.println(msg);
	}

	public static void main(String[] args) throws IOException {
		Connect5Client myApp = new Connect5Client();
		try {
			myApp.play();
		} catch (java.net.ConnectException e) {
			System.out.println("Unable to connect.  Maybe the Server needs to be started");
		}
	}

}