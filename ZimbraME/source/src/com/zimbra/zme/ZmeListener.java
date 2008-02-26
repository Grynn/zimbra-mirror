/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.zme;

public interface ZmeListener {
	/**
	 * @param source Source of the action
	 * @param data Any data associated with the action
	 */
	void action(Object source,
			    Object data);
}
