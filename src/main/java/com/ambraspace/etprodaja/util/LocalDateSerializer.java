package com.ambraspace.etprodaja.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalDateSerializer extends JsonSerializer<LocalDate>
{

	@Override
	public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException
	{

		ZonedDateTime zdt = ZonedDateTime.of(value, LocalTime.MIDNIGHT, ZoneId.systemDefault());
		gen.writeString(zdt.format(DateTimeFormatter.ISO_INSTANT));

	}



}
