/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimlets
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */

/*
 * Created on May 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.zimbra.cs.zimlet.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zimbra.cs.zimlet.ZimletConf;
import com.zimbra.cs.zimlet.ZimletHandler;

/**
 * @author schemers
 *
 * NANP rules:
 * 
 *  
 */
public class NANPHandler implements ZimletHandler {

	// FIXME: this needs to be much more robust...
	// needed something for testing...
    private Pattern NANP_NUMBER_PATTERN = Pattern.compile(
    "\\b(\\(?\\d{3}\\)?[-. ])?\\d{3}[-. ]\\d{4}\\b");
    
    /**
     * first digit of area code or exchange must be 2-9
     * second and third digits can't both be '1' and '1'
     * @return
     */
    private boolean invalid(StringBuffer match, int offset) {
    	return (match.charAt(offset) == '0' ||
    			match.charAt(offset) == '1' ||
				(match.charAt(offset+1) == '1' &&
				match.charAt(offset+2) == '1'));
    }
    
	public String[] match(String text, ZimletConf config) {
        Matcher m = NANP_NUMBER_PATTERN.matcher(text);
        List<String> l = new ArrayList<String>();
        while (m.find()) {
        	String match = text.substring(m.start(), m.end());
        	StringBuffer digits = new StringBuffer(10);
        	for (int i=0; i < match.length(); i++) {
        		char c = match.charAt(i);
        		if (Character.isDigit(c))
        			digits.append(c);
        	}
        	if (invalid(digits, 0) ||
        		((digits.length() == 10 && invalid(digits, 3))))
        		continue;
        	l.add(match);
        }
        return (String[])l.toArray(new String[0]);
	}
	
	private void test(String str) {
		String[] res = match(str, null);
		System.out.print( res.length > 0 ? "true" : "false");
		System.out.print(" - ");
		System.out.println(str);
	}
	public static void main(String[] args) throws Exception {
		NANPHandler h = new NANPHandler();
		h.test(" phone: 555-1212 ");
		h.test(" phone: 1-213-555-1212 ");
		h.test(" (213) 555-1212 ");
		h.test("213-555-1212");
		h.test("213.555.1212");
		h.test("213 555 1212");
		h.test("1213-555-1212");
		h.test("213_555_1212");
		h.test("213-555-12123");
		h.test("213 555 1212123123");
		h.test("213 55531212");
		h.test("_Part_173_14431377.1162933715308");
	}
}
