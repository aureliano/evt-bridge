package com.github.aureliano.damihilogs.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.aureliano.damihilogs.validation.MaxValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Constraint(validatedBy = MaxValidator.class)
public @interface Max {

	public abstract String message() default "Expected a maximum value of ? for field ? but got ?.";
	
	public abstract int value();
}