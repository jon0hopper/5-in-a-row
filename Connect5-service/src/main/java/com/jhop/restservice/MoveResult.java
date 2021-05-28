package com.jhop.restservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor()
@AllArgsConstructor()
public class MoveResult {
	@Getter
	@Setter
	@NonNull
	private Boolean allowed;
	
	@Setter
	@Getter
	private String errorReason;
	

}
