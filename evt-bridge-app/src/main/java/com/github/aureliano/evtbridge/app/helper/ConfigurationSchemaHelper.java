package com.github.aureliano.evtbridge.app.helper;

import org.reflections.Reflections;

import com.github.aureliano.evtbridge.common.exception.EventBridgeException;
import com.github.aureliano.evtbridge.common.helper.StringHelper;
import com.github.aureliano.evtbridge.core.SchemaTypes;
import com.github.aureliano.evtbridge.core.config.IConfiguration;
import com.github.aureliano.evtbridge.core.config.InputConfigTypes;
import com.github.aureliano.evtbridge.core.config.OutputConfigTypes;
import com.github.aureliano.evtbridge.core.doc.DocumentationSourceTypes;
import com.github.aureliano.evtbridge.core.doc.ISchemaBuilder;
import com.github.aureliano.evtbridge.core.doc.JsonSchemaBuilder;
import com.github.aureliano.evtbridge.core.doc.YamlSchemaBuilder;
import com.github.aureliano.evtbridge.core.schedule.SchedulerTypes;
import com.github.aureliano.evtbridge.input.external_command.ExternalCommandInputConfig;
import com.github.aureliano.evtbridge.input.file.FileInputConfig;
import com.github.aureliano.evtbridge.input.file_tailer.FileTailerInputConfig;
import com.github.aureliano.evtbridge.input.jdbc.JdbcInputConfig;
import com.github.aureliano.evtbridge.input.standard.StandardInputConfig;
import com.github.aureliano.evtbridge.input.url.UrlInputConfig;
import com.github.aureliano.evtbridge.output.elasticsearch.ElasticSearchOutputConfig;
import com.github.aureliano.evtbridge.output.file.FileOutputConfig;
import com.github.aureliano.evtbridge.output.jdbc.JdbcOutputConfig;
import com.github.aureliano.evtbridge.output.standard.StandardOutputConfig;

public final class ConfigurationSchemaHelper {
	
	private ConfigurationSchemaHelper() {}
	
	public static String fetchSchema(String type, String name, String format) {
		final SchemaTypes schemaType = SchemaTypes.valueOf(type.toUpperCase());
		final DocumentationSourceTypes sourceType = DocumentationSourceTypes.valueOf(format.toUpperCase());
		
		if (StringHelper.isEmpty(name)) {
			return ConfigurationSchemaHelper.getSchemaBuilder(sourceType).build(schemaType);
		} else {
			initializeResouces();
			switch (schemaType) {
			case SCHEDULER:
				return ConfigurationSchemaHelper.getSchemaBuilder(sourceType)
						.build(SchedulerTypes.valueOf(name.toUpperCase()));
			case INPUT:
				return buildInputSchema(sourceType, name);
			case OUTPUT:
				return buildOutputSchema(sourceType, name);
			default:
				throw new EventBridgeException("Unsupported schema type: [" + type + "]");
			}
		}
	}
	
	private static String buildInputSchema(DocumentationSourceTypes sourceType, String name) {
		InputConfigTypes inputType = InputConfigTypes.valueOf(name.toUpperCase());
		return ConfigurationSchemaHelper.getSchemaBuilder(sourceType).build(inputType);
	}
	
	private static String buildOutputSchema(DocumentationSourceTypes sourceType, String name) {
		OutputConfigTypes outputType = OutputConfigTypes.valueOf(name.toUpperCase());
		return ConfigurationSchemaHelper.getSchemaBuilder(sourceType).build(outputType);
	}
	
	private static void initializeResouces() {
		Reflections reflections = new Reflections("com.github.aureliano.evtbridge");
		Class<?>[] classes = reflections.getSubTypesOf(IConfiguration.class).toArray(new Class<?>[0]);
		
		try {
			for (Class<?> clazz : classes) {
				Class.forName(clazz.getName());
			}
		} catch (ClassNotFoundException ex) {
			throw new EventBridgeException(ex);
		}
	}
	
	private static ISchemaBuilder<String> getSchemaBuilder(DocumentationSourceTypes sourceType) {
		if (DocumentationSourceTypes.JSON.equals(sourceType)) {
			return new JsonSchemaBuilder();
		} else if (DocumentationSourceTypes.YAML.equals(sourceType)) {
			return new YamlSchemaBuilder();
		} else {
			throw new EventBridgeException("Unsupported source type '" + sourceType + "'.");
		}
	}
}