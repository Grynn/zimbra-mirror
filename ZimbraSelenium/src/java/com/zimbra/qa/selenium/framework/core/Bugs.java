package com.zimbra.qa.selenium.framework.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * An annotation to correlate test methods with Bug IDs
 * @author Matt Rhoades
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bugs {
	
	/**
	 * A comma separated list of bug IDs
	 * @return
	 */
	public String ids();
	
}
