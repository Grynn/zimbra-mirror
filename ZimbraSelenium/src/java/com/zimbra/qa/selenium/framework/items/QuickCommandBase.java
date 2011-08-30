package com.zimbra.qa.selenium.framework.items;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuickCommandBase {
	protected LinkedHashMap<String, Object> table = new LinkedHashMap<String, Object>();
	protected QuickCommandBase() {
	}
	
	protected String addEntry(String k, Object v) {
		String key = "\"" + k + "\"";
		String value = v.toString();
		
		if ( v instanceof String)
			value = "\""+ v.toString() +"\"";		// In the client, Strings are surrounded by quotes
		
		if ( v instanceof List<?>)
			value = v.toString().replace(" ", "");	// In the client, there are no spaces between list entries
		
		return (key + ":" + value);
	}
	
	public String toString() {
		StringBuilder sb = null;
		for (Map.Entry<String, Object> entry : table.entrySet()) {
			if ( sb == null ) {
				sb = new StringBuilder();
				sb.append(addEntry(entry.getKey(), entry.getValue()));
			} else {
				sb.append(",").append(addEntry(entry.getKey(), entry.getValue()));
			}
		}
		return (String.format("{%s}", sb == null ? "" : sb.toString()));
	}

}
