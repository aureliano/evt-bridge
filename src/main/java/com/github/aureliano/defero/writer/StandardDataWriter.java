package com.github.aureliano.defero.writer;

import java.util.List;

import com.github.aureliano.defero.config.output.IConfigOutput;
import com.github.aureliano.defero.config.output.StandardOutputConfig;
import com.github.aureliano.defero.event.AfterWritingEvent;
import com.github.aureliano.defero.event.BeforeWritingEvent;
import com.github.aureliano.defero.listener.DataWritingListener;

public class StandardDataWriter implements IDataWriter {

	private StandardOutputConfig outputConfiguration;
	private List<DataWritingListener> listeners;
	
	public StandardDataWriter() {
		super();
	}
	
	@Override
	public IConfigOutput getOutputConfiguration() {
		return outputConfiguration;
	}

	@Override
	public IDataWriter withOutputConfiguration(IConfigOutput config) {
		this.outputConfiguration = (StandardOutputConfig) config;
		return this;
	}

	@Override
	public void write(Object data, List<DataWritingListener> listeners) {
		this.listeners = listeners;
		this.executeBeforeWritingMethodListeners(data);
		System.out.println(data);
		this.executeAfterWritingMethodListeners(data);
	}

	private void executeBeforeWritingMethodListeners(Object data) {
		for (DataWritingListener listener : this.listeners) {
			listener.beforeDataWriting(new BeforeWritingEvent(this.outputConfiguration, data));
		}
	}

	private void executeAfterWritingMethodListeners(Object data) {
		for (DataWritingListener listener : this.listeners) {
			listener.afterDataWriting(new AfterWritingEvent(this.outputConfiguration, data));
		}
	}
}