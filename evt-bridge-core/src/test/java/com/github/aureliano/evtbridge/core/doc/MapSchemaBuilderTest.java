package com.github.aureliano.evtbridge.core.doc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.aureliano.evtbridge.core.SchemaTypes;

public class MapSchemaBuilderTest {

	@Test
	public void testBuildRoot() {
		Map<String, Object> map = new MapSchemaBuilder().build(SchemaTypes.ROOT);
		
		assertEquals("http://json-schema.org/draft-04/schema#", map.get("$schema"));
		assertEquals("Events collector configuration schema.", map.get("title"));
		assertEquals("object", map.get("type"));
		
		@SuppressWarnings("unchecked")
		Map<String, Object> properties = (Map<String, Object>) map.get("properties");
		assertEquals(8, properties.size());
		
		assertNotNull(properties.get("collectorId"));
		assertNotNull(properties.get("metadata"));
		assertNotNull(properties.get("eventsCollectorListeners"));
		assertNotNull(properties.get("scheduler"));
		assertNotNull(properties.get("multiThreadingEnabled"));
		assertNotNull(properties.get("persistExecutionLog"));
		assertNotNull(properties.get("outputConfigs"));
		assertNotNull(properties.get("inputConfigs"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testBuildScheduler() {
		Map<String, Object> map = new MapSchemaBuilder().build(SchemaTypes.SCHEDULER);
		
		assertEquals("http://json-schema.org/draft-04/schema#", map.get("$schema"));
		assertEquals("Scheduling execution configuration.", map.get("title"));
		assertEquals("object", map.get("type"));
		
		Map<String, Object> properties = (Map<String, Object>) map.get("properties");
		Map<String, Object> type = (Map<String, Object>) properties.get("type");
		List<Map<String, Object>> anyOf = (List<Map<String, Object>>) type.get("anyOf");
		
		assertEquals(3, anyOf.size());
	}
}