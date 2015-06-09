package com.github.aureliano.damihilogs.converter.clean;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import com.github.aureliano.damihilogs.clean.CleanerTypes;
import com.github.aureliano.damihilogs.exception.DaMihiLogsException;

public class CleanerFactoryTest {
	
	@Test
	public void testCreateConverterNonexistent() {
		String type = "test";
		String errorMessage = "Cleaner type '" + type + "' not supported. Expected one of: " + Arrays.toString(CleanerTypes.values());
		
		try {
			CleanerFactory.createConverter(type);
			Assert.fail("An exception was expected.");
		} catch (DaMihiLogsException ex) {
			Assert.assertEquals(errorMessage, ex.getMessage());
		}
	}

	@Test
	public void testCreateConverter() {
		Assert.assertTrue(CleanerFactory.createConverter(CleanerTypes.FILE.name()) instanceof FileCleanerConverter);
		Assert.assertTrue(CleanerFactory.createConverter(CleanerTypes.FILE.name().toLowerCase()) instanceof FileCleanerConverter);
		Assert.assertTrue(CleanerFactory.createConverter(CleanerTypes.LOG.name()) instanceof LogCleanerConverter);
		Assert.assertTrue(CleanerFactory.createConverter(CleanerTypes.LOG.name().toLowerCase()) instanceof LogCleanerConverter);
	}
}