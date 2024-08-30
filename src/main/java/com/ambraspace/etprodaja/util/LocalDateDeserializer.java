package com.ambraspace.etprodaja.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate>
{

	@Override
	public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException
	{
		ZonedDateTime zdt = ZonedDateTime.parse(p.getText());
		return LocalDate.ofInstant(zdt.toInstant(), ZoneId.systemDefault());
	}

}
