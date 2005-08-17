package com.liquidsys.liquidAjax.imagemerge;

import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.*;

import net.jmge.gif.*;
import org.apache.commons.cli.*;

/*
 * Program to aggregate n GIFs into single GIF images.  This program
 * separates the GIFs into those requiring transparency and those that
 * don't require transparency (creating two output files).  
 */
public class ImageMerge { 

	public static final int AUTO_LAYOUT = 0;
	public static final int VERT_LAYOUT = 1;
	public static final int HORIZ_LAYOUT = 2;
	public static final int REPEAT_LAYOUT = 3;
	
    private static Options _mOptions = new Options();
    private Vector _inputDirs = new Vector();
    private String _outputDirName;
    private FileOutputStream _cssFOS;
    private String _cssPath;
    private int _layoutStyle;
    private boolean _isCopy;

    static {
        Option option = new Option("i", "input", true, 
        		"directories to load all images from. If there are multiple directories "
        		+ "and we are aggregating images, then the output file will be named by "
				+ "the directory name of the first input directory");
        option.setRequired(true);
        _mOptions.addOption(option);

        option = new Option("c", "copy", false, "present if in copy (not merge) mode");
        option.setRequired(false);
        _mOptions.addOption(option);

        option = new Option("l", "layout", true, " a - automatic [default], v - all vertical, h - all horizontal. r - repeat. Useful for border images.");
        option.setRequired(false);
        _mOptions.addOption(option);

        option = new Option("o", "output", true, "name of directory to put resultant files in");
        option.setRequired(true);
        _mOptions.addOption(option);

        option = new Option("p", "css-path", true, "path for background-image:url in CSS file");
        option.setRequired(true);
        _mOptions.addOption(option);
        
        option = new Option("s", "css-file", true, "css file name");
        option.setRequired(true);
        _mOptions.addOption(option);
    }

    private static void usage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main [options]", _mOptions);
        System.exit(1);
    }

    private void parseArguments(String argv[]) 
    throws FileNotFoundException {
        CommandLineParser parser = new GnuParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(_mOptions, argv);
        } catch (ParseException pe) {
            System.out.println(pe);
            usage();
        }

        if (cl.hasOption("i")) {
        String[] dirNames = cl.getOptionValue("i").split("[;,]");
       	for (int i = 0; i < dirNames.length; i++) {
        		_inputDirs.add(new File(dirNames[i]));
        	}
    	} else {
            usage();
    	}

        if (cl.hasOption("o"))
            _outputDirName = cl.getOptionValue("o");
        else
            usage();

       if (cl.hasOption("l")) {
            String forceLayoutStr = cl.getOptionValue("l").toLowerCase();
            if (forceLayoutStr.startsWith("v"))
            	_layoutStyle = VERT_LAYOUT;
            else if (forceLayoutStr.startsWith("h"))
            	_layoutStyle = HORIZ_LAYOUT;
            else if (forceLayoutStr.startsWith("a"))
            	_layoutStyle = AUTO_LAYOUT;
            else if (forceLayoutStr.startsWith("r"))
            	_layoutStyle = REPEAT_LAYOUT;
            else
            	usage();
       } else {
       		_layoutStyle = AUTO_LAYOUT;
       }
       
       if (cl.hasOption("c"))
        _isCopy = true;
    
        
       if (cl.hasOption("p") && cl.hasOption("s")) {
            _cssPath = cl.getOptionValue("p");
            String cssFile = cl.getOptionValue("s");
            _cssFOS = new FileOutputStream(new File(_outputDirName, cl.getOptionValue("s")), true);
       } else {
            usage();
       }


    }

    private static String[] getFilesOfType(Collection inputDirs,
                                           final String extension) {
    	ArrayList fileNameList = new ArrayList();
    	
    	for (Iterator iter = inputDirs.iterator(); iter.hasNext();) {
    		File dir = (File)iter.next();
    		String[] fileNames = dir.list(new FilenameFilter() { 
                public boolean accept(File dir, String name) { 
                    int period = name.lastIndexOf(".");
                    if (period == -1 || (period == (name.length() - 1)))
                        return false;
                    else
                        return (name.substring(period + 1).compareToIgnoreCase(extension) == 0);
                }
    		});
    		
    		if (fileNames == null)
    			continue;
    		
    		String path = dir.getPath();
    		for (int i = 0; i < fileNames.length; i++) {
    			fileNameList.add(path + File.separator + fileNames[i]);
    		}
    	}
    	return (String[])fileNameList.toArray(new String[0]);
    }

    public static void main(String argv[]) throws Exception {
        ImageMerge merger = new ImageMerge();
        merger.process(argv);
    }

    public void process(String argv[]) throws Exception {
        final String PROPERTY = "java.awt.headless";
        String ovalue = System.getProperty(PROPERTY);
        System.setProperty(PROPERTY, "true");

        parseArguments(argv);
        if (_isCopy) {
           processCopy(_inputDirs);
        } else {
        	processAggregate(_inputDirs);
        }
        
        if (ovalue != null) {
            System.setProperty(PROPERTY, ovalue);
        }
    }
    
    private void processAggregate(Collection allInputDirs)
    throws IOException, ImageMergeException {
        Map dirmap = new HashMap();
        
        // collect like directories
        Iterator dirs = allInputDirs.iterator();
        while (dirs.hasNext()) {
            File dir = (File)dirs.next();
            String dirname = dir.getName();
            java.util.List dirlist = (java.util.List)dirmap.get(dirname);
            if (dirlist == null) {
                dirlist = new LinkedList();
                dirmap.put(dirname, dirlist);
            }
            dirlist.add(dir);
        }
        
        // process directories
        Iterator keys = dirmap.keySet().iterator();
        while (keys.hasNext()) {
            String imageFileName = (String)keys.next();
            Collection inputDirs = (Collection)dirmap.get(imageFileName);
            processAggregate(inputDirs, imageFileName);
        }
        
    } // processAggregate(Collection)

    private void processAggregate(Collection inputDirs, String imageFileName) 
    throws IOException, ImageMergeException { 
    	File aggFile;
    	DecodedFullColorImage orig[];
    	String[] inputFilenames = getFilesOfType(inputDirs, "gif");
    	
    	int numOrig = inputFilenames.length;
        if (numOrig> 0) {
           	aggFile = new File(_outputDirName, imageFileName + ".gif");
           	aggFile.delete();
	        processGIFs(aggFile, inputFilenames, imageFileName);        	
        }	
        
 		inputFilenames = getFilesOfType(inputDirs, "png");
        numOrig = inputFilenames.length;
        if (numOrig > 0) {
           	aggFile = new File(_outputDirName, imageFileName + ".png");
           	aggFile.delete();
            orig = new DecodedFullColorImage[numOrig];
	        loadAndProcess(aggFile, inputFilenames, "png", orig, numOrig, imageFileName);
         }

		inputFilenames = getFilesOfType(inputDirs, "jpg");
        numOrig = inputFilenames.length;
        if (numOrig > 0) {
           	 aggFile = new File(_outputDirName, imageFileName + ".jpg");
           	aggFile.delete();
            orig = new DecodedFullColorImage[numOrig];
	        loadAndProcess(aggFile, inputFilenames, "jpg", orig, numOrig, imageFileName);
         }
    }

    private static void copyFile(File in, 
                                 File out) 
    throws IOException 
    {
        FileInputStream fis  = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[8192];
        int i = 0;
        while((i = fis.read(buf)) != -1)
            fos.write(buf, 0, i);
        fis.close();
        fos.close();
    }


    private void processCopy(Vector inputDirs)
        throws IOException, ImageMergeException
    {
        StringBuffer sb = new StringBuffer();

 		copyImageFiles(getFilesOfType(inputDirs, "gif"), _outputDirName, sb, "gif");
		copyImageFiles(getFilesOfType(inputDirs, "jpg"), _outputDirName, sb, "jpg");
		copyImageFiles(getFilesOfType(inputDirs, "png"), _outputDirName, sb, "png");

		// write out CSS output
        _cssFOS.write(sb.toString().getBytes());
        _cssFOS.close();
    }


    private void copyImageFiles(String filenames[],
    								   String outputDirname,
                                       StringBuffer sb,
                                       String suffix) 
        throws IOException, ImageMergeException
    {

        System.out.println("Processing "+suffix+" files...");
        for (int i = 0; i < filenames.length; i++) {
            String curFile = filenames[i];
        
            // load the image. GIF's get slightly special treatment.
            DecodedImage curImage = (suffix.equalsIgnoreCase("gif")) ?
                (DecodedImage) new DecodedGifImage(curFile, _cssPath) :
                (DecodedImage) new DecodedFullColorImage(curFile, suffix, _cssPath);
            System.out.println("Loading image " + curFile);
            curImage.load();
            curImage.setCombinedColumn(0);
            curImage.setCombinedRow(0);

            // copy it to the destination directory
            // REVISIT: optimize this by passing in File objects...
        	File parentDir = new File(curFile).getParentFile();
        	String parentDirname = parentDir.getName();

            // create output directory
            // REVISIT: optimize this by creating the output dirs ahead of time...
        	File outputDir = new File(outputDirname + File.separator + parentDirname);
        	if (!outputDir.mkdirs() && !outputDir.exists()) {
        	    throw new ImageMergeException("unable to create output directory");
        	}
        	
        	String outFilename = curFile.substring(curFile.lastIndexOf(File.separator)+1);
            copyFile(new File(curFile), new File(outputDir, outFilename));

            // add to the CSS output
            String combinedFilename = parentDirname + '/' + outFilename; // NOTE: use URL path sep
            sb.append(curImage.getJavaScriptRep(curImage.getWidth(), curImage.getHeight(), combinedFilename, _layoutStyle));
        }
        System.out.println("Copied " + filenames.length + " " + suffix + " images.");

    }

    
    private void loadAndProcess(File inputDir,
                                       String inputFilenames[],
                                       String type,
                                       DecodedFullColorImage originals[],
                                       int numOriginals,
									   String imageFileName) 
        throws java.io.IOException 
    {
        // load images
        for (int i = 0; i < numOriginals; i++) {
            DecodedFullColorImage curImage = new DecodedFullColorImage(inputFilenames[i], type, _cssPath);
            System.out.println("Loading image " + inputFilenames[i]);
            curImage.load();
            originals[i] = curImage;
        }

        System.out.println("Found " + numOriginals + " " + type + " images.");

        // process the images
        processFullColorImages(inputDir, originals, numOriginals, imageFileName);
    }


    private static int getMaxHeight(DecodedImage images[],
            					    int numImages) {
        int maxHeight = 0;

        for (int i = 0; i < numImages; i++) {
            // width is max of all seen widths
            if (images[i].getHeight() > maxHeight)
                maxHeight = images[i].getHeight();
        }
        return maxHeight;
    }


    private static int getMaxWidth(DecodedImage images[],
                                   int numImages){
        int maxWidth = 0;

        for (int i = 0; i < numImages; i++) {
            // width is max of all seen widths
            if (images[i].getWidth() > maxWidth)
                maxWidth = images[i].getWidth();
        }
        return maxWidth;
    }


    private void writeCSSAndGetOutputFile(String extension,
                                                 int combinedWidth,
                                                 int combinedHeight,
												 String combinedFileName,
                                                 DecodedImage images[],
                                                 int numImages) 
    throws java.io.IOException {
        // write out a CSS description of the combined image
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < numImages; i++) {
            sb.append(images[i].getJavaScriptRep(combinedWidth, combinedHeight, combinedFileName, _layoutStyle));
        }        
        _cssFOS.write(sb.toString().getBytes());
    }

    private static int getTypeFromSuffix(String suffix) 
        throws ImageMergeException 
    {
        if (suffix.equalsIgnoreCase("jpg"))
            return BufferedImage.TYPE_3BYTE_BGR;
        else if (suffix.equalsIgnoreCase("png"))
            // XXX pretty sure this is right, but if colors are off check it
            return BufferedImage.TYPE_INT_ARGB;
        else 
            throw new ImageMergeException("Unknown image type " + suffix);
    }

    private void processFullColorImages(File aggFile,
    										   DecodedFullColorImage originals[],
                                               int numOriginals,
											   String imageFileName)
        throws java.io.IOException
    {
        if (numOriginals == 0)
            return;

        String type = originals[0].getSuffix();
        
        // dims[0] - width, dims[1] - height
        int[] dims = new int[2];
        placeImages(dims, originals, numOriginals);
        
        int combinedWidth = dims[0];
        int combinedHeight = dims[1];

        System.out.println("Combining " + numOriginals + " images into a " + combinedWidth + "x" + 
                           combinedHeight + " image...");

        // create the combined image and write other images into it
        BufferedImage buffImg = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < numOriginals; i++)
            // add this image's bits to the combined image
            addFullColorImageBits(buffImg, originals[i]);

        // write out the combined image

        writeCSSAndGetOutputFile(type, combinedWidth, combinedHeight, aggFile.getName(), originals, numOriginals);
        Iterator iter = ImageIO.getImageWritersBySuffix(type);
        ImageWriter writer = (ImageWriter) iter.next();
        writer.setOutput(new FileImageOutputStream(aggFile));
        writer.write(buffImg);
        writer.dispose(); 
    }


    /* 
     * Add the bits from the originalImg into the outputImg.  The originalImg
     * knows where it should go in the combined image.
     */
    public static void addFullColorImageBits(BufferedImage outputImg, 
                                             DecodedImage originalImg)
    {
        BufferedImage inputImg = originalImg.getBufferedImage();
        int originalImgWidth = originalImg.getWidth();
        int outputRow = originalImg.getCombinedRow();

        // iterate over all rows in the original, then all columns within a row, copying bits
        for (int inputRow = 0; inputRow < originalImg.getHeight(); inputRow++, outputRow++) {
            int columnBase = originalImg.getCombinedColumn();
            for (int inputColumn = 0; inputColumn < originalImgWidth; inputColumn++)
                outputImg.setRGB(columnBase + inputColumn, outputRow, inputImg.getRGB(inputColumn, inputRow));
        }
    }


    private static void swap(DecodedImage originals[],
    						 int index) {
    	DecodedImage temp = originals[index];
    	originals[index] = originals[index + 1];
    	originals[index + 1] = temp;
    }
    
    private static void sortImagesByHeight(DecodedImage originals[],
                                           int numOriginals)
    {
        int flag;
        do {
            /* do...while loop to sort the array */
            flag = 0;
            for(int z = 0; z < (numOriginals-1); z++) {
            	if (originals[z].getHeight() < originals[z+1].getHeight()) {
            			swap(originals, z);
            			flag = 1;
            	}
            }
        } while (flag != 0);
    }


    private void placeImages(int[] dims,
								    DecodedImage[] originals,
									int numOriginals) {
    	if (_layoutStyle == AUTO_LAYOUT) {
    		sortImagesByHeight(originals, numOriginals);
    		// scan to see the size characteristics of the input images
    		dims[0] = getMaxWidth(originals, numOriginals);
    		dims[1] = placeImagesAuto(originals, numOriginals, dims[0]);
    	} else if (_layoutStyle == VERT_LAYOUT) {
      		dims[0] = getMaxWidth(originals, numOriginals);
    		dims[1] = placeImagesVertical(originals, numOriginals);
    	} else { // _HORIZ_LAYOUT
    		dims[0] = placeImagesHorizontal(originals, numOriginals);
     		dims[1] = getMaxHeight(originals, numOriginals);
    	}

    	System.out.println("Combining " + numOriginals + " images into a " + dims[0] + "x" + 
    			dims[1] + " image...");    	
    }


    private static int placeImagesAuto(DecodedImage images[],
                                       int numImages,
                                       int combinedWidth)
    {
        int currentHeight = images[0].getHeight();   // one more than the bottom-most row of pixels in the
                                                        //    current image row.  
        int currentTop = 0;                             // the top-most row of pixels in the current image row
        int currentColumn = 0;                          // the current column in the current image row
        for (int i = 0; i < numImages; ) {
            if ((currentColumn + images[i].getWidth()) <= combinedWidth) {
                // fits without exceeding width constraint so place it
                images[i].setCombinedRow(currentTop);
                images[i].setCombinedColumn(currentColumn);
                currentColumn += images[i].getWidth();
                i++;
            } else {
                // exceeds width constraint on current row so it's the first image on the next row
                currentTop = currentHeight;
                currentHeight += images[i].getHeight();
                currentColumn = 0;
            }
        }
        return currentHeight;
    }

    private static int placeImagesHorizontal(DecodedImage images[],
		       								 int numImages) {
        int currentWidth= images[0].getWidth();         
        for (int i = 0; i < numImages; i++) {
        	images[i].setCombinedRow(0);
        	images[i].setCombinedColumn(currentWidth);
        	currentWidth += images[i].getWidth();
        }
        return currentWidth;
    }
     
    private static int placeImagesVertical(DecodedImage images[],
    								       int numImages) {
        int currentHeight = images[0].getHeight(); 
        int currentTop = 0;         
        for (int i = 0; i < numImages; i++) {
        	images[i].setCombinedRow(currentTop);
        	images[i].setCombinedColumn(0);
        	currentHeight += images[i].getHeight();
        }

        return currentHeight;
    }
     
    private void processGIFs(File aggFile,
    								String[] originals,
    								String imageFileName)
        throws IOException, ImageMergeException
    {
		int numOriginals = originals.length;
		
		if (numOriginals == 0)
			return;
		
        DecodedGifImage origGIF[] = new DecodedGifImage[numOriginals];

        // color (not index) of GIF transparency color
        boolean transIsSet = false;
        int transparencyColor = -1;  

        // load the GIF images and check that the transparency is the same or not present
        for (int i = 0; i < numOriginals; i++) {
            DecodedGifImage curImage = new DecodedGifImage(originals[i], _cssPath);
            System.out.println("Loading image " + originals[i]);
            curImage.load();

            if (curImage.usesTransparency()) {
                if (!transIsSet) {
                    // hasn't been set yet, so set it
                    transIsSet = true;
                    transparencyColor = curImage.getTransparencyColor();
                } else if (transparencyColor != curImage.getTransparencyColor()) {
                    // this image uses transparency and not the color we support
                    throw new ImageMergeException("Cannot handle images with different transparency");
                }
            }
            origGIF[i] = curImage;
        }

        // 
        // For each image, first make sure that its colors are present in the 
        // colorTable.  If not, add them.  Then map the index color in a given [x,y] 
        // position to the new index in the colorTable.  Fill in with index 0 in 
        // the space that should not be shown (e.g. columns 16-47 of 16x16 images 
        // when a 48x48 image is present).  
        //
        // Each DecodedImage will store its location in the resulting file in the
        // coordinates that this program uses (not the javascript output).
        //
        Color colorTable[] = new Color[256];  // this is the combined color table
        for (int i = 0; i < 256; i++)
            colorTable[i] = new Color(0);
        int colorTableCount = 0;
        if (transIsSet)
            // the transparent color's index is always 0
            colorTable[colorTableCount++] = new Color(transparencyColor);

        int[] dims = new int[2];
        placeImages(dims, origGIF, numOriginals);
        
        int combinedWidth = dims[0];
        int combinedHeight = dims[1];

        byte[][] combinedImageBits = new byte[combinedHeight][combinedWidth];
        for (int i = 0; i < numOriginals; i++) {
            // add image's colors to the combined color table
            colorTableCount = origGIF[i].addImageColors(colorTable, colorTableCount);

            // add image's bits to the combined image
            addImageBits(combinedImageBits, origGIF[i], colorTable, colorTableCount);
        }

        // the Gif89Encoder requires the bits in a 1-D array
        byte combinedImage[] = new byte[combinedWidth * combinedHeight];
        for (int r = 0; r < combinedHeight; r++)
            System.arraycopy(combinedImageBits[r], 0, combinedImage, r * combinedWidth, combinedWidth);
        Gif89Encoder encoder = new Gif89Encoder(colorTable, combinedWidth, combinedHeight, combinedImage);
        encoder.setTransparentIndex(0);

        /*
         * tell the GIF encoder to write out the GIF image.  if the input dir is 
         * /a/b/c and the output dir is /d, want to name the output files /d/c.css
         * and /d/c.gif.
         */
        writeCSSAndGetOutputFile("gif", combinedWidth, combinedHeight, aggFile.getName(), origGIF, numOriginals);
        FileOutputStream fos = new FileOutputStream(aggFile);
        encoder.encode(fos);
        fos.close();
    }

    
    /*
     * Place the bits of the image described by decodedImg into the combined 
     * image represented by combinedImageBits.  We use the colors in colorTable
     * as the color table for the addition.  This assumes that the colors 
     * needed by decodedImg are already present in the colorTable.  Top-left of
     * decodedImg is placed at (0, currentRow) in the combinedImg.
     */
    public static  void addImageBits(byte combinedImageBits[][],
                                     DecodedImage decodedImg,
                                     Color colorTable[],
                                     int colorTableCount)
        throws ImageMergeException
    {
        int decodedImgWidth = decodedImg.getWidth();
        int outputRow = decodedImg.getCombinedRow();
        BufferedImage buffImg = decodedImg.getBufferedImage();

        for (int inputRow = 0; inputRow < decodedImg.getHeight(); inputRow++, outputRow++) {
            // for each row in the original image, copy the RGB translation to combined bits
            int columnBase = decodedImg.getCombinedColumn();
            for (int inputCol = 0; inputCol < decodedImgWidth; inputCol++)
                combinedImageBits[outputRow][columnBase + inputCol] = 
                    getIndexOf(colorTable, colorTableCount, buffImg.getRGB(inputCol, inputRow));
        }
    }
                             

    /*
     * Get the index into the colorTable of the RGB color described by color.
     */
    private static byte getIndexOf(Color colorTable[],
                                   int colorTableCount,
                                   int color)
        throws ImageMergeException
    {
        /*
         * From what I can tell a getRGB on a pixel of an image will return
         * 0 if that pixel is supposed to be transparent and 0xFF000000 if
         * that pixel is supposed to be black.  But, for some reason, when
         * you create the color table, you frequently put in black as the
         * transparent color.  So you end up with a color table that has 
         * 0xFF000000 twice -- once for black, and once for the transparent
         * color.  This program is hard-coded to always use index 0 for the
         * transparent color in the images it generates.  There really isn't
         * any harm in that.
         */
        if (color == 0)
            return 0;  // transparent color's index is always 0
        for (int i = 1; i < colorTableCount; i++) {
            if (colorTable[i].getRGB() == color)
                return (byte) i;
        }
        System.err.println("ERROR: Cannot find color " + color);
        throw new ImageMergeException("ERROR: Cannot find color " + color);
    }

}
