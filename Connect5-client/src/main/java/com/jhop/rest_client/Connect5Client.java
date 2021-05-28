package com.jhop.rest_client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.JSONObject;

public class Connect5Client {

	
	String name = null;
	String gameID = null;
	Scanner scanner = null;
	
	public void play() throws IOException {

		scanner = new Scanner(System.in);

		System.out.println("Welcome to 5-in-a-Row");

		System.out.println("What is your name?");
		name = scanner.nextLine();
		System.out.println(String.format("Hi %s, Lets Play", name));

		// Start Game
		JSONObject game = startGame(name);
		gameID = game.getString("id");

		// Wait for Opponent
		game = waitForOponent(game);
		
		System.out.println("game:" + game.toString());
		System.out.println(
				String.format("Player1:%s vs Player2: %s", game.getString("player1"), game.getString("player2")));
		
		// while game not over
		while(!game.getBoolean("finnished")){
		// waitMyTurn
			game = waitMyTurn();
		// make move
			makeAMove();
		}

		scanner.close();

		System.out.println("Thanks for using PICLER.");

	}



	/*
	 * Returns the game as a JSON String
	 */
	public JSONObject startGame(String name) throws IOException {

		ConnectionResult result = getREST("startGame?name=" + name);
		if (result.getHttpCode() == 200) {
			
			// TODO validate the game
			
			JSONObject game = new JSONObject(result.getHttpResponse());
			
			return game;
		}
		// an error happened
		return null;
	}


	private JSONObject waitForOponent(JSONObject game) throws IOException {
		JSONObject newGame = game;
		
		System.out.print(String.format("Waiting for an Oponent"));
		while (!newGame.getBoolean("started")) {
			System.out.print(".");
			// A busy poll isnt great, but it will do for now.
			try {
				Thread.sleep(500);
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
		System.out.print(String.format("Waiting for My Turn"));
		while (!name.equals(newGame.getString("turn"))) {
			System.out.print(".");
			// A busy poll isnt great, but it will do for now.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newGame = getGameState(gameID);
		}
		
		System.out.println();
		return newGame;
	}
	
	private void makeAMove() throws IOException {
		
		Boolean allowed = false;
		while(!allowed) {
			
			System.out.print(String.format("It’s your turn %s, please enter column (1-9):",name));
			
			//TODO make it 1-9 only
			Integer column = scanner.nextInt();
			
			JSONObject move = new JSONObject();
			move.put("player", name);
			move.put("column", column);
				
			ConnectionResult result = postREST("Game/" + gameID, move);
			JSONObject moveResult = new JSONObject(result.getHttpResponse());
			System.out.println("result" + moveResult.toString());
			allowed = moveResult.getBoolean("allowed");
			if(!allowed) {
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

			System.out.println("getGame:" + game.toString());
			return game;
		}

		// an error happened
		return null;
	}

	
	
	protected ConnectionResult getREST(String request) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/" + request).openConnection();
		connection.setRequestMethod("GET");

		int responseCode = connection.getResponseCode();
		String response = "";

		if (responseCode == 200) {
			Scanner scanner = new Scanner(connection.getInputStream());
			while (scanner.hasNextLine()) {
				response += scanner.nextLine();
			}
			scanner.close();
		}

		System.out.println("getRest" + response);
		return new ConnectionResult(responseCode, response);

	}

	protected ConnectionResult postREST(String request,JSONObject body) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/" + request).openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; utf-8");

		System.out.println("Post:" + body.toString());
		
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
	
	
	
	public static void main(String[] args) throws IOException {
		Connect5Client myApp = new Connect5Client();
		myApp.play();
	}


}