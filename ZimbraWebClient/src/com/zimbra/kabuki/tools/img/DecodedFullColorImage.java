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

import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/*
 * DecodedFullColorImage represents a single PNG/JPG image that will be combined 
 * later.  It knows the original image's height, width, source filename, and 
 * target coordinates in the combined image.
 */
public class DecodedFullColorImage extends DecodedImage {

    private BufferedImage mBufImg;

    public DecodedFullColorImage(String filename,
                                 String suffix,
                                 String prefix,
                                 int layoutStyle
                                ) 
    {
        mFilename = filename;
        mSuffix = suffix;
        mPrefix = prefix;
        mLayoutStyle = layoutStyle;
    }

    public String getSuffix() { return mSuffix; }
    public BufferedImage getBufferedImage() { return mBufImg; }

    public int getWidth() { return mBufImg.getWidth(); }
    public int getHeight() { return mBufImg.getHeight(); }



    /*
     * Get a JavaScript definition for this piece of the combined image.
     * expects combinedFilename to be of the form "megaimage.gif".
     */
	/* MOW: moved the png-specific logic into DecodedImage.java since the code 
			here was mangling the original string, which made it hard to change.
	
    public String getCssString(int combinedWidth,
                               int combinedHeight,
                               String combinedFilename,
                               boolean includeDisableCss) {}
                               
     */
     

    /*
     * Load the contents of this image
     */
    public void load() 
        throws java.io.IOException
    {
        Iterator iter = ImageIO.getImageReadersBySuffix(mSuffix);
        ImageReader reader = (ImageReader) iter.next();
        // make the input file be the input source for the ImageReader (decoder)
        reader.setInput(new FileImageInputStream(new File(mInputDir, mFilename)));
        mBufImg = reader.read(0);
    }

}