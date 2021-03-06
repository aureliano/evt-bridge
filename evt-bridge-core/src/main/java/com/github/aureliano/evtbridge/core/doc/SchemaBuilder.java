package com.github.aureliano.evtbridge.core.doc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.aureliano.evtbridge.annotation.doc.SchemaConfiguration;
import com.github.aureliano.evtbridge.annotation.doc.SchemaProperty;
import com.github.aureliano.evtbridge.common.helper.StringHelper;
import com.github.aureliano.evtbridge.core.config.InputConfigTypes;
import com.github.aureliano.evtbridge.core.config.OutputConfigTypes;
import com.github.aureliano.evtbridge.core.schedule.SchedulerTypes;

public abstract class SchemaBuilder<T> implements ISchemaBuilder<T> {

	protected Map<String, Object> schema;
	
	public SchemaBuilder() {
		this.schema = new LinkedHashMap<>();
	}
	
	protected void configureSchemaHeader(Class<?> configuration) {
		SchemaConfiguration schemaConfiguration = configuration.getAnnotation(SchemaConfiguration.class);
		
		if (schemaConfiguration == null) {
			return;
		}
		
		this.schema.put("$schema", schemaConfiguration.schema());
		this.schema.put("title", schemaConfiguration.title());
		this.schema.put("type", schemaConfiguration.type());
	}
	
	protected void configureSchemaProperties(Class<?> configuration) {
		Map<String, Object> properties = this.buildProperties(configuration);
		this.schema.put("properties", properties);
	}
	
	private Map<String, Object> buildProperties(Class<?> configuration) {
		Map<String, Object> properties = new HashMap<>();
		
		for (Method method : configuration.getMethods()) {
			SchemaProperty schemaProperty = method.getAnnotation(SchemaProperty.class);
			if (schemaProperty == null) {
				continue;
			}
			
			Map<String, Object> property = this.buildProperty(schemaProperty);
			String key = property.keySet().iterator().next();
			properties.put(key, property.get(key));
		}
		
		return properties;
	}
	
	private Map<String, Object> buildProperty(SchemaProperty schemaProperty) {
		Map<String, Object> properties = new HashMap<>();
		Map<String, Object> property = new HashMap<>();
		
		property.put("type", this.configureTypes(schemaProperty.types()));
		property.put("description", schemaProperty.description());
		property.put("required", schemaProperty.required());
		if (!StringHelper.isEmpty(schemaProperty.defaultValue())) {
			property.put("default", schemaProperty.defaultValue());
		}
		properties.put(schemaProperty.property(), property);
		
		configureReference(schemaProperty, property);
		
		return properties;
	}

	private void configureReference(SchemaProperty schemaProperty, Map<String, Object> property) {
		if ("array".equals(schemaProperty.types()[0])) {
			property.put("items", this.mapItems(schemaProperty.reference()));
		} else {
			Class<?> clazz = schemaProperty.reference();
			if (clazz == null) {
				return;
			}
			
			Annotation annotation = clazz.getAnnotation(SchemaConfiguration.class);
			if (annotation != null) {
				Map<String, Object> properties = this.buildProperties(clazz);
				property.put("properties", properties);
			} else {
				String ref = this.getReferenceLabel(schemaProperty.reference());
				if (!StringHelper.isEmpty(ref)) {
					property.put("$ref", ref);
				}
			}
		}
	}
	
	private Object configureTypes(String[] types) {
		if (types.length == 1) {
			return types[0];
		} else {
			Map<String, Object> mtypes = new HashMap<>();
			mtypes.put("anyOf", types);
			
			return mtypes;
		}
	}

	private Map<String, Object> mapItems(Class<?> reference) {
		Map<String, Object> items = new HashMap<>();
		
		String ref = this.getReferenceLabel(reference);
		if (!StringHelper.isEmpty(ref)) {
			Map<String, Object> type = new HashMap<>();
			
			type.put("$ref", ref);
			items.put("type", type);
		} else {
			items.put("type", "string");
		}
		
		
		return items;
	}
	
	private String getReferenceLabel(Class<?> reference) {
		if (InputConfigTypes.class.equals(reference)) {
			return "input";
		} else if (OutputConfigTypes.class.equals(reference)) {
			return "output";
		} else if (SchedulerTypes.class.equals(reference)) {
			return "scheduler";
		} else {
			return null;
		}
	}
}