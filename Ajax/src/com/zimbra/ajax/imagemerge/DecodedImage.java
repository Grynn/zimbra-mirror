package com.zimbra.ajax.imagemerge;

import java.io.*;
import java.awt.image.*;

public abstract class DecodedImage {
    protected String mFilename;
    protected File mInputDir;
    protected int mCombinedRow = -1;
    protected int mCombinedColumn = -1;
    protected String mPrefix;

    public abstract BufferedImage getBufferedImage();
    public abstract int getWidth();
    public abstract int getHeight();

    public void setCombinedRow(int x) { mCombinedRow = x; }
    public int getCombinedRow() { return mCombinedRow; }

    public void setCombinedColumn(int x) { mCombinedColumn = x; }
    public int getCombinedColumn() { return mCombinedColumn; }

    /*
     * Get a JavaScript definition for this piece of the combined image.
     * expects combinedFilename to be of the form "megaimage.gif".
     */
    public String getJavaScriptRep(int combinedWidth,
                                   int combinedHeight,
                                   String combinedFilename,
								   int layoutStyle) 
    {
    	String filename = mFilename.substring(mFilename.lastIndexOf(File.separator)+1);
        String fileNameBase = filename.substring(0, filename.indexOf('.'));
        
        String bgImgStr = "background-image:url(\""+ mPrefix + combinedFilename + "\");";
        String bgPosStr = "background-position:" +
        				  ((mCombinedColumn == 0) ? "" : "-") + mCombinedColumn + "px " + 
        				  ((mCombinedRow == 0) ? "" : "-") + mCombinedRow + "px;";
        // NOTE: Images that are explicitly laid out horizontally are used as
        //		 vertical borders and should y-repeat. Likewise, images laid
        //		 out vertically are used for horizontal borders and should
        //		 x-repeat. All other images should be set no-repeat, unless
        //		 explicitly set as a repeat layout.
        String bgRptStr = "no-repeat";
        switch (layoutStyle) {
            case ImageMerge.HORIZ_LAYOUT: bgRptStr = "repeat-y"; break;
            case ImageMerge.VERT_LAYOUT: bgRptStr = "repeat-x"; break;
            case ImageMerge.REPEAT_LAYOUT: bgRptStr = "repeat"; break;
        }
        bgRptStr = "background-repeat:" + bgRptStr + ";";

        String widthStr = layoutStyle != ImageMerge.VERT_LAYOUT && layoutStyle != ImageMerge.REPEAT_LAYOUT 
        				? "width:" + getWidth() + "px;" : "";
        String heightStr = layoutStyle != ImageMerge.HORIZ_LAYOUT && layoutStyle != ImageMerge.REPEAT_LAYOUT 
        				 ? "height:" + getHeight() + "px;" : "";

        String className = ".Img" + fileNameBase;
        String backgroundStr = bgImgStr + bgPosStr + bgRptStr;
        String sizeStr = widthStr + heightStr;
        String otherStr = "overflow:hidden;";

        return className + "{" + backgroundStr + sizeStr + otherStr + "}\n";
    }
    
    public abstract void load() throws java.io.IOException, ImageMergeException;

    public String getFilename() { return mFilename; }
}