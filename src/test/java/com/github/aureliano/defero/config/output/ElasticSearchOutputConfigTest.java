package com.github.aureliano.defero.config.output;

import org.junit.Assert;
import org.junit.Test;

public class ElasticSearchOutputConfigTest {

	@Test
	public void testGetDefaults() {
		ElasticSearchOutputConfig c = new ElasticSearchOutputConfig();
		Assert.assertEquals("127.0.0.1", c.getHost());
		Assert.assertEquals(9200, c.getPort());
		Assert.assertEquals(9300, c.getTransportClientPort());
		Assert.assertFalse(c.isAutomaticClientClose());
		Assert.assertFalse(c.isPrintElasticSearchLog());
	}
	
	@Test
	public void testConfiguration() {
		ElasticSearchOutputConfig c = new ElasticSearchOutputConfig()
			.withHost("localhost")
			.withPort(8080)
			.withTransportClientPort(9301)
			.withAutomaticClientClose(true)
			.withPrintElasticSearchLog(true);
		
		Assert.assertEquals("localhost", c.getHost());
		Assert.assertEquals(8080, c.getPort());
		Assert.assertEquals(9301, c.getTransportClientPort());
		Assert.assertTrue(c.isAutomaticClientClose());
		Assert.assertTrue(c.isPrintElasticSearchLog());
	}
}