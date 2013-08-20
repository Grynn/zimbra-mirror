/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2013 Zimbra Software, LLC.
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

    //
    // Data
    //

    private BufferedImage mBufImg;

    //
    // Constructors
    //

    public DecodedFullColorImage(String filename) {
        super(filename);
    }

    //
    // DecodedImage methods
    //

    public BufferedImage getBufferedImage() { return mBufImg; }

    public int getWidth() { return mBufImg.getWidth(); }
    public int getHeight() { return mBufImg.getHeight(); }

    /*
     * Load the contents of this image
     */
    public void load() throws IOException {
        String name = getFilename();
        int index = name.lastIndexOf('.');
        String suffix = index != -1 ? name.substring(index + 1) : "";
        Iterator iter = ImageIO.getImageReadersBySuffix(suffix);
        ImageReader reader = (ImageReader) iter.next();
        // make the input file be the input source for the ImageReader (decoder)
        reader.setInput(new FileImageInputStream(new File(mFilename)));
        mBufImg = reader.read(0);
    }

} // class DecodedFullColorImage