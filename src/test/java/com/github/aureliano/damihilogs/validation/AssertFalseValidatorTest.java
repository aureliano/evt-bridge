package com.github.aureliano.damihilogs.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.github.aureliano.damihilogs.CustomInputConfig;
import com.github.aureliano.damihilogs.annotation.validation.AssertFalse;
import com.github.aureliano.damihilogs.config.IConfiguration;

public class AssertFalseValidatorTest {

	private AssertFalseValidator validator;
	
	public AssertFalseValidatorTest() {
		this.validator = new AssertFalseValidator();
	}
	
	@Test
	public void testValidateWithNullError() throws SecurityException, NoSuchMethodException {
		IConfiguration configuration = new CustomInputConfig().withNotOk(null);
		Method method = configuration.getClass().getMethod("isNotOk", new Class[] {});
		Annotation annotation = method.getAnnotation(AssertFalse.class);
		 
		ConstraintViolation constraint = this.validator.validate(configuration, method, annotation);
		Assert.assertNotNull(constraint);
		
		Assert.assertEquals("Expected field notOk to be false but got null.", constraint.getMessage());
		Assert.assertEquals(AssertFalse.class, constraint.getValidator());
	}
	
	@Test
	public void testValidateWithFalseError() throws SecurityException, NoSuchMethodException {
		IConfiguration configuration = new CustomInputConfig().withNotOk(true);
		Method method = configuration.getClass().getMethod("isNotOk", new Class[] {});
		Annotation annotation = method.getAnnotation(AssertFalse.class);
		 
		ConstraintViolation constraint = this.validator.validate(configuration, method, annotation);
		Assert.assertNotNull(constraint);
		
		Assert.assertEquals("Expected field notOk to be false but got true.", constraint.getMessage());
		Assert.assertEquals(AssertFalse.class, constraint.getValidator());
	}
	
	@Test
	public void testValidate() throws SecurityException, NoSuchMethodException {
		IConfiguration configuration = new CustomInputConfig().withNotOk(false);
		Method method = configuration.getClass().getMethod("isNotOk", new Class[] {});
		Annotation annotation = method.getAnnotation(AssertFalse.class);
		 
		ConstraintViolation constraint = this.validator.validate(configuration, method, annotation);
		Assert.assertNull(constraint);
	}
}