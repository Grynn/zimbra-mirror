/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.container;

import java.io.File;

/**
 * Represents the data model used to represent development mode within the Jive
 * Wildfire plugin framework.
 *
 * @author Derek DeMoro
 */
public class PluginDevEnvironment {
    private File webRoot;
    private File classesDir;

    /**
     * Returns the document root of a plugins web development
     * application.
     *
     * @return the document root of a plugin.
     */
    public File getWebRoot() {
        return webRoot;
    }

    /**
     * Set the document root of a plugin.
     * @param webRoot the document root of a plugin.
     */
    public void setWebRoot(File webRoot) {
        this.webRoot = webRoot;
    }

    /**
     * Returns the classes directory of a plugin in development mode.
     * @return the classes directory of a plugin in development mode.
     */
    public File getClassesDir() {
        return classesDir;
    }

    /**
     * Sets the classes directory of a plugin used in development mode.
     * @param classesDir the classes directory.
     */
    public void setClassesDir(File classesDir) {
        this.classesDir = classesDir;
    }
}
