package com.jhop.rest_client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
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
		//{"finnished":false,"id":"f56316d0-bdbb-47ca-bbdd-c57edcddf54d","board":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],"full":true}
		
		String gameID = "f56316d0-bdbb-47ca-bbdd-c57edcddf54d";
		String jsonString = "{\"finnished\":false,\"id\":\""+gameID+"\",\"board\":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],\"full\":true}";
		ConnectionResult response = new ConnectionResult(200, jsonString);
		doReturn(response).when(client).getREST(anyString());
		
		JSONObject game = client.startGame("jhop");
		
		assertEquals(gameID,game.getString("id"));
		
    }
}
