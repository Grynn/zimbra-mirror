/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


package com.zimbra.kabuki.tools.img;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ImageMergeTask
		extends Task {

	//
	// Data
	//

	// required

	private List<DirSet> _inputDirs = new LinkedList<DirSet>();
	private File _outputDir = null;
	private String _cssFile = null;
	private String _cssPath = null;
	private String _jsFile = null;
	private File _cacheFile = null;

	// optional

	private String _layoutStyle = "auto";
	private boolean _copy = false;
	private boolean _disabledCss = false;

	//
	// Public methods
	//

	// required

	public void setDestDir(File dir) {
		_outputDir = dir;
	}

	public DirSet createDirSet() {
		DirSet dirset = new DirSet();
		_inputDirs.add(dirset);
		return dirset;
	}

	public void setCssFile(String filename) {
		_cssFile = filename;
	}

	public void setCssPath(String path) {
		_cssPath = path;
	}

	public void setJsFile(String filename) {
		_jsFile = filename;
	}

	public void setCacheFile(File file) {
		_cacheFile = file;
	}

	// optional
	public void setCopy(boolean copy) {
		_copy = copy;
	}

	public void setLayout(String layout) {
		_layoutStyle = layout;
	}

	public void setDisable(boolean disable) {
		_disabledCss = disable;
	}
	//
	// Task methods
	//

	public void execute() throws BuildException {

		// check required arguments
		if (_outputDir == null) {
			throw new BuildException("destination directory required -- use destdir attribute");
		}
		if (!_outputDir.exists()) {
			throw new BuildException("destination directory doesn't exist");
		}
		if (!_outputDir.isDirectory()) {
			throw new BuildException("destination must be a directory");
		}

		if (_inputDirs.size() == 0) {
			throw new BuildException("input directories required -- use nested <dirset> element(s)");
		}

		if (_cssFile == null || _cssFile.length() == 0) {
			throw new BuildException("css output file required -- use cssfile attribute");
		}

		if (_cssPath == null) {
			throw new BuildException("css path prefix required -- use csspath attribute");
		}

		if (!_layoutStyle.equals("auto") && !_layoutStyle.equals("horizontal") &&
				!_layoutStyle.equals("vertical") && !_layoutStyle.equals("repeat")) {
			throw new BuildException("layout must be specified as 'auto', 'horizontal', 'vertical', or 'repeat'");
		}

		// build argument list
		List<String> argList = new LinkedList<String>();

		Iterator<DirSet> iter = _inputDirs.iterator();
		StringBuffer dirs = new StringBuffer();
		while (iter.hasNext()) {
			DirSet dirset = iter.next();
			DirectoryScanner scanner = dirset.getDirectoryScanner(getProject());
			File baseDir = scanner.getBasedir();
			String baseDirName = baseDir.getAbsolutePath() + File.separator;
			String[] dirnames = scanner.getIncludedDirectories();
			for (String dirname : dirnames) {
				if (dirs.length() > 0) {
					dirs.append(';');
				}
				dirs.append(baseDirName).append(dirname);
			}
		}
		argList.add("-i");
		argList.add(dirs.toString());

		argList.add("-o");
		argList.add(_outputDir.getAbsolutePath());

		argList.add("-s");
		argList.add(_cssFile);

		argList.add("-p");
		argList.add(_cssPath);

		if (_cacheFile != null) {
			argList.add("-f");
			argList.add(_cacheFile.getAbsolutePath());
		}

		if (_jsFile != null) {
			argList.add("-j");
			argList.add(_jsFile);
		}

		if (!_layoutStyle.equals("auto")) {
			argList.add("-l");
			argList.add(_layoutStyle);
		}

		if (_copy) {
			argList.add("-c");
		}

		if (_disabledCss) {
			argList.add("-d");
		}

		// run program
		try {
			String[] argv = argList.toArray(new String[0]);
			System.out.print("ImageMerge");
			for (String anArgv : argv) {
				System.out.print(' ');
				System.out.print(anArgv);
			}
			System.out.println();
			ImageMerge.main(argv);
		}
		catch (Exception e) {
			throw new BuildException(e);
		}

	} // execute()

} // class ImageMergeTask