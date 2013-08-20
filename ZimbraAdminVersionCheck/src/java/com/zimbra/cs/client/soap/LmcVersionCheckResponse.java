/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.client.soap;
import com.zimbra.cs.versioncheck.VersionUpdate;
import java.util.List;
import java.util.ArrayList;
/**
 * @author Greg Solovyev
 */
public class LmcVersionCheckResponse extends LmcSoapResponse {
	private List <VersionUpdate> updates;
	private boolean status;
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public LmcVersionCheckResponse() {
		updates = new ArrayList();
	}
	
	public void addUpdate(VersionUpdate upd) {
		updates.add(upd);
	}
	
	public List <VersionUpdate> getUpdates() {
		return updates;
	}
}
