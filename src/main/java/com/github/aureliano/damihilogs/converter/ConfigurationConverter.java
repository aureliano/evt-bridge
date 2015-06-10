package com.github.aureliano.damihilogs.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.github.aureliano.damihilogs.clean.ICleaner;
import com.github.aureliano.damihilogs.config.EventCollectorConfiguration;
import com.github.aureliano.damihilogs.config.input.IConfigInput;
import com.github.aureliano.damihilogs.config.output.IConfigOutput;
import com.github.aureliano.damihilogs.helper.DataHelper;
import com.github.aureliano.damihilogs.helper.ReflectionHelper;
import com.github.aureliano.damihilogs.helper.StringHelper;
import com.github.aureliano.damihilogs.listener.EventsCollectorListener;
import com.github.aureliano.damihilogs.report.ILoggerReporter;
import com.github.aureliano.damihilogs.schedule.EventCollectionSchedule;

public class ConfigurationConverter implements IConfigurationConverter<EventCollectorConfiguration> {

	public ConfigurationConverter() {
		super();
	}
	
	@Override
	public EventCollectorConfiguration convert(Map<String, Object> data) {
		EventCollectorConfiguration configuration = new EventCollectorConfiguration()
			.withCollectorId(StringHelper.parse(data.get("id")));
		
		String value = StringHelper.parse(data.get("persistExecutionLog"));
		if (!StringHelper.isEmpty(value)) {
			configuration.withPersistExecutionLog(Boolean.parseBoolean(value));
		}
		
		value = StringHelper.parse(data.get("multiThreadingEnabled"));
		if (!StringHelper.isEmpty(value)) {
			configuration.withMultiThreadingEnabled(Boolean.parseBoolean(value));
		}
		
		if (data.get("metadata") != null) {
			Properties properties = DataHelper.mapToProperties((Map<String, Object>) data.get("metadata"));
			configuration.withMetadata(properties);
		}
		
		this.propagateConversion(configuration, data);
		return configuration;
	}
	
	private void propagateConversion(EventCollectorConfiguration configuration, Map<String, Object> data) {
		for (ILoggerReporter reporter : this.convertReporter(data)) {
			configuration.addReporter(reporter);
		}
		
		for (ICleaner cleaner : this.convertCleaner(data)) {
			configuration.addCleaner(cleaner);
		}
		
		for (IConfigInput input : this.convertInputs(data)) {
			configuration.addInputConfig(input);
		}
		
		for (IConfigOutput output : this.convertOutputs(data)) {
			configuration.addOutputConfig(output);
		}
		
		configuration
			.withScheduler(this.convertScheduler(data))
			.withEventsCollectorListeners(this.convertEventsCollectorListeners(data));
	}
	
	private EventCollectionSchedule convertScheduler(Map<String, Object> data) {
		List<?> result = ConversionApplyer.apply(ConverterType.SCHEDULER, data);
		if ((result != null) && (!result.isEmpty())) {
			return (EventCollectionSchedule) result.get(0);
		} else {
			return null;
		}
	}
	
	private List<ILoggerReporter> convertReporter(Map<String, Object> data) {
		return (List<ILoggerReporter>) ConversionApplyer.apply(ConverterType.REPORTER, data);
	}
	
	private List<ICleaner> convertCleaner(Map<String, Object> data) {
		return (List<ICleaner>) ConversionApplyer.apply(ConverterType.CLEANER, data);
	}
	
	private List<EventsCollectorListener> convertEventsCollectorListeners(Map<String, Object> data) {
		List<String> list = (List<String>) data.get("eventsCollectorListeners");
		if (list == null) {
			return Collections.emptyList();
		}
		
		List<EventsCollectorListener> listeners = new ArrayList<EventsCollectorListener>(list.size());
		for (String className : list) {
			listeners.add((EventsCollectorListener) ReflectionHelper.newInstance(className));
		}
		
		return listeners;
	}
	
	private List<IConfigInput> convertInputs(Map<String, Object> data) {
		return (List<IConfigInput>) ConversionApplyer.apply(ConverterType.INPUT, data);
	}
	
	private List<IConfigOutput> convertOutputs(Map<String, Object> data) {
		return (List<IConfigOutput>) ConversionApplyer.apply(ConverterType.OUTPUT, data);
	}
}