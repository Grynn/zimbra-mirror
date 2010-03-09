/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.doc.soap.util;

import java.util.*;
import com.zimbra.doc.soap.*;
import com.zimbra.doc.soap.template.*;

/**
 * This class represents a utility to generate all documentation for the Zimbra SOAP API.
 * 
 * @author sposetti
 *
 */
public	class	DocGenerator {
		
	private	static	final	String		ARG_SERVICE_SOURCE_DIR = "-service.src.dir";
	private	static	final	String		ARG_TEMPLATES_DIR = "-templates.dir";
	private	static	final	String		ARG_OUTPUT_DIR = "-output.dir";
	private	static	final	String		ARG_BUILD_VERSION = "-build.version";
	private	static	final	String		ARG_BUILD_DATE = "-build.date";

	private	static	String	serviceSrcDir = null;
	private	static	String	templatesDir = null;
	private	static	String	outputDir = null;
	private	static	String	buildVersion = null;
	private	static	String	buildDate = null;

	/**
	 * Reads the command line arguments.
	 * 
	 * @param	args		the arguments
	 */
	private static void readArguments(String[] args) {
		int	argPos = 0;
		
		if (args[argPos].equals(ARG_SERVICE_SOURCE_DIR)) {
			serviceSrcDir = args[++argPos];
			argPos++;
		}

		if (args[argPos].equals(ARG_TEMPLATES_DIR)) {
			templatesDir = args[++argPos];
			argPos++;
		}

		if (args[argPos].equals(ARG_OUTPUT_DIR)) {
			outputDir = args[++argPos];
			argPos++;
		}

		if (args[argPos].equals(ARG_BUILD_VERSION)) {
			buildVersion = args[++argPos];
			argPos++;
		}

		if (args[argPos].equals(ARG_BUILD_DATE)) {
			buildDate = args[++argPos];
			argPos++;
		}

	}
		
	/**
	 * Main
	 * 
	 * @param	args		the utility arguments
	 */
    public static void main(String[] args) throws Exception {
    	
       	readArguments(args);

       	// build services list
       	List	services = new LinkedList();
       	services.add(new com.zimbra.cs.service.account.AccountService());
       	services.add(new com.zimbra.cs.service.admin.AdminService());
       	services.add(new com.zimbra.cs.service.im.IMService());
       	services.add(new com.zimbra.cs.service.mail.MailService());
	
       	// create root services data model
       	Map rootContext = new HashMap();
       	rootContext.put(Root.PROP_SERVICE_LIST, services);
       	rootContext.put(Root.PROP_SERVICE_SRC_DIR, serviceSrcDir);
       	Root root = new Root(rootContext);
 
       	// create and process templates
       	Properties templateContext = new Properties();
       	templateContext.setProperty(TemplateHandler.PROP_TEMPLATES_DIR, templatesDir);
       	templateContext.setProperty(TemplateHandler.PROP_OUTPUT_DIR, outputDir);
       	templateContext.setProperty(TemplateHandler.PROP_BUILD_VERSION, buildVersion);
       	templateContext.setProperty(TemplateHandler.PROP_BUILD_DATE, buildDate);

       	// generate the API Reference documentation
       	ApiReferenceTemplateHandler templateHandler = new ApiReferenceTemplateHandler(templateContext);
       	templateHandler.process(root);
    }

} // end DocGenerator class