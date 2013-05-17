/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.mountpoints.viewer.actions;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.SeparateWindow;

public class LaunchInSeparateWindow extends CalendarWorkWeekTest {

	public LaunchInSeparateWindow() {
		logger.info("New "+ OpenInNewWindow.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(description = "Grantee with view rights launches grantor's calendar in the new window",
			groups = { "functional" })
			
	public void LaunchInSeparateWindow_01() throws HarnessException {
		String body = null;
		ZimbraAccount Owner = (new ZimbraAccount()).provision().authenticate();

		// Owner creates a folder, shares it with current user with viewer rights
		String ownerFoldername = "ownerfolder"+ ZimbraSeleniumProperties.getUniqueString();        
		Owner.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + ownerFoldername +"' l='1' view='appointment'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem ownerFolder = FolderItem.importFromSOAP(Owner, ownerFoldername);
		ZAssert.assertNotNull(ownerFolder, "Verify the new owner folder exists");
		
		Owner.soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ ownerFolder.getId() +"' op='grant'>"
				+			"<grant d='" + app.zGetActiveAccount().EmailAddress + "' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		

		// Current user creates the mountpoint that points to the share
		String mountpointFoldername = "mountpoint"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointFoldername +"' view='appointment' rid='"+ ownerFolder.getId() +"' zid='"+ Owner.ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointFoldername);
		ZAssert.assertNotNull(mountpoint, "Verify the subfolder is available");

		// Click to Refresh button to load the mounted shared calender 
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Launch shared folder in separate window through context menu
		SeparateWindow window = (SeparateWindow)app.zTreeCalendar.zTreeItem(Action.A_RIGHTCLICK, Button.B_LAUNCH_IN_SEPARATE_WINDOW, mountpoint);
       
		  try { 
			  	window.zWaitForActive();
			    body = window.sGetBodyText();
			    
			  	// Verify launched calender in new windows shows all calender data correctly
				ZAssert.assertStringContains(body, "Day Work Week Week Month" , "Verify calender views are shown in new window");
				ZAssert.assertStringContains(body, "Sunday Monday Tuesday Wednesday Thursday Friday Saturday" , "Verify weekday names are shown in new window");
				ZAssert.assertStringContains(body, ownerFoldername, "Verify owners calender name is displayed in new window");
				
	        }finally {
	        		 if ( window != null )
	        			 window.sClose();
	        		}    
	}

}
