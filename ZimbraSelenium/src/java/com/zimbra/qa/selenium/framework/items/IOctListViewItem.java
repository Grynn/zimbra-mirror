package com.zimbra.qa.selenium.framework.items;

import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * This Interface is used by the Octopus application for any
 * item that can be represented in the Octopus file list
 * view, such as folders, files, shares
 * 
 * @author Matt Rhoades
 *
 */
public interface IOctListViewItem {

	/**
	 * Get the List View icon
	 * @return
	 * @throws HarnessException
	 */
	public String getListViewIcon() throws HarnessException;

	
	/**
	 * Set the List View icon
	 * @throws HarnessException
	 */
	public void setListViewIcon(String icon) throws HarnessException;
	
	/**
	 * Get the List View name
	 * @return
	 * @throws HarnessException
	 */
	public String getListViewName() throws HarnessException;

	
	/**
	 * Set the List View icon
	 * @throws HarnessException
	 */
	public void setListViewName(String name) throws HarnessException;
	
}
