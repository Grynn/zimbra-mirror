/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class StartDesktopClient extends Thread {
	protected static final Logger logger = LogManager.getLogger(StartDesktopClient.class);


	private String[] executablePath = null;
	private String[] params = null;

	public StartDesktopClient(String[] executablePath, String[] params) {

		this.executablePath = (String[])executablePath.clone();
		this.params = (String [])params.clone();

	}

	public void run() {
		try {
			logger.info(CommandLine.cmdExecWithOutput(executablePath, params));
		} catch (HarnessException e) {
			logger.error("Getting Harness Exception", e);
		} catch (IOException e) {
			logger.error("Getting IOException", e);
		} catch (InterruptedException e) {
			logger.error("Getting InterruptedException", e);
		}
	}
}