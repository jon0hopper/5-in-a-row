package com.jhop.restservice;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
	void helloWorld() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.get("/greeting")
	    	      .contentType(MediaType.APPLICATION_JSON))
	    	      .andExpect(status().isOk())
	    	      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
	    	      .andExpect(jsonPath("$.id", is(1)))
	    	      .andExpect(jsonPath("$.content").value(containsString("World")));
	}
	
	
	//@Test
	void startGame() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.get("/startGame?name=jhop")
	    	      .contentType(MediaType.APPLICATION_JSON))
	    	      .andExpect(status().isOk())
	    	      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
	    	      .andExpect(jsonPath("$.id", is(1)))
	    	      .andExpect(jsonPath("$.content").value(containsString("World")));
	    
//TODO	    work out how to save the ID that comes back here, and compare it to the next call.
	    
	}
	
	
}
