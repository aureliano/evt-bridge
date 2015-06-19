package com.github.aureliano.damihilogs.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.github.aureliano.damihilogs.annotation.validation.Size;
import com.github.aureliano.damihilogs.config.IConfiguration;
import com.github.aureliano.damihilogs.helper.ReflectionHelper;

public class SizeValidator implements IValidator {

	public SizeValidator() {
		super();
	}
	
	@Override
	public ConstraintViolation validate(IConfiguration configuration, Method method, Annotation annotation) {
		String property = ReflectionHelper.getSimpleAccessMethodName(method);
		Object returnedValue = ReflectionHelper.callMethod(configuration, method.getName(), null, null);
		
		if (returnedValue == null) {
			returnedValue = "";
		}
		
		Size sizeAnnotation = (Size) annotation;
		String message = sizeAnnotation.message();
		int minSize = sizeAnnotation.min();
		int maxSize = sizeAnnotation.max();
		
		if ((minSize > returnedValue.toString().length()) || (maxSize < returnedValue.toString().length())) {
			int size = returnedValue.toString().length();
			return new ConstraintViolation()
				.withValidator(Size.class)
				.withMessage(message
					.replaceFirst("\\?", property)
					.replaceFirst("\\?", String.valueOf(minSize))
					.replaceFirst("\\?", String.valueOf(maxSize))
					.replaceFirst("\\?", String.valueOf(size)));
		}
		
		return null;
	}
}