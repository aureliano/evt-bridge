package com.github.aureliano.damihilogs.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aureliano.damihilogs.exception.DaMihiLogsException;

public class JsonFormatter implements IOutputFormatter {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public JsonFormatter() {
		super();
	}

	@Override
	public String format(Object data) {
		if (data == null) {
			return "";
		}
		
		try {
			return objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException ex) {
			throw new DaMihiLogsException(ex);
		}
	}
}