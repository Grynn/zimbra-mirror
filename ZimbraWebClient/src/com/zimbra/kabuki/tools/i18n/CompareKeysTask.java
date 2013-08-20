/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.kabuki.tools.i18n;

import java.io.*;
import java.util.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class CompareKeysTask
	extends Task {

	//
	// Constants
	//

	static final String PLAT_WINDOWS = "win";
	static final String PLAT_MACINTOSH = "mac";
	static final String PLAT_LINUX = "linux";

	static final String PREFIX_KEYS = "keys.";
	static final String SUFFIX_KEYCODE = ".keycode";

	static final String EXT_PROPERTIES = ".properties";

	static final String P_SHIFT = "keys.shift.display";
	static final String P_CTRL = "keys.ctrl.display";
	static final String P_ALT = "keys.alt.display";
	static final String P_META = "keys.meta.display";

	//
	// Data
	//

	// required

	private boolean verbose = false;
	private File dir;
	LinkedList<String> basenames = new LinkedList<String>();

	//
	// Public methods
	//

	// required

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public KeyFile createKeyfile() {
		return new KeyFile();
	}

	//
	// Task methods
	//

	public void execute() throws BuildException {

		// check arguments
		if (this.dir == null) {
			throw new BuildException("directory required");
		}
		if (!this.dir.exists()) {
			throw new BuildException("not a directory");
		}
		if (this.basenames.size() == 0) {
			throw new BuildException("basename required");
		}

		// collect locales
		String primaryBasename = this.basenames.getLast();
		File dir = new File(this.dir, primaryBasename.replaceAll("^/","")).getParentFile();
		final String PREFIX_BASENAME = primaryBasename.replaceAll("^.*/", "");
		String[] filenames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (!name.startsWith(PREFIX_BASENAME) || !name.endsWith(EXT_PROPERTIES)) {
					return false;
				}
				return name.charAt(PREFIX_BASENAME.length()) == '_';
			}
		});
		Set<Locale> locales = new TreeSet<Locale>(new LocaleComparator());
		locales.add(Locale.US);
		for (String filename : filenames) {
			String locid = filename.replaceAll("^.*_([a-z]{2}(_[A-Z]{2})?).*$", "$1");
			String[] parts = locid.split("_");
			Locale locale = parts.length > 1 ? new Locale(parts[0], parts[1]) : new Locale(parts[0]);
			locales.add(locale);
		}

		// custom class loader to pull in resource bundles from dir
		ClassLoader loader = new ClassLoader(Thread.currentThread().getContextClassLoader()) {
			public InputStream getResourceAsStream(String path) {
				try {
					File file = new File(CompareKeysTask.this.dir, path);
					return new FileInputStream(file);
				}
				catch (FileNotFoundException e) {
					return super.getResourceAsStream(path);
				}
			}
		};

		// check keycodes for each locale
		String[] PLATFORMS = { PLAT_WINDOWS, PLAT_MACINTOSH, PLAT_LINUX };
		for (Locale locale : locales) {
			// convert resource bundle to properties
			Properties allProps = new Properties();
			for (String basename : this.basenames) {
				ResourceBundle bundle = ResourceBundle.getBundle(basename, locale, loader);
				for (Enumeration keys = bundle.getKeys(); keys.hasMoreElements(); ) {
					String name = (String)keys.nextElement();
					allProps.setProperty(name, bundle.getString(name));
				}
			}
			// check each platform
			for (String platform : PLATFORMS) {
				boolean isUS = locale.equals(Locale.US);
				String locIdStr = !isUS ? " ["+locale+"]" : ""; 
				System.out.println(primaryBasename+locIdStr+", platform: "+platform);

				// normalize and get modifier keywords
				Properties props = normalize(allProps, platform);
				String shift = props.getProperty(P_SHIFT).toLowerCase();
				String ctrl = props.getProperty(P_CTRL).toLowerCase();
				String alt = props.getProperty(P_ALT).toLowerCase();
				String meta = props.getProperty(P_META).toLowerCase();

				// build key/cmd map
				Map<String, List<String>> commandMap = new HashMap<String,List<String>>();
				Map<String, List<String>> errorMap = new HashMap<String,List<String>>();
				for (Enumeration names = props.propertyNames(); names.hasMoreElements(); ) {
					String value = (String)names.nextElement();
					// ignore everything that's not a keycode *and* modifier keys
					if (!value.endsWith(SUFFIX_KEYCODE)) continue;
					if (value.startsWith(PREFIX_KEYS)) continue;
					// add each keycode to map separately
					String[] codes = props.getProperty(value).split(";");
					for (String code : codes) {
                        try {
                            code = normalize(code, shift, ctrl, alt, meta);
                            List<String> cmds = commandMap.get(code);
                            if (cmds == null) {
                                commandMap.put(code, cmds = new LinkedList<String>());
                            }
                            cmds.add(value);
                        }
                        catch (UnknownModifierException e) {
							List<String> errs = errorMap.get(value);
							if (errs == null) {
								errorMap.put(value, errs = new LinkedList<String>());
							}
							errs.add("unknown modifier \""+e.getModifier()+"\" for \""+value+"\"");
                        }
					}
				}

				// list errors
				for (String value : new TreeSet<String>(errorMap.keySet())) {
					for (String err : errorMap.get(value)) {
						System.out.println("  Error: "+err);
					}
				}

				// list duplicates
				List<String> keycodes = new LinkedList<String>(commandMap.keySet());
				Collections.sort(keycodes);
				for (String keycode : keycodes) {
					List<String> commands = commandMap.get(keycode);

					// organize by category
					Map<String,List<String>> categoryMap = new TreeMap<String,List<String>>();
					boolean hasConflict = false;
					for (String cmd : commands) {
						String category = cmd.replaceAll("^([^\\.]+).*$","$1");
						List<String> commandList = categoryMap.get(category);
						if (commandList == null) {
							categoryMap.put(category, commandList = new LinkedList<String>());
						}
						commandList.add(cmd);
						if (commandList.size() > 1) {
							hasConflict = true;
						}
					}

					// only show if there is at least one conflict
					if (this.verbose || hasConflict) {
						System.out.println("  Keycode: \""+keycode+'"');
						for (String category : categoryMap.keySet()) {
							List<String> commandList = categoryMap.get(category);
							if (!this.verbose && commandList.size() < 2) continue;
							for (String command : commandList) {
								System.out.println("    "+command.substring(0, command.length() - SUFFIX_KEYCODE.length()));
							}
						}
					}
				}
			}
		}
	} // execute()

	//
	// Static functions
	//

	static String normalize(String keycode, String shift, String ctrl, String alt, String meta) {
		StringBuilder str = new StringBuilder();
		String[] parts = keycode.replaceAll("\\s+","").split(",");
		for (int j = 0; j < parts.length; j++) {
			if (j > 0) {
				str.append(",");
			}
			boolean hasShift = false, hasCtrl = false, hasAlt = false, hasMeta = false;
			String[] keys = parts[j].split("\\+");
			for (int i = 0; i < keys.length - 1; i++) {
                String key = keys[i].toLowerCase();
				if (key.equals(shift)) hasShift = true;
				else if (key.equals(ctrl)) hasCtrl = true;
				else if (key.equals(alt)) hasAlt = true;
				else if (key.equals(meta)) hasMeta = true;
                else {
                    throw new UnknownModifierException(keys[i]);
                }
			}
			if (hasShift) str.append("Shift+");
			if (hasCtrl) str.append("Ctrl+");
			if (hasAlt) str.append("Alt+");
			if (hasMeta) str.append("Meta+");
			str.append(keys[keys.length-1]);
		}
		return str.toString();
	}

	static Properties normalize(Properties props, String platform) {
		Properties nProps = new Properties();
		// copy base values, ignoring platform specific overrides
		Enumeration names = props.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String)names.nextElement();
			nProps.setProperty(name, props.getProperty(name));
		}
		// replace platform specific overrides
		names = props.propertyNames();
		while (names.hasMoreElements()) {
			String fullname = (String)names.nextElement();
			if (fullname.endsWith(platform)) {
				String name = fullname.substring(0, fullname.length() - platform.length() - 1);
				nProps.setProperty(name, props.getProperty(fullname));
			}
		}
		return nProps;
	}

	//
	// Classes
	//

	public class KeyFile {
		public void setBasename(String basename) {
			CompareKeysTask.this.basenames.add(basename);
		}
	}

	/**
	 * <strong>Note:</strong>
	 * The TreeSet class would throw a ClassCastException for objects
	 * that don't implement <code>Comparable</code> so I'm providing my
	 * own comparator to avoidac the problem.
	 */
	static class LocaleComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o1.equals(Locale.US)) return -1;
			if (o2.equals(Locale.US)) return 1;
			return o1.toString().compareTo(o2.toString());
		}
	}

    static class UnknownModifierException extends RuntimeException {
        // Data
        private String modifier;
        // Constructors
        public UnknownModifierException(String modifier) {
            this.modifier = modifier;
        }
        // Public methods
        public String getModifier() {
            return this.modifier;
        }
    }

} // class CompareKeysTask