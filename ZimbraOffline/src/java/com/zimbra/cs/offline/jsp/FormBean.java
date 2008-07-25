package com.zimbra.cs.offline.jsp;

import java.util.HashSet;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.offline.jsp.JspConstants.JspVerb;

public abstract class FormBean extends PageBean {

	protected JspVerb verb;
	
	private String error;
	
	private Set<String> invalids = new HashSet<String>();
	
	public FormBean() {}
	
	public void setVerb(String strVerb) {
		verb = strVerb != null ? JspVerb.fromString(strVerb) : null;
	}
	
	public boolean isNoVerb() {
		return verb == null;
	}
	
	public boolean isAdd() {
		return verb != null && verb.isAdd();
	}
	
	public boolean isModify() {
		return verb != null && verb.isModify();
	}
	
	public boolean isReset() {
		return verb != null && verb.isReset();
	}
	
	public boolean isDelete() {
		return verb != null && verb.isDelete();
	}	
	
	protected void setError(String error) {
		String failprefix = "system failure:";
		this.error = this.error == null ? (error == null ? getMessage("UnknownError") : (error.startsWith(failprefix) ? error.substring(failprefix.length()) : error)) : this.error;
	}
	
	protected void setExceptionError(ServiceException ex) {
	    String exMsg, exCode;	    
	    if (this.error != null || (exMsg = ex.getMessage()) == null || (exCode = ex.getCode()) == null)
	        return;	    
	    String msg = getMessage("exception." + exCode, false);
	    this.error = msg == null ? exCode + ": " + exMsg : msg;
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
		return !invalids.contains(name);
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
			error = getMessage("MissingRequired");
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
	
	protected boolean isValidPort(String input) {
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
	
	protected boolean isValidHost(String input) {
		return !isEmpty(input) && input.indexOf(':') < 0 && input.indexOf('/') < 0;
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
