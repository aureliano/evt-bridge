package com.github.aureliano.defero.config;

import java.util.ArrayList;
import java.util.List;

import com.github.aureliano.defero.config.input.IConfigInput;
import com.github.aureliano.defero.config.input.StandardInputConfig;
import com.github.aureliano.defero.config.output.IConfigOutput;
import com.github.aureliano.defero.config.output.StandardOutputConfig;
import com.github.aureliano.defero.filter.IEventFielter;
import com.github.aureliano.defero.formatter.IOutputFormatter;
import com.github.aureliano.defero.listener.DataReadingListener;
import com.github.aureliano.defero.listener.DataWritingListener;
import com.github.aureliano.defero.matcher.IMatcher;
import com.github.aureliano.defero.matcher.SingleLineMatcher;
import com.github.aureliano.defero.parser.IParser;
import com.github.aureliano.defero.parser.PlainTextParser;

public class EventCollectorConfiguration {

	private IConfigInput inputConfig;
	private IConfigOutput outputConfig;
	private IMatcher matcher;
	private IParser<?> parser;
	private IEventFielter filter;
	private IOutputFormatter outputFormatter;
	private List<DataReadingListener> dataReadingListeners;
	private List<DataWritingListener> dataWritingListeners;
	
	public EventCollectorConfiguration() {
		this.inputConfig = new StandardInputConfig();
		this.outputConfig = new StandardOutputConfig();
		this.matcher = new SingleLineMatcher();
		this.parser = new PlainTextParser();
		
		this.dataReadingListeners = new ArrayList<DataReadingListener>();
		this.dataWritingListeners = new ArrayList<DataWritingListener>();
	}

	public IConfigInput getInputConfig() {
		return inputConfig;
	}

	public EventCollectorConfiguration withInputConfig(IConfigInput inputConfig) {
		this.inputConfig = inputConfig;
		return this;
	}
	
	public IConfigOutput getOutputConfig() {
		return outputConfig;
	}
	
	public EventCollectorConfiguration withOutputConfig(IConfigOutput outputConfig) {
		this.outputConfig = outputConfig;
		return this;
	}
	
	public IMatcher getMatcher() {
		return matcher;
	}
	
	public EventCollectorConfiguration withMatcher(IMatcher matcher) {
		this.matcher = matcher;
		return this;
	}

	public IParser<?> getParser() {
		return parser;
	}

	public EventCollectorConfiguration withParser(IParser<?> parser) {
		this.parser = parser;
		return this;
	}
	
	public IEventFielter getFilter() {
		return filter;
	}
	
	public EventCollectorConfiguration withFilter(IEventFielter filter) {
		this.filter = filter;
		return this;
	}
	
	public IOutputFormatter getOutputFormatter() {
		return outputFormatter;
	}
	
	public EventCollectorConfiguration withOutputFormatter(IOutputFormatter outputFormatter) {
		this.outputFormatter = outputFormatter;
		return this;
	}

	public List<DataReadingListener> getDataReadingListeners() {
		return dataReadingListeners;
	}

	public EventCollectorConfiguration withDataReadingListeners(List<DataReadingListener> dataReadingListeners) {
		this.dataReadingListeners = dataReadingListeners;
		return this;
	}
	
	public EventCollectorConfiguration addDataReadingListeners(DataReadingListener listener) {
		this.dataReadingListeners.add(listener);
		return this;
	}
	
	public List<DataWritingListener> getDataWritingListeners() {
		return dataWritingListeners;
	}
	
	public EventCollectorConfiguration withDataWritingListeners(List<DataWritingListener> dataWritingListeners) {
		this.dataWritingListeners = dataWritingListeners;
		return this;
	}
	
	public EventCollectorConfiguration addDataWritingListeners(DataWritingListener listener) {
		this.dataWritingListeners.add(listener);
		return this;
	}
}