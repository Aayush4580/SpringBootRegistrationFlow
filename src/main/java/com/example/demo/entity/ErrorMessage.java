package com.example.demo.entity;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorMessage {

	private HttpStatus status;
	private String message;
}
