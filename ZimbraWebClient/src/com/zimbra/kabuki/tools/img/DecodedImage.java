/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
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


package com.zimbra.kabuki.tools.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Pattern;

public abstract class DecodedImage {

    //
    // Data
    //

	protected String mFilename;
    
    //
    // Constructors
    //

    public DecodedImage(String filename) {
        mFilename = filename;
    }

    //
    // Public methods
    //

    public abstract BufferedImage getBufferedImage();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void load() throws java.io.IOException;

	public String getName() {
		String fileName = mFilename;
		String fileNameBase = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

		// Strip the extension.
	    fileNameBase = fileNameBase.substring(0, fileNameBase.lastIndexOf('.'));

		// Strip any "repeat*" tiling derectives.  (Static layout has no directive.)
        for (ImageLayout layout : ImageLayout.values()) {
            if (layout.equals(ImageLayout.NONE)) continue;
            if (fileNameBase.endsWith(layout.toExtension())) {
                fileNameBase = fileNameBase.substring(0, fileNameBase.lastIndexOf('.'));
                break;
            }
        }

		return fileNameBase;
	}

    public String getFilename() {
        return mFilename;
    }

} // class DecodedImage