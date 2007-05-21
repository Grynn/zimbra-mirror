package com.zimbra.zme;

public interface ZmeListener {
	/**
	 * @param source Source of the action
	 * @param data Any data associated with the action
	 */
	void action(Object source,
			    Object data);
}
