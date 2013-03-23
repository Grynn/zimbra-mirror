/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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

import java.util.*;

import org.apache.log4j.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.AutocompleteEntry.Icon;

/**
 * This bubble object describes an email address bubble ( both email addresses
 * and distribution lists).
 * 
 * @author zimbra
 *
 */
public class BubbleEmailAddress extends AbsBubble {
	protected static Logger logger = LogManager.getLogger(BubbleEmailAddress.class);

	/**
	 * Set if the bubble has a DL expansion icon
	 */
	protected String myLocatorExpandIcon = null;

	
	public static class Locators {
	}
	
	public BubbleEmailAddress(AbsApplication application) {	
		super(application);
		
		logger.info("new " + this.getClass().getCanonicalName());
	}
	
	
	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsBubble#parseBubble(java.lang.String)
	 */
	public AbsBubble parseBubble(String bubbleLocator) throws HarnessException {
		logger.info("Bubble.parseBubble(" + bubbleLocator + ")");
		
		String locator = null;

		this.myLocator = bubbleLocator;
		
		// Set the text contents (i.e. display name or email address)
		locator = bubbleLocator + ">span";
		if ( this.sIsElementPresent(locator) ) {
			this.setMyDisplayText( this.sGetText(locator).trim() );
		}

		// Determine if there is 'expand'/'+' on the bubble
		locator = bubbleLocator + " div[id$='_com_zimbra_email_expand']";
		if ( this.sIsElementPresent(locator) ) {
			myLocatorExpandIcon = locator;
		}
		
		logger.info(this.prettyPrint());

		return (this);
	}
	
	/**
	 * Determines if the expand icon (Distribution List) is present
	 * @return
	 * @throws HarnessException
	 */
	public boolean zHasExpandIcon() throws HarnessException {
		return (this.myLocatorExpandIcon != null);
	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsBubble#zItem(com.zimbra.qa.selenium.framework.ui.Action)
	 */
	public AbsPage zItem(Action action) throws HarnessException {
		logger.info(myPageName() + " zItem("+ action +")");

		tracer.trace(action +" on bubble = "+ this.toString());


		if ( action == null )
			throw new HarnessException("action cannot be null");

		AbsPage page = null;
		String locator = null;
		
		if ( action == Action.A_HOVEROVER ) {
			
			locator = this.myLocator;
			page = null; // TODO: which object should be returned?
			
			this.sMouseOver(locator);
			this.zWaitForBusyOverlay();

			if ( page != null ) {
				page.zWaitForActive();
			}
			
			return (page);
			
		} else if ( action == Action.A_EXPAND ) {
			
			if ( this.myLocatorExpandIcon == null ) {
				throw new HarnessException("No expand icon.  Did you call parseBubble() first?");
			}
			
			locator = this.myLocatorExpandIcon;
			page = null;
			
			// Use zAutocompleteList() to get the list of displayed members
			
			
			// FALL THROUGH
			
		} else if ( action == Action.A_REMOVE ) {
			
			throw new HarnessException("Implement action: "+ action);

		} else {
			
			// For all other actions,
			// pass control to the base class
			return (super.zItem(action));

		}
			
		if ( locator == null ) {
			throw new HarnessException("No locator defined for action " + action);
		}
		
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Locator not present "+ locator);
		}
	
		this.sClick(locator);
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		
		return (page);
	}
	
	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsBubble#zItem(com.zimbra.qa.selenium.framework.ui.Action, com.zimbra.qa.selenium.framework.ui.Button)
	 */
	public AbsPage zItem(Action action, Button option) throws HarnessException {
		logger.info(myPageName() + " zItem("+ action +", "+ option +")");

		tracer.trace(action +" then "+ option + " on bubble = "+ this.toString());


		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null )
			throw new HarnessException("option cannot be null");

		AbsPage page = null;
		String locator = null;
		String optionLocator = null;
		
		if ( action == Action.A_RIGHTCLICK ) {
			
			locator = this.myLocator;
			
			if ( option == Button.B_NEW_MAIL ) {
				
				// View mail bubble
				
				optionLocator = "implement me";
				page = null;
				
				throw new HarnessException("implement me");

			} else if ( option == Button.B_GO_TO_URL ) {
				
				// View mail bubble
				
				throw new HarnessException("implement me");
				
			} else if ( option == Button.B_ADD_TO_CONTACTS ) {
				
				// Compose bubble
				// and
				// View mail bubble
				
				throw new HarnessException("implement me");
				
			} else if ( option == Button.B_DELETE ) {
				
				// Compose bubble
				
				throw new HarnessException("implement me");
				
			} else if ( option == Button.B_EDIT ) {
				
				// Compose bubble
				
				throw new HarnessException("implement me");
				
			} else if ( option == Button.B_EXPAND ) {
				
				// Compose bubble
				
				throw new HarnessException("implement me");
				
			} else if ( option == Button.B_MOVE_TO_TO ) {
				
				// Compose bubble
				
				throw new HarnessException("implement me");
				
			} else if ( option == Button.B_MOVE_TO_CC ) {
				
				// Compose bubble
				
				throw new HarnessException("implement me");
				
			} else if ( option == Button.B_MOVE_TO_BCC ) {
				
				// Compose bubble
				
				throw new HarnessException("implement me");
				
			}
			
			if ( locator != null ) {
				
				// Right click on the bubble
				this.zRightClickAt(locator,"");
				this.zWaitForBusyOverlay();
				
				if ( optionLocator != null ) {
					
					// Click on the context menu
					this.zClickAt(optionLocator,"");
					this.zWaitForBusyOverlay();

				}

			}
			
			// FALL THROUGH
			
		} else {
			throw new HarnessException("No logic for action "+ action);
		}
		
		if ( page != null ) {
			page.zWaitForActive();
		}

		// Default behavior
		return (page);

	}
	
	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsBubble#zItem(com.zimbra.qa.selenium.framework.ui.Action, com.zimbra.qa.selenium.framework.ui.Button, com.zimbra.qa.selenium.framework.ui.Button)
	 */
	public AbsPage zItem(Action action, Button option, Button subOption) throws HarnessException {
		logger.info(myPageName() + " zItem("+ action +", "+ option +", "+ subOption +")");

		tracer.trace(action +" then "+ option + " then "+ subOption +" on bubble = "+ this.toString());


		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null )
			throw new HarnessException("option cannot be null");
		if ( subOption == null )
			throw new HarnessException("subOption cannot be null");

		AbsPage page = null;
		String locator = null;
		String optionLocator = null;
		String subOptionLocator = null;
		
		if ( action == Action.A_RIGHTCLICK ) {
			
			locator = this.myLocator;
			
			if ( option == Button.B_FIND_EMAILS ) {
				
				// View mail bubble
				optionLocator = "implement me";
				subOptionLocator = "implement me";
				page = null;

				if ( subOption == Button.B_SENT_TO_RECIPIENT ) {

					optionLocator = "implement me";
					subOptionLocator = "implement me";
					page = null;

					throw new HarnessException("implement me");

				} else if ( subOption == Button.B_RECEIVED_FROM_RECIPIENT ) {
					
					optionLocator = "implement me";
					subOptionLocator = "implement me";
					page = null;

					throw new HarnessException("implement me");

				}

			} else if ( option == Button.B_ADD_TO_FILTER ) {
				
				// View mail bubble
				
				throw new HarnessException("implement me");
				
			}
			
			if ( locator != null ) {
				
				// Right click on the bubble
				this.zRightClickAt(locator,"");
				this.zWaitForBusyOverlay();
				
				if ( optionLocator != null ) {
					
					// Click on the context menu
					this.zClickAt(optionLocator,"");
					this.zWaitForBusyOverlay();

					if ( subOptionLocator != null ) {
						
						// Click on the context menu
						this.zClickAt(optionLocator,"");
						this.zWaitForBusyOverlay();

					}
				}

			}
			
			// FALL THROUGH
			
		} else {
			throw new HarnessException("No logic for action "+ action);
		}
		
		if ( page != null ) {
			page.zWaitForActive();
		}

		// Default behavior
		return (page);

	}

	protected AutocompleteEntry parseAutocompleteEntry(String itemLocator) throws HarnessException {
		logger.info(myPageName() + " parseAutocompleteEntry()");

		String locator = null;
		
		// Get the icon
		locator = itemLocator + " td.Icon div@class";
		String image = this.sGetAttribute(locator);
		
		// Get the address
		locator = itemLocator + " td + td";
		String address = this.sGetText(locator);
		
		AutocompleteEntry entry = new AutocompleteEntry(
									Icon.getIconFromImage(image), 
									address, 
									false,
									itemLocator);

		return (entry);
	}
	

	/**
	 * After expanding a DL bubble, this method returns a list of members that are displayed
	 * @return
	 * @throws HarnessException
	 */
	public List<AutocompleteEntry> zAutocompleteListGetEntries() throws HarnessException {
		logger.info(myPageName() + " zAutocompleteListGetEntries()");
		
		List<AutocompleteEntry> items = new ArrayList<AutocompleteEntry>();
		
		String containerLocator = "css=div[id^='ZmAutocompleteListView']";

		if ( !this.zWaitForElementPresent(containerLocator,"5000") ) {
			// Autocomplete is not visible, return an empty list.
			return (items);
		}


		/**

		  <div  id="ZmAutocompleteListView_1" x-display="block" parentid="z_shell" class="ZmAutocompleteListView" style="position: absolute; overflow: auto; display: block; left: 732px; top: 175px; z-index: 750;">
    		<table id="DWT51" border="0" cellpadding="0" cellspacing="0">
		      <tbody>
		        <tr id="ZmAutocompleteListView_1_acRow_selectAll" class="acRow">
		          <td class="Icon"><div class="ImgBlank16"></div></td>
		          <td>Select all 2 addresses</td>
		        </tr>
		
		        <tr id="ZmAutocompleteListView_1_acRow_1" class="acRow">
		          <td class="Icon"><div class="ImgGALContact"></div></td>
		          <td>enus13543177962824@testdomain.com</td>
		        </tr>
		
		        <tr id="ZmAutocompleteListView_1_acRow_2" class="acRow">
		          <td class="Icon"><div class="ImgGALContact"></div></td>
		          <td>enus13543177980605@testdomain.com</td>
		        </tr>
		      </tbody>
		    </table>
		  </div>

		 */
		
		
		String rowsLocator = containerLocator + " tr[id*='_acRow_']";
		int count = this.sGetCssCount(rowsLocator);
		for (int i = 1; i < count; i++) {
			
			// The first row (acRow_0) is the "select all" ... skip that one
			
			items.add(parseAutocompleteEntry(containerLocator + " tr[id$='_acRow_"+ i +"']"));
			
		}
		
		return (items);
	}

	

	@Override
	public String myPageName() {
		return (this.getClass().getCanonicalName());
	}



}
