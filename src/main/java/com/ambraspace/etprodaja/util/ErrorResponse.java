package com.ambraspace.etprodaja.util;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorResponse
{

	private ZonedDateTime timestamp = ZonedDateTime.now();

	private int status;

	private String error;

	private String message;

	private String path;

}
