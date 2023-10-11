package com.att.sales.nexxus.common;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface APIFieldProperty.
 */
@Target(value=ElementType.FIELD)
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface APIFieldProperty {

	/**
	 * Required.
	 *
	 * @return true, if successful
	 */
	public boolean required() default false;
	
}

