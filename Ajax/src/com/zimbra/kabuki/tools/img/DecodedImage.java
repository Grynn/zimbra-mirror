/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    public void setCombinedRow(int x) {
        mCombinedRow = x;
    }

    public int getCombinedRow() {
        return mCombinedRow;
    }

    public void setCombinedColumn(int x) {
        mCombinedColumn = x;
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
        String filename = mFilename.substring(mFilename.lastIndexOf(File.separator) + 1);
        String fileNameBase = filename.substring(0, filename.indexOf('.'));

        // background image
        String bgImgStr = mPrefix + combinedFilename + "?v=@jsVersion@";
        
		// background position
        String bgPosStr = ((mCombinedColumn == 0) ? "" : "-") + mCombinedColumn + "px " +
                		  ((mCombinedRow == 0) ? "" : "-") + mCombinedRow + "px";

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
                ? "width:" + getWidth() + "px;" : "";
		
		// height
        String heightStr = mLayoutStyle != ImageMerge.VERT_LAYOUT && mLayoutStyle != ImageMerge.TILE_LAYOUT
                ? "height:" + getHeight() + "px;" : "";

		
		// CSS selector (may be further modified below)
        String selector = "";
        if (fileNameBase.indexOf("-") < 0) {
        	selector += ".Img" + fileNameBase;
        } else {
        	String[] list = fileNameBase.split("-");
        	for (int i = 0; i < list.length - 1; i++) {
        		selector += "." + list[i] + " ";
        	}
        	selector += ".Img" + list[list.length-1];
        }


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
							+ "#ENDIF";
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
}