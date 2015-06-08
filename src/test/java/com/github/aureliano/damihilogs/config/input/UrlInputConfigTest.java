package com.github.aureliano.damihilogs.config.input;

import org.junit.Assert;
import org.junit.Test;

import com.github.aureliano.damihilogs.exception.IExceptionHandler;

public class UrlInputConfigTest {

	@Test
	public void testGetDefaults() {
		UrlInputConfig c = new UrlInputConfig();
		Assert.assertEquals(ConnectionSchema.HTTP, c.getConnectionSchema());
		Assert.assertEquals(-1, c.getPort());
		Assert.assertNotNull(c.getParameters());
		Assert.assertTrue(c.getParameters().isEmpty());
		Assert.assertEquals(new Long(UrlInputConfig.DEFAULT_READ_TIMEOUT), c.getReadTimeout());
		Assert.assertEquals(new Integer(0), c.getByteOffSet());
		Assert.assertFalse(c.isAppendIfOutputFileExist());
		Assert.assertNull(c.getFileStartPosition());
		Assert.assertFalse(c.isNoCheckCertificate());
	}
	
	@Test
	public void testConfiguration() {
		UrlInputConfig c = new UrlInputConfig()
			.withConnectionSchema(ConnectionSchema.HTTPS)
			.withHost("localhost")
			.withPort(8080)
			.withPath("logs")
			.addParameter("test", "Is it a test?")
			.withReadTimeout(new Long(5 * 1000))
			.withByteOffSet(199845)
			.withOutputFile("output_file.log")
			.withAppendIfOutputFileExist(true)
			.withFileStartPosition(45)
			.withUser("user_name")
			.withPassword("my-password")
			.withNoCheckCertificate(true);
		
		
		Assert.assertEquals(ConnectionSchema.HTTPS, c.getConnectionSchema());
		Assert.assertEquals("localhost", c.getHost());
		Assert.assertEquals(8080, c.getPort());
		Assert.assertEquals("logs", c.getPath());
		Assert.assertTrue(c.getParameters().size() == 1);
		Assert.assertEquals("Is it a test?", c.getParameters().get("test"));
		Assert.assertEquals(new Long(5000), c.getReadTimeout());
		Assert.assertEquals(new Integer(199845), c.getByteOffSet());
		Assert.assertEquals("output_file.log", c.getOutputFile().getPath());
		Assert.assertTrue(c.isAppendIfOutputFileExist());
		Assert.assertEquals(new Integer(45), c.getFileStartPosition());
		Assert.assertEquals("user_name", c.getUser());
		Assert.assertEquals("my-password", c.getPassword());
		Assert.assertTrue(c.isNoCheckCertificate());
	}
	
	@Test
	public void testClone() {
		UrlInputConfig c1 = (UrlInputConfig) new UrlInputConfig()
			.withConnectionSchema(ConnectionSchema.HTTPS)
			.withHost("localhost")
			.withPort(8080)
			.withPath("logs")
			.addParameter("test", "Is it a test?")
			.withReadTimeout(new Long(5 * 1000))
			.withByteOffSet(199845)
			.withOutputFile("output_file.log")
			.withAppendIfOutputFileExist(true)
			.withFileStartPosition(45)
			.withUser("user_name")
			.withPassword("my-password")
			.withNoCheckCertificate(true)
			.withConfigurationId("url.input.config")
			.putMetadata("test", "my test")
			.addExceptionHandler(new IExceptionHandler() {
				public void captureException(Runnable runnable, IConfigInput inputConfig, Throwable trowable) { }
			});
		
		UrlInputConfig c2 = c1.clone();
		
		Assert.assertEquals(c1.getConfigurationId(), c2.getConfigurationId());
		Assert.assertEquals(c1.getHost(), c2.getHost());
		Assert.assertEquals(c1.getPassword(), c2.getPassword());
		Assert.assertEquals(c1.getPath(), c2.getPath());
		Assert.assertEquals(c1.getReadTimeout(), c2.getReadTimeout());
		Assert.assertEquals(c1.getUser(), c2.getUser());
		Assert.assertEquals(c1.getByteOffSet(), c2.getByteOffSet());
		Assert.assertEquals(c1.getConnectionSchema(), c2.getConnectionSchema());
		Assert.assertEquals(c1.isAppendIfOutputFileExist(), c2.isAppendIfOutputFileExist());
		Assert.assertEquals(c1.getFileStartPosition(), c2.getFileStartPosition());
		Assert.assertEquals(c1.getOutputFile(), c2.getOutputFile());
		Assert.assertEquals(c1.getParameters().size(), c2.getParameters().size());
		Assert.assertEquals(c1.getPort(), c2.getPort());
		Assert.assertEquals(c1.isNoCheckCertificate(), c2.isNoCheckCertificate());
		Assert.assertEquals(c1.getMetadata("test"), c2.getMetadata("test"));
		Assert.assertEquals(c1.getExceptionHandlers().size(), c2.getExceptionHandlers().size());
	}
	
	@Test
	public void testInputType() {
		Assert.assertEquals(InputConfigTypes.URL.name(), new UrlInputConfig().id());
	}
}