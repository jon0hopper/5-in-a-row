package com.jhop.rest_client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

/**
 * Unit test for Connect5Client
 */
public class Connect5ClientTest 
{
	
	Connect5Client client;
	
	@BeforeEach
	void Setup() {
		client = Mockito.spy(new Connect5Client());	
	}
	
	@AfterEach
	void TearDown() {
		
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
		//  TODO update the response 
		//assertEquals("jhop",game.getString("player1"));
		
    }
	
	@Test
	public void printBoard() {
		
		String jsonString = "{\"player1\":\"jhop\",\"player2\":null,\"board\":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],\"turn\":null,\"winner\":0,\"id\":\"8cfa588c-5701-4c91-9093-aaedec63e817\",\"started\":false,\"finished\":false}\r\n}";
		JSONObject game = new JSONObject(jsonString);
		
		client.printBoard(game);
		
		//There is no real validation of this test. Look at it on the screen, and it doesnt throw exceptions
		
	}
}
