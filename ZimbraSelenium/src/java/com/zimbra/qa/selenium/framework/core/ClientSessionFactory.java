/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.framework.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Use the <code>ClientSessionFactory</code> class to retrieve
 * {@link ClientSession} instances.
 * <p>
 * The factory determines the current {@link ClientSession} instance
 * based on the current thread ID.
 * <p>
 * If no session exists, it is created.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public class ClientSessionFactory {
	private static Logger logger = LogManager.getLogger(ClientSessionFactory.class);
	
	/**
	 * Get the current client session for this test case
	 * @return
	 */
	public static ClientSession session() {
		long threadID = Thread.currentThread().getId();
		ClientSession session = getSingleton().getClientSession(Long.toString(threadID));
		logger.debug("Factory found session: "+ session.toString());
		return (session);
	}
	
	// ----
	// Singleton methods
	// ----
	private volatile static ClientSessionFactory singleton;

	private static ClientSessionFactory getSingleton() {
		if(singleton==null) {
			synchronized(ClientSessionFactory.class){
				if(singleton==null)
					singleton= new ClientSessionFactory();
			}
		}
		return (singleton);
	}
	// ----
	// End Singleton methods
	// ----

	// ----
	// Object methods
	// ----
	private Map<String, ClientSession> SessionMap = null;

	private ClientSessionFactory() {
		logger.info("New ClientSessionFactory");
		SessionMap = new HashMap<String, ClientSession>();
	}
	
	private ClientSession getClientSession(String threadID) {

		// If the session already exists for this ID, return it
		if ( SessionMap.containsKey(threadID) ) {
			return (SessionMap.get(threadID));
		}
		
		// If the session doesn't yet exist,
		// 1. Create it
		// 2. Insert it into the table
		// 3. return it
		ClientSession session = new ClientSession();
		SessionMap.put(threadID, session);
		
		return (session);
		
		
	}

	// ----
	// End Object methods
	// ----

}
