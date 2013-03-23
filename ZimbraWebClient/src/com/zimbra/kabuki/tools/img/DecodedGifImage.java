/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 VMware, Inc.
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

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class DecodedGifImage extends DecodedImage {

    //
    // Data
    //

    private GifDecoder d;
    private int mSortedColorTable[];

    //
    // Constructors
    //

    public DecodedGifImage(String filename) {
        super(filename);
        d = new GifDecoder();
    }

    //
    // Public methods
    //

    public boolean usesTransparency() { return d.transparency; }

    public int getTransparencyColor() {
        return d.gct[d.transIndex];
    }

    public int[] getImagesColorTable() {
        return d.gct;
    }

    public int[] getUniqueColorTable() {
        return mSortedColorTable;
    }

    //
    // DecodedImage methods
    //

    public BufferedImage getBufferedImage() { return d.getFrame(0); }
    
    public int getWidth() {
        return d.width;
    }
    public int getHeight() {
        return d.height;
    }

    /*
     * Load the image.  This includes parsing out the color table, transparency,
     * etc.  It will also determine the unique colors in this image.
     */
    public void load() throws IOException {
        load(false);
    }

    /*
     * Load the image.  This includes parsing out the color table, transparency,
     * etc.  It will also determine the unique colors in this image.
     * @param allowMultipleFrames True to allow multiple frames; else throw exception.
     */
    public void load(boolean allowMultipleFrames) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(mFilename));
            int status = d.read(in);
            if (status != GifDecoder.STATUS_OK) {
                System.err.println("ERROR " + status + " decoding " + mFilename);
                throw new ImageMergeException("ERROR " + status + " decoding " + mFilename);
            }
        }
        finally {
            try {
                in.close();
            }
            catch (Exception e) {
                // ignore
            }
        }

        int n = d.getFrameCount();
        if (n != 1 && !allowMultipleFrames) {
            System.err.println("ERROR: There are " + n + " frames in " + mFilename);
            throw new ImageMergeException("ERROR: There are " + n + " frames in " + mFilename);
        }
        
        // get unique colors used in image
        BufferedImage image = d.getImage();
        int rows = image.getHeight();
        int cols = image.getWidth();

        Map<Integer,Color> colors = new HashMap<Integer,Color>();
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                int argb = image.getRGB(x, y);
//                if ((argb & 0x0FF000000) == 0) continue; // skip transparent
                if (colors.get(argb) == null) {
                    colors.put(argb, new Color(argb));
                }
            }
        }
        image.flush();

        java.util.List<Integer> colorList = new LinkedList<Integer>();
        for (int argb : colors.keySet()) {
            colorList.add(argb);
        }
        Collections.sort(colorList, new Comparator<Integer>() {
            public int compare(Integer i1, Integer i2) {
                return i2.intValue() - i1.intValue();
            }
        });

        mSortedColorTable = new int[colorList.size()];
        Iterator<Integer> iter = colorList.iterator();
        for (int i = 0; iter.hasNext(); i++) {
            mSortedColorTable[i] = iter.next();
        }
    }

} // class DecodedGifImage