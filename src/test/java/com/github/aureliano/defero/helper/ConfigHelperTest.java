package com.github.aureliano.defero.helper;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.github.aureliano.defero.config.input.ConnectionSchema;
import com.github.aureliano.defero.config.input.InputFileConfig;
import com.github.aureliano.defero.config.input.UrlInputConfig;
import com.github.aureliano.defero.config.output.FileOutputConfig;
import com.github.aureliano.defero.exception.DeferoException;

public class ConfigHelperTest {
	
	@Test
	public void testBuildUrl() {
		UrlInputConfig c = new UrlInputConfig()
			.withConnectionSchema(ConnectionSchema.HTTP)
			.withHost("www.google.com");
		Assert.assertEquals("http://www.google.com", ConfigHelper.buildUrl(c));
		
		c.withConnectionSchema(ConnectionSchema.HTTPS)
			.withHost("127.0.0.1")
			.withPort(8080)
			.withPath("logs")
			.addParameter("year", "2015")
			.addParameter("another", "some text");
		String url = "https://127.0.0.1:8080/logs?year=2015&another=some+text";
		Assert.assertEquals(url, ConfigHelper.buildUrl(c));
		
		c.withHost("127.0.0.1/").withPath("/logs");
		Assert.assertEquals(url, ConfigHelper.buildUrl(c));
	}

	@Test
	public void testInputConfigValidation() {
		try {
			ConfigHelper.inputConfigValidation(null);
		} catch (DeferoException ex) {
			Assert.assertEquals("Input configuration must be provided.", ex.getMessage());
		}
	}
	
	@Test
	public void testOutputConfigValidation() {
		try {
			ConfigHelper.outputConfigValidation(null);
		} catch (DeferoException ex) {
			Assert.assertEquals("Output configuration must be provided.", ex.getMessage());
		}
	}
	
	@Test
	public void testInputFileConfigValidationFile() {
		try {
			ConfigHelper.inputFileConfigValidation(new InputFileConfig());
		} catch (DeferoException ex) {
			Assert.assertEquals("Input file not provided.", ex.getMessage());
		}
		
		try {
			ConfigHelper.inputFileConfigValidation(new InputFileConfig().withFile(new File("/non/existent/file")));
		} catch (DeferoException ex) {
			Assert.assertEquals("Input file '/non/existent/file' does not exist.", ex.getMessage());
		}
		
		try {
			ConfigHelper.inputFileConfigValidation(new InputFileConfig().withFile(new File("src/test/resources")));
		} catch (DeferoException ex) {
			Assert.assertEquals("Input resource 'src/test/resources' is not a file.", ex.getMessage());
		}
		
		ConfigHelper.inputFileConfigValidation(new InputFileConfig().withFile(new File("src/test/resources/empty-file.log")));
		ConfigHelper.inputFileConfigValidation(new InputFileConfig().withFile("src/test/resources/empty-file.log"));
	}
	
	@Test
	public void testInputFileConfigValidationStartPosition() {
		try {
			ConfigHelper.inputFileConfigValidation(new InputFileConfig().withFile("src/test/resources/empty-file.log").withStartPosition(-1));
		} catch (DeferoException ex) {
			Assert.assertEquals("Start position must be greater or equal to zero (>= 0).", ex.getMessage());
		}
		
		ConfigHelper.inputFileConfigValidation(new InputFileConfig().withFile("src/test/resources/empty-file.log").withStartPosition(0));
	}
	
	@Test
	public void testOutputFileConfigValidationFile() {
		try {
			ConfigHelper.outputConfigValidation(new FileOutputConfig());
		} catch (DeferoException ex) {
			Assert.assertEquals("Output file not provided.", ex.getMessage());
		}
		
		try {
			ConfigHelper.outputConfigValidation(new FileOutputConfig().withFile(new File("/non/existent/file")));
		} catch (DeferoException ex) {
			Assert.assertEquals("Output file '/non/existent/file' does not exist.", ex.getMessage());
		}
		
		try {
			ConfigHelper.outputConfigValidation(new FileOutputConfig().withFile(new File("src/test/resources")));
		} catch (DeferoException ex) {
			Assert.assertEquals("Output resource 'src/test/resources' is not a file.", ex.getMessage());
		}
		
		ConfigHelper.outputConfigValidation(new FileOutputConfig().withFile(new File("src/test/resources/empty-file.log")));
		ConfigHelper.outputConfigValidation(new FileOutputConfig().withFile("src/test/resources/empty-file.log"));
	}
	
	@Test
	public void testUrlInputConfigValidation() {
		try {
			ConfigHelper.urlInputConfigValidation(new UrlInputConfig().withConnectionSchema(null));
		} catch (DeferoException ex) {
			Assert.assertEquals("Connection schema not provided.", ex.getMessage());
		}
		
		try {
			ConfigHelper.urlInputConfigValidation(new UrlInputConfig().withConnectionSchema(ConnectionSchema.HTTP));
		} catch (DeferoException ex) {
			Assert.assertEquals("Host not provided.", ex.getMessage());
		}
		
		try {
			ConfigHelper.urlInputConfigValidation(new UrlInputConfig()
				.withConnectionSchema(ConnectionSchema.HTTP).withHost("localhost"));
		} catch (DeferoException ex) {
			Assert.assertEquals("Output file not provided.", ex.getMessage());
		}
		
		try {
			ConfigHelper.urlInputConfigValidation(new UrlInputConfig()
				.withConnectionSchema(ConnectionSchema.HTTP).withHost("localhost")
				.withOutputFile("src/test/resources"));
		} catch (DeferoException ex) {
			Assert.assertEquals("Output file 'src/test/resources' is a directory.", ex.getMessage());
		}
		
		ConfigHelper.urlInputConfigValidation(new UrlInputConfig()
			.withConnectionSchema(ConnectionSchema.HTTP).withHost("localhost")
			.withOutputFile("src/test/resources/empty-file.log"));
	}
}