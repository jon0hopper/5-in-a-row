package com.jhop.restservice.models;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * A move request from the user
 * 
 * @author jhopper
 *
 */
public class GameMove {

	@Getter
	@Setter
	private String player;
	@Getter
	@Setter
	private Integer column;

}
