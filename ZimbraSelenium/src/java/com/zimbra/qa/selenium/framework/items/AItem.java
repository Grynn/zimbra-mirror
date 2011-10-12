package com.zimbra.qa.selenium.framework.items;

public abstract class AItem {

	private String id = "0";
	
	protected AItem() {
	}

	/**
	 * Get the Zimbra ID of this item
	 * @return
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Set the Zimbra ID of this item
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Get the name of this item, such as subject, fileas, folder name, etc.
	 * @return
	 */
	public String getName() {
		return (getId());
	}
	
	/**
	 * Create a string version of this object suitable for using with a logger
	 */
	public abstract String prettyPrint();
}
