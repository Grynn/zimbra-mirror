/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */


package com.zimbra.kabuki.tools.img;

import java.awt.image.BufferedImage;
import java.io.File;

public abstract class DecodedImage {
    protected String mFilename;
    protected File mInputDir;
    protected int mCombinedRow = -1;
    protected int mCombinedColumn = -1;
    protected String mPrefix;
    protected String mSuffix;
    
    protected int mLayoutStyle;

    public abstract BufferedImage getBufferedImage();

    public abstract int getWidth();

    public abstract int getHeight();

    public void setCombinedRow(int r) {
        mCombinedRow = r;
    }

    public int getCombinedRow() {
        return mCombinedRow;
    }

    public void setCombinedColumn(int c) {
        mCombinedColumn = c;
    }

    public int getCombinedColumn() {
        return mCombinedColumn;
    }

    /*
     * Get a CSS definition for this piece of the combined image.
     * expects combinedFilename to be of the form "megaimage.gif".
     */
    public String getCssString(int combinedWidth,
                               int combinedHeight,
                               String combinedFilename,
                               boolean includeDisableCss) {
        return getCssString(combinedWidth, combinedHeight, combinedFilename, includeDisableCss, false);
    }

    public String getCssString(int combinedWidth,
                               int combinedHeight,
                               String combinedFilename,
                               boolean includeDisableCss,
                               boolean unmerged) {
        String fileNameBase = mFilename.substring(mFilename.lastIndexOf(File.separator) + 1);


	    // Strip the extension.
        fileNameBase = fileNameBase.substring(0, fileNameBase.lastIndexOf('.'));

	    // Strip any "repeat*" tiling derectives.  (Static layout has no directive.)
	    if (fileNameBase.endsWith(ImageMerge.LAYOUT_EXTENSIONS[ImageMerge.HORIZ_LAYOUT])
				|| fileNameBase.endsWith(ImageMerge.LAYOUT_EXTENSIONS[ImageMerge.VERT_LAYOUT])
				|| fileNameBase.endsWith(ImageMerge.LAYOUT_EXTENSIONS[ImageMerge.TILE_LAYOUT])) {
		    fileNameBase = fileNameBase.substring(0, fileNameBase.lastIndexOf('.'));
	    }

        // background image
        String bgImgStr = mPrefix + (unmerged ? getNameAfterBase(mFilename) : combinedFilename) + "?v=@jsVersion@";

        // background position
        String bgPosStr = (unmerged)
                            ? "0px 0px"
                            : getBgPosition();

		// background repeat
        // NOTE: Images that are explicitly laid out horizontally are used as
        //		 vertical borders and should y-repeat. Likewise, images laid
        //		 out vertically are used for horizontal borders and should
        //		 x-repeat. All other images should be set no-repeat, unless
        //		 explicitly set as a repeat layout.
        String bgRptStr = "no-repeat";
        switch (mLayoutStyle) {
            case ImageMerge.HORIZ_LAYOUT:
                bgRptStr = "repeat-x";
                break;
            case ImageMerge.VERT_LAYOUT:
                bgRptStr = "repeat-y";
                break;
            case ImageMerge.TILE_LAYOUT:
                bgRptStr = "repeat";
                break;
        }

		// width
        String widthStr = mLayoutStyle != ImageMerge.HORIZ_LAYOUT && mLayoutStyle != ImageMerge.TILE_LAYOUT
                ? "width:" + getWidth() + "px !important;" : "";
		
		// height
        String heightStr = mLayoutStyle != ImageMerge.VERT_LAYOUT && mLayoutStyle != ImageMerge.TILE_LAYOUT
                ? "height:" + getHeight() + "px !important;" : "";

		StringBuffer buffer = new StringBuffer();
	    String[] namePieces = fileNameBase.split("-");
	    for (String p : namePieces) {
		    buffer.append(" .");
		    buffer.append(p);
	    }

	    // CSS selector (may be further modified below)
        String selector = buffer.toString();

		// body of the style definition
		// NOTE:	IE doesn't process PNG graphics normally, so we output PNGs with
		//			the filter attribute in IE (using the #IFDEF syntax to make sure that
		//			it only shows up for IE)
		String styleBody;
        if (mSuffix.equalsIgnoreCase("png")) {
			styleBody = "background:" + bgPosStr + " " + bgRptStr + ";" + "\n"
							+ "#IFDEF MSIE\n" 
								+ "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + bgImgStr + "',sizingMethod='scale');\n"
							+ "#ELSE\n"
								+ "background-image:url(\""+bgImgStr+"\");"
							+ "\n#ENDIF\n";
		} else {
			styleBody = "background:url(\"" + bgImgStr + "\") " + bgPosStr + " " + bgRptStr + ";"
							+ widthStr + heightStr + "overflow:hidden;";
		}

        if (includeDisableCss) {
            return selector + "," + selector + "Dis" + "{" + styleBody + "}\n" 
            	 + selector + "Dis" + "{opacity:.3;\n#IFDEF MSIE\nfilter:alpha(opacity=30);\n#ENDIF\n}";
        } else {
            return selector + "{" + styleBody + "}";
        }
    }

    public abstract void load() throws java.io.IOException, ImageMergeException;

    public String getFilename() {
        return mFilename;
    }


    //
    // Protected
    //

    protected String getBgPosition() {
        return ((mCombinedColumn == 0) ? "" : "-") + mCombinedColumn + "px " +
               ((mCombinedRow == 0) ? "" : "-") + mCombinedRow + "px";
    }

    //
    // Private
    //

    /*
     * Get the relavent part of the image name, the part after "img/".
     */
    private static String getNameAfterBase(String fullname) {
        int i = fullname.lastIndexOf("img/");
        if (i == -1) {
            return null;
        }
        return fullname.substring(i+4);
    }
}