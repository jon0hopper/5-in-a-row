package com.jhop.rest_client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for Connect5Client
 */
public class Connect5ClientTest 
{
	
  
	Scanner mockScanner;
	
	Connect5Client client;

	private AutoCloseable mockitoCloseable;
	
	
	@BeforeEach
	void Setup() {
		mockitoCloseable = MockitoAnnotations.openMocks(this);
		client = Mockito.spy(new Connect5Client());	
		mockScanner = new Scanner(System.in);
		
	}
	
	@AfterEach
	void TearDown() throws Exception {
		mockitoCloseable.close();
		
	}

	@Test
	/**
	 * Play a bit of a game
	 */
	public void testPlay() throws IOException, QuitException {
		
		
		String jsonGame = "{\"player1\":\"jhop\",\"player2\":shop,\"board\":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],\"turn\":null,\"winner\":0,\"id\":\"f9965a4a-87c6-41d9-ac99-463c539b1738\",\"started\":false,\"finished\":false}";
		
		JSONObject game = new JSONObject(jsonGame); 
		doReturn(game).when(client).startGame();
		
		//this is the game loop
		doReturn(game).when(client).waitForOponent(any(JSONObject.class));
		
		doReturn(game).when(client).waitMyTurn();
		doNothing().when(client).makeAMove();
		doReturn(game).when(client).getGameState(anyString());

		//do it twice
		doReturn(game).when(client).waitMyTurn();
		doNothing().when(client).makeAMove();
		doReturn(game).when(client).getGameState(anyString());
		
		//this is a finished game
		jsonGame = "{\"player1\":\"jhop\",\"player2\":shop,\"board\":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],\"turn\":null,\"winner\":1,\"id\":\"f9965a4a-87c6-41d9-ac99-463c539b1738\",\"started\":false,\"finished\":true}";
		game = new JSONObject(jsonGame); 

		doReturn(game).when(client).waitMyTurn();
		doReturn(game).when(client).getGameState(anyString());
		
		doReturn(false).when(client).wantToContinue();
		
		doNothing().when(client).quitGame();
		
		client.play(mockScanner);
	}
	
	
	
	@Test
    public void testStartGame() throws MalformedURLException, IOException
    {
		//This is what the REST server returns
		//{"finished":false,"id":"f56316d0-bdbb-47ca-bbdd-c57edcddf54d","board":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],"full":true}
		
		String gameID = "f56316d0-bdbb-47ca-bbdd-c57edcddf54d";
		String jsonString = "{\"finished\":false,\"id\":\""+gameID+"\",\"board\":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],\"full\":true}";
		ConnectionResult response = new ConnectionResult(200, jsonString);
		doReturn(response).when(client).getREST(anyString());
		doReturn("jhop").when(client).getNameFromUser();
		
		JSONObject game = client.startGame();
		
		assertEquals(gameID,game.getString("id"));
		
    }
	
	@Test
	public void printBoard() {
		
		String jsonString = "{\"player1\":\"jhop\",\"player2\":null,\"board\":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],\"turn\":null,\"winner\":0,\"id\":\"8cfa588c-5701-4c91-9093-aaedec63e817\",\"started\":false,\"finished\":false}\r\n}";
		JSONObject game = new JSONObject(jsonString);
		
		client.printBoard(game);
		
		//There is no real validation of this test. Look at it on the screen, and it doesnt throw exceptions
		
	}
}
