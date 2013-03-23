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
package com.zimbra.qa.selenium.projects.ajax.ui;





import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;

public class ApptView extends AbsDisplay {
	
	protected ApptView(AbsApplication application) {
		super(application);
		
		logger.info("new " + ApptView.class.getCanonicalName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}
	
	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zDisplayPressButton("+ button +")");
		
		tracer.trace("Click "+ button);

		throw new HarnessException("no logic defined for button: "+ button);
		
	}
	
	public boolean isApptExist(AppointmentItem appt) throws HarnessException {		
		String text= sGetText("css=div[id*=zli__CLWW__]");
		return ( text.contains(appt.getLocation()) &&
				 text.contains(appt.getSubject()) );	
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {
        return !sIsVisible("css=div#APPT_COMPOSE_1");
     
						
	}
	
}
