package com.github.aureliano.defero.reader;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.github.aureliano.defero.config.input.IConfigInput;
import com.github.aureliano.defero.config.input.InputFileConfig;
import com.github.aureliano.defero.event.AfterReadingEvent;
import com.github.aureliano.defero.event.BeforeReadingEvent;
import com.github.aureliano.defero.event.StepParseEvent;
import com.github.aureliano.defero.exception.DeferoException;
import com.github.aureliano.defero.listener.DataReadingListener;
import com.github.aureliano.defero.parser.IParser;

public class FileDataReader implements IDataReader {

	private InputFileConfig inputConfiguration;
	private IParser<?> parser;
	private LineIterator lineIterator;
	private long lineCounter;
	
	private List<DataReadingListener> listeners;
	
	public FileDataReader() {
		this.lineCounter = 0;
	}
	
	@Override
	public IConfigInput getInputConfiguration() {
		return inputConfiguration;
	}

	@Override
	public IDataReader withInputConfiguration(IConfigInput config) {
		this.inputConfiguration = (InputFileConfig) config;
		return this;
	}
	
	@Override
	public IParser<?> getParser() {
		return parser;
	}

	@Override
	public IDataReader withParser(IParser<?> parser) {
		this.parser = parser;
		return this;
	}
	
	@Override
	public List<DataReadingListener> getListeners() {
		return listeners;
	}
	
	@Override
	public IDataReader withListeners(List<DataReadingListener> listeners) {
		this.listeners = listeners;
		return this;
	}

	@Override
	public Object nextData() {
		this.initialize();
		
		if (!this.lineIterator.hasNext()) {
			LineIterator.closeQuietly(this.lineIterator);
			return null;
		}
		
		this.prepareReading();
		
		this.executeBeforeReadingMethodListeners();
		Object data = this.parseData();
		this.executeAfterReadingMethodListeners(data);
		
		return data;
	}
	
	@Override
	public long lastLine() {
		return this.lineCounter;
	}
	
	private void executeBeforeReadingMethodListeners() {
		for (DataReadingListener listener : this.listeners) {
			listener.beforeDataReading(new BeforeReadingEvent(this.inputConfiguration, this.lineCounter, MAX_PARSE_ATTEMPTS));
		}
	}
	
	private void executeAfterReadingMethodListeners(Object data) {
		for (DataReadingListener listener : this.listeners) {
			listener.afterDataReading(new AfterReadingEvent(this.lineCounter, data));
		}
	}
	
	private void prepareReading() {		
		this.lineCounter++;
		
		while (this.inputConfiguration.getStartPosition() > this.lineCounter) {
			this.lineIterator.nextLine();
			this.lineCounter++;
		}
	}
	
	private String parseData() {
		int counter = 0;
		String line = this.lineIterator.nextLine();
		StringBuilder buffer = new StringBuilder(line);
		
		for (DataReadingListener listener : this.listeners) {
			listener.stepLineParse(new StepParseEvent(counter + 1, line, buffer.toString()));
		}
		
		while (!this.parser.accept(line)) {
			if (MAX_PARSE_ATTEMPTS >= ++counter) {
				throw new DeferoException("Max parse attempts overflow.");
			}
			
			buffer.append("\n").append(this.lineIterator.nextLine());			
			for (DataReadingListener listener : this.listeners) {
				listener.stepLineParse(new StepParseEvent((counter + 1), line, buffer.toString()));
			}
		}
		
		return buffer.toString();
	}
	
	private void initialize() {
		if (this.lineIterator != null) {
			return;
		}
		
		try {
			this.lineIterator = FileUtils.lineIterator(
					this.inputConfiguration.getFile(), this.inputConfiguration.getEncoding());
		} catch (IOException ex) {
			throw new DeferoException(ex);
		}
	}
}