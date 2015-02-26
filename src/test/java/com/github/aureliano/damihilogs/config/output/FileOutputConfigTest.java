package com.github.aureliano.damihilogs.config.output;

import org.junit.Assert;
import org.junit.Test;

public class FileOutputConfigTest {

	@Test
	public void testGetDefaults() {
		FileOutputConfig c = new FileOutputConfig();
		Assert.assertNull(c.getFile());
		Assert.assertEquals("UTF-8", c.getEncoding());
		Assert.assertFalse(c.isAppend());
	}
	
	@Test
	public void testConfiguration() {
		FileOutputConfig c = new FileOutputConfig()
			.withAppend(true)
			.withEncoding("ISO-8859-1")
			.withFile("/there/is/not/file");
		
		Assert.assertEquals("/there/is/not/file", c.getFile().getPath());
		Assert.assertEquals("ISO-8859-1", c.getEncoding());
		Assert.assertTrue(c.isAppend());
	}
	
	@Test
	public void testClone() {
		FileOutputConfig c1 = new FileOutputConfig()
			.withAppend(true)
			.withEncoding("ISO-8859-1")
			.withFile("/there/is/not/file");
		
		FileOutputConfig c2 = c1.clone();
		Assert.assertEquals(c1.getFile(), c2.getFile());
		Assert.assertEquals(c1.getEncoding(), c2.getEncoding());
		Assert.assertEquals(c1.isAppend(), c2.isAppend());
	}
}