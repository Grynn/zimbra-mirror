package com.zimbra.qa.selenium.projects.ajax.ui;

import java.awt.event.*;
import java.util.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;

public class SeparateWindowPrintPreview extends SeparateWindowShowOriginal {

	
	/**
	 * Create a new Print Preview Window
	 * 
	 * @param application
	 */
	public SeparateWindowPrintPreview(AbsApplication application) {
		super(application);
		
		// Initialize existing names to empty
		existingWindowNames = new ArrayList<String>();
		
		// Use zInitializeWindowNames() and zSetWindowName()
		// to set the new window name
		this.DialogWindowName = null;
	}

	protected boolean IsDismissed = false;
	
	/**
	 * Type "<ESC>" in the OS Print Dialog to close it
	 * 
	 * @throws HarnessException
	 */
	public void zDismissPrintDialog() throws HarnessException {
		
		if ( IsDismissed ) {
			return; // Already dismissed
		}
		
		// Wait for the print dialog
		SleepUtil.sleepMedium();
		zKeyboard.zTypeKeyEvent(KeyEvent.VK_ESCAPE);
		
		IsDismissed = true;
	}
	
	/**
	 * Determine if a new window opened
	 * Set DialogWindowName if found.
	 */
	public void zSetWindowName() throws HarnessException {
		zDismissPrintDialog(); // On the first attempt, dismiss the print dialog
		super.zSetWindowName();
	}
	

}
