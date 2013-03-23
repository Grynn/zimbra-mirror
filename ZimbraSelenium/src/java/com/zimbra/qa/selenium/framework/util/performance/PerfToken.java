/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.framework.util.performance;

/**
 * A PerfToken is used to track one instance of measuring the browser performance
 * @author Matt Rhoades
 *
 */
public class PerfToken {
	
	private static int c = 1;
	
	private int t;
	
	public PerfToken() {
		t = c++;
	}
	
	public String toString() {
		return (""+ t);
	}

}
