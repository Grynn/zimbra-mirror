/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 VMware, Inc.
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
package com.zimbra.cs.versioncheck;
/**
 * @author Greg Solovyev
 */
public class VersionUpdate {
	private String type;
	private String buildtype;
	private String platform;
	private String shortversion;
	private String version;
	private String release;
	private boolean critical;
	private String updateURL;
	private String description;
	
	public VersionUpdate () {
		
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getShortversion() {
		return shortversion;
	}
	public void setShortversion(String shortversion) {
		this.shortversion = shortversion;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public boolean isCritical() {
		return critical;
	}
	public void setCritical(boolean critical) {
		this.critical = critical;
	}
	public String getUpdateURL() {
		return updateURL;
	}
	public void setUpdateURL(String updateURL) {
		this.updateURL = updateURL;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getBuildtype() {
		return buildtype;
	}

	public void setBuildtype(String buildtype) {
		this.buildtype = buildtype;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}
