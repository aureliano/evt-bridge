package com.github.aureliano.damihilogs.executor.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.github.aureliano.damihilogs.config.input.ExternalCommandInputConfig;
import com.github.aureliano.damihilogs.exception.DaMihiLogsException;

public class ExternalCommandDataReader extends AbstractDataReader {

	private Process process;
	private BufferedReader bufferedReader;
	private ExternalCommandInputConfig externalCommandInput;
	
	private static final Logger logger = Logger.getLogger(ExternalCommandInputConfig.class);
	
	public ExternalCommandDataReader() {
		super();
	}
	
	@Override
	public void initializeResources() {
		this.initialize();
	}
	
	@Override
	public String readLine() {
		try {
			return this.bufferedReader.readLine();
		} catch (IOException ex) {
			throw new DaMihiLogsException(ex);
		}
	}

	@Override
	public Map<String, Object> executionLog() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("input.config." + super.inputConfiguration.getConfigurationId() + ".last.line", super.lineCounter);
		
		return map;
	}

	@Override
	public void loadLastExecutionLog(Properties properties) { }

	@Override
	public void finalizeResources() {
		logger.debug(" >>> Flushing and closing stream reader.");
		if (this.bufferedReader == null) {
			return;
		}

		try {
			if (this.process.waitFor() != 0) {
				logger.warn("External command exited with no normal exit status.");
				logger.warn(this.readError());
			}
		} catch (InterruptedException ex) {
			throw new DaMihiLogsException(ex);
		}
		
		this.process.destroy();
		
		try {
			this.bufferedReader.close();
			this.bufferedReader = null;
		} catch (IOException ex) {
			throw new DaMihiLogsException(ex);
		}
	}
	
	private void initialize() {
		this.externalCommandInput = (ExternalCommandInputConfig) super.inputConfiguration;
		
		logger.info("Execute command: " + this.externalCommandInput.getCommand());
		logger.info("With parameters: " + this.externalCommandInput.getParameters());
		
		String command = this.buildCommand();
		
		try {
			this.process = Runtime.getRuntime().exec(command);
		} catch (IOException ex) {
			throw new DaMihiLogsException(ex);
		}
		
		InputStreamReader inputStreamReader = new InputStreamReader(this.process.getInputStream());
		this.bufferedReader = new BufferedReader(inputStreamReader);
	}
	
	private String buildCommand() {
		StringBuilder command = new StringBuilder(this.externalCommandInput.getCommand());
		
		for (String parameter : this.externalCommandInput.getParameters()) {
			command.append(" ").append(parameter);
		}
		
		return command.toString();
	}
	
	private String readError() {
		InputStreamReader inputStreamReader = new InputStreamReader(this.process.getErrorStream());
		BufferedReader reader = new BufferedReader(inputStreamReader);
		
		StringBuilder error = new StringBuilder();
		String line = null;
		
		try {
			while ((line = reader.readLine()) != null) {
				error.append(line).append("\n");
			}
			reader.close();
		} catch (IOException ex) {
			throw new DaMihiLogsException(ex);
		}
		
		error.deleteCharAt(error.length() - 1);
		return error.toString();
	}
}