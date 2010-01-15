/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.kabuki.util;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Colors {

	//
	// Constants
	//

	private static final Pattern RE_HEX3 = Pattern.compile("#?([0-9a-f])\\s*([0-9a-f])\\s*([0-9a-f])", Pattern.CASE_INSENSITIVE);
	private static final Pattern RE_HEX6 = Pattern.compile("#?([0-9a-f]{2})\\s*([0-9a-f]{2})\\s*([0-9a-f]{2})", Pattern.CASE_INSENSITIVE);
	private static final Pattern RE_RGB = Pattern.compile("(\\d+),(\\d+),(\\d+)");

	private static Map<String,Color> HTML;
	private static Map<String,Color> X11;
	private static Map<String,Color> WEBSAFE;
	private static Map<String,Color> COLORS;

	//
	// Constructors
	//

	private Colors() {}

	//
	// Public functions
	//

	public static Color getColor(String name) {
		if (COLORS == null) init();
		name = name.toLowerCase();
		Color color = COLORS.get(name);
		if (color == null) {
			color = Colors.decode(name);
			if (color != null) {
				COLORS.put(name, color);
			}
		}
		return color;
	}

	public static Set<String> getHtmlColorNames() {
		if (COLORS == null) init();
		return HTML.keySet();
	}

	public static Map<String,Color> getHtmlColorMap() {
		if (COLORS == null) init();
		return HTML;
	}

	public static Set<String> getX11ColorNames() {
		if (COLORS == null) init();
		return X11.keySet();
	}

	public static Map<String,Color> getX11ColorMap() {
		if (COLORS == null) init();
		return X11;
	}

	public static Set<String> getWebSafeColorNames() {
		if (COLORS == null) init();
		return WEBSAFE.keySet();
	}

	public static Map<String,Color> getWebSafeColorMap() {
		if (COLORS == null) init();
		return WEBSAFE;
	}

	//
	// Private functions
	//

	private static void init() {
		Map<String,Color> html = new HashMap<String,Color>();
		load("res/html.properties", html);
		HTML = html;

		Map<String,Color> x11 = new HashMap<String,Color>();
		load("res/x11.properties", x11);
		X11 = x11;

		Map<String,Color> websafe = new HashMap<String,Color>();
		load("res/websafe.properties", websafe);
		WEBSAFE = websafe;

		Map<String,Color> colors = new HashMap<String,Color>();
		colors.putAll(html);
		colors.putAll(x11);
		colors.putAll(websafe);
		COLORS = colors;
	}

	private static void load(String name, Map<String,Color> map) {
		InputStream in = null;
		try {
			in = Colors.class.getResourceAsStream(name);
			Properties props = new Properties();
			props.load(in);
			Enumeration keys = props.propertyNames();
			while (keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				String value = props.getProperty(key);
				map.put(key.toLowerCase(), Colors.decode(value));
			}
		}
		catch (Exception e) {
			System.err.println("error: "+e);
		}
		finally {
			try {
				in.close();
			}
			catch (Exception e) {
				// ignore
			}
		}
	}

	private static Color decode(String value) {
		Color color = null;
		int r = -1, g = -1, b = -1;
		Matcher hex3 = RE_HEX3.matcher(value);
		if (hex3.matches()) {
			r = Integer.parseInt(hex3.group(1)+hex3.group(1), 16);
			g = Integer.parseInt(hex3.group(2)+hex3.group(2), 16);
			b = Integer.parseInt(hex3.group(3)+hex3.group(3), 16);
		}
		else {
			Matcher hex6 = RE_HEX6.matcher(value);
			if (hex6.matches()) {
				r = Integer.parseInt(hex6.group(1), 16);
				g = Integer.parseInt(hex6.group(2), 16);
				b = Integer.parseInt(hex6.group(3), 16);
			}
			else {
				Matcher rgb = RE_RGB.matcher(value);
				if (rgb.matches()) {
					r = Integer.parseInt(rgb.group(1));
					g = Integer.parseInt(rgb.group(2));
					b = Integer.parseInt(rgb.group(3));
				}
			}
		}
		if (r != -1) {
			color = new Color(r, g, b);
		}
		return color;
	}

	//
	// MAIN
	//

	public static void main(String[] argv) throws Exception {
		if (argv.length > 0) {
			for (String arg : argv) {
				System.out.println(arg+" = "+Colors.getColor(arg));
			}
		}
		else {
			print("HTML Colors", Colors.getHtmlColorMap());
			print("Web Safe Colors", Colors.getWebSafeColorMap());
			print("X11 Colors", Colors.getX11ColorMap());
		}
	}

	private static void print(String section, Map<String,Color> map) {
			System.out.print("<h3>");
			System.out.print(section);
			System.out.println("</h3>");
			System.out.println("<table border=1>");
			int i = 0;
			for (String key : map.keySet()) {
				Color value = map.get(key);
				System.out.print("<tr><td>");
				System.out.print(++i);
				System.out.print("</td><td>");
				System.out.print(key);
				System.out.print("</td><td width=50 style='background-color:rgb(");
				System.out.print(value.getRed());
				System.out.print(",");
				System.out.print(value.getGreen());
				System.out.print(",");
				System.out.print(value.getBlue());
				System.out.println(")'>&nbsp;&nbsp;</td></tr>");
			}
			System.out.println("</table>");
	}

} // class Colors