package com.zimbra.cs.offline.jsp;

import java.util.HashSet;
import java.util.Set;

public abstract class FormBean extends PageBean {

	private String error;
	
	private Set<String> invalids = new HashSet<String>();
	
	public FormBean() {}
	
	protected void setError(String error) {
		this.error = this.error == null ? (error == null ? "Unknown error" : error) : this.error;
		
	}
	
	public String getError() {
		return error;
	}
	
	protected void addInvalid(String name) {
		invalids.add(name);
	}
	
	public boolean isAllValid() {
		return invalids.isEmpty();
	}
	
	public boolean isAllOK() {
		return isAllValid() && getError() == null;
	}
	
	protected boolean isValid(String name) {
		return invalids.contains(name);
	}
	
	protected abstract void reload();

	protected abstract void doRequest();
	
	public static boolean isValid(FormBean formBean, String name) {
		return formBean.isValid(name);
	}
	
	public static void reload(FormBean formBean) {
		formBean.reload();
	}
	
	public static void doRequest(FormBean formBean) {
		formBean.doRequest();
	}
	
	protected String require(String input) {
		input = input == null ? null : input.trim();
		if (isEmpty(input))
			error = "missing required field";
		return input;
	}
	
	protected String optional(String input) {
		return input == null ? "" : input.trim();
	}
	
	protected boolean isEmpty(String input) {
		return input == null || input.trim().length() == 0;
	}
	
	protected boolean isValidNumber(String input) {
		if (isEmpty(input))
			return false;
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException x) {
			return false;
		}
	}
	
	protected boolean isValidPortNumber(String input) {
		if (isEmpty(input))
			return false;
		try {
			int port = Integer.parseInt(input);
			if (port <= 0 || port > 65535)
				return false;
			return true;
		} catch (NumberFormatException x) {
			return false;
		}
	}
	
	protected boolean isValidEmail(String input) {
		if (isEmpty(input))
			return false;
		int at = input.indexOf('@');
		if (at > 0 && at < input.length() - 1)
			return true;
		return false;
	}
}
