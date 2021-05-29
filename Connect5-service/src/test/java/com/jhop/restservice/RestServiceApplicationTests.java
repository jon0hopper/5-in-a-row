package com.jhop.restservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class RestServiceApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Test
	void contextLoads() {
	}

	@Test
	void startGame() throws Exception {
		MvcResult call1 = mvc
				.perform(MockMvcRequestBuilders.get("/startGame?name=jhop").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.finished", is(false)))
				.andExpect(jsonPath("$.started", is(false))).andReturn();

		String result1 = call1.getResponse().getContentAsString();
		// now get the id out of result1, and see that it is what we need.

		mvc.perform(MockMvcRequestBuilders.get("/startGame?name=shop").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.finished", is(false)))
				.andExpect(jsonPath("$.started", is(true)));

	}

	@Test
	void getGame() throws Exception {
		MvcResult response = mvc
				.perform(MockMvcRequestBuilders.get("/startGame?name=ben").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.finished", is(false)))
				.andExpect(jsonPath("$.started", is(false))).andReturn();

		JSONObject game1 = new JSONObject(response.getResponse().getContentAsString());

		String gameID = game1.getString("id");

		response = mvc.perform(MockMvcRequestBuilders.get("/Game/" + gameID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.finished", is(false)))
				.andExpect(jsonPath("$.started", is(false))).andReturn();

		JSONObject game2 = new JSONObject(response.getResponse().getContentAsString());

		// Now check the two games are the same
		assertEquals(game1.get("id"), game2.getString("id"));
		
		//Start the game, so that future tests work
		mvc
				.perform(MockMvcRequestBuilders.get("/startGame?name=brad").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.finished", is(false)))
				.andExpect(jsonPath("$.started", is(true))).andReturn();
		

	}

	@Test
	void findOponent() throws Exception {
		MvcResult response = mvc
				.perform(MockMvcRequestBuilders.get("/startGame?name=ted").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.finished", is(false)))
				.andExpect(jsonPath("$.started", is(false))).andReturn();

		JSONObject tedsGame = new JSONObject(response.getResponse().getContentAsString());

		response = mvc
				.perform(MockMvcRequestBuilders.get("/startGame?name=bill").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.finished", is(false)))
				.andExpect(jsonPath("$.started", is(true))).andReturn();

		JSONObject billsGame = new JSONObject(response.getResponse().getContentAsString());

		// Now check the two games are the same
		assertEquals(tedsGame.get("id"), billsGame.getString("id"));

		String gameID = tedsGame.getString("id");

		response = mvc.perform(MockMvcRequestBuilders.get("/Game/" + gameID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.finished", is(false)))
				.andExpect(jsonPath("$.started", is(true))).andReturn();

		JSONObject theGame = new JSONObject(response.getResponse().getContentAsString());
		// Now check the two games are the same
		assertEquals(tedsGame.get("id"), theGame.getString("id"));
	}

	@Test
	void makeAMove() throws Exception {

		// We checked this works above, just put it in now
		MvcResult response = mvc
				.perform(MockMvcRequestBuilders.get("/startGame?name=jack").contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		response = mvc
				.perform(MockMvcRequestBuilders.get("/startGame?name=jill").contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		JSONObject theGame = new JSONObject(response.getResponse().getContentAsString());

		String gameID = theGame.getString("id");
		String player = theGame.getString("turn");

		String jsonMove = "{\"column\":2,\"player\":\""+player +"\" }";

		response = mvc.perform(MockMvcRequestBuilders.post("/Game/" + gameID).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(jsonMove))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.allowed", is(true)))
				.andReturn();


		//Same player goes again, not allowed
		response = mvc.perform(MockMvcRequestBuilders.post("/Game/" + gameID).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(jsonMove))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.allowed", is(false)))
				.andReturn();
		
		//Other Player is allowed
		response = mvc
				.perform(MockMvcRequestBuilders.get("/Game/" + gameID).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		theGame = new JSONObject(response.getResponse().getContentAsString());
		player = theGame.getString("turn");

		jsonMove = "{\"column\":2,\"player\":\""+player +"\" }";

		response = mvc.perform(MockMvcRequestBuilders.post("/Game/" + gameID).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(jsonMove))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.allowed", is(true)))
				.andReturn();

	}

	@Test
	void quitAGame() throws Exception {

		// We checked this works above, just put it in now
		MvcResult response = mvc
				.perform(MockMvcRequestBuilders.get("/startGame?name=mary").contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		response = mvc
				.perform(MockMvcRequestBuilders.get("/startGame?name=jane").contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		JSONObject theGame = new JSONObject(response.getResponse().getContentAsString());

		String gameID = theGame.getString("id");
		String player = theGame.getString("turn");

		String jsonMove = "{\"column\":2,\"player\":\""+player +"\" }";

		response = mvc.perform(MockMvcRequestBuilders.post("/Game/" + gameID).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(jsonMove))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		theGame = new JSONObject(response.getResponse().getContentAsString());
		
	
		//mary quits the game
		mvc.perform(MockMvcRequestBuilders.delete("/Game/" + gameID + "?name=mary").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(jsonMove))
				.andExpect(MockMvcResultMatchers.status().isOk());


		//see that the game is over
		response = mvc.perform(MockMvcRequestBuilders.get("/Game/" + gameID).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.winner", is(2)))
				.andExpect(jsonPath("$.finished", is(true)))				
				.andReturn();

		theGame = new JSONObject(response.getResponse().getContentAsString());

	}
}
