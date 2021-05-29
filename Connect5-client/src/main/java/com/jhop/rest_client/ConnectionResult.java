package com.jhop.rest_client;

import lombok.Getter;
import lombok.Setter;

/**
 * This handles the result of a request to the Server
 * 
 * 
 * @author jhopper
 *
 */
public class ConnectionResult {
		
	@Getter
	@Setter
	private Integer httpCode;

	@Getter
	@Setter
	private String httpResponse;
	
	public ConnectionResult(Integer code, String response) {
		this.httpCode = code;
		this.httpResponse = response;
	}

}
