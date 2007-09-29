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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import net.jmge.gif.Gif89Encoder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/*
 * Program to aggregate n GIFs into single GIF images.  This program
 * separates the GIFs into those requiring transparency and those that
 * don't require transparency (creating two output files).  
 */

public class ImageMerge {

	public static final int STATIC_LAYOUT = 0;
	public static final int HORIZ_LAYOUT = 1;
	public static final int VERT_LAYOUT = 2;
	public static final int TILE_LAYOUT = 3;

	public static String[] LAYOUT_EXTENSIONS = new String[4];
	public static String[] LAYOUT_MESSAGES = new String[4];

	public static final String NO_MERGE_DIR = "_nomerge.flag";
	public static final String IGNORE_DIR = "_ignore.flag";

	private static Options _mOptions = new Options();
	private Vector<File> _inputDirs = new Vector<File>();
	private String _outputDirName;
	private PrintWriter _cssOut;
	private PrintWriter _cacheFileOut;
	private String _cssPath;
	private boolean _isCopy;
	private boolean _incDisableCss = false;	// NOTE: deprecated, but not removed in the code

	static {
		LAYOUT_EXTENSIONS[STATIC_LAYOUT] = "";
		LAYOUT_EXTENSIONS[HORIZ_LAYOUT] = ".repeatx";
		LAYOUT_EXTENSIONS[VERT_LAYOUT] = ".repeaty";
		LAYOUT_EXTENSIONS[TILE_LAYOUT] = ".repeat";

		LAYOUT_MESSAGES[STATIC_LAYOUT] = "Aggregating static images for ";
		LAYOUT_MESSAGES[HORIZ_LAYOUT] = "Aggregating horizontal borders for ";
		LAYOUT_MESSAGES[VERT_LAYOUT] = "Aggregating vertical borders for ";
		LAYOUT_MESSAGES[TILE_LAYOUT] = "Copying background images for ";

		Option option = new Option("i", "input", true,
				"directories to load all images from. If there are multiple directories "
						+ "and we are aggregating images, then the output file will be named by "
						+ "the directory name of the first input directory");
		option.setRequired(true);
		_mOptions.addOption(option);

		option = new Option("c", "copy", false, "present if in copy (not merge) mode");
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

		option = new Option("f", "cache-file", true, "cache file name");
		option.setRequired(false);
		_mOptions.addOption(option);

		option = new Option("d", "disable-image", false, "Create disabled image CSS");
		option.setRequired(false);
		_mOptions.addOption(option);
	}

	private static void explainUsageAndExit() {
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
			explainUsageAndExit();
		}

		if (cl != null && cl.hasOption("i")) {
			String[] dirNames = cl.getOptionValue("i").split("[;,]");
			for (String dirName : dirNames) {
				_inputDirs.add(new File(dirName));
			}
		} else {
			explainUsageAndExit();
		}

		if (cl != null && cl.hasOption("o"))
			_outputDirName = cl.getOptionValue("o");
		else
			explainUsageAndExit();

		if (cl != null && cl.hasOption("c"))
			_isCopy = true;

//	MOW: this option is no longer needed by the web client, so skipping this
//	TODO: remove this option throughout the code.
//		if (cl != null && cl.hasOption("d"))
//			_incDisableCss = true;

		if (cl != null && cl.hasOption("p") && cl.hasOption("s")) {
			_cssPath = cl.getOptionValue("p");
			String cssFile = cl.getOptionValue("s");
			OutputStream cssFOS = new FileOutputStream(new File(_outputDirName, cssFile), true);
			_cssOut = new PrintWriter(cssFOS);
			String cacheFile = cl.getOptionValue("f");
			if (cacheFile != null) {
				OutputStream cacheFOS = new FileOutputStream(new File(_outputDirName, cacheFile), true);
				_cacheFileOut = new PrintWriter(cacheFOS);
			}
		} else {
			explainUsageAndExit();
		}
	}


	public static void main(String argv[]) throws Exception {
		try {
			ImageMerge merger = new ImageMerge();
			merger.process(argv);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void process(String argv[]) throws Exception {
		final String PROPERTY = "java.awt.headless";
		String ovalue = System.getProperty(PROPERTY);
		System.setProperty(PROPERTY, "true");

		parseArguments(argv);

		/* Directories that have a file called "_nomerge.flag" should always be copied.
		 * Directories that have a file called "_ignore.flag" should always be skipped.
		 */
		Vector<File> copyDirs = new Vector<File>();
		Vector<File> aggregateDirs = new Vector<File>();

		for (File f : _inputDirs) {
			boolean ignore = new File(f, IGNORE_DIR).exists();
			if (ignore) {
				continue;
			}
			boolean noMerge = new File(f, NO_MERGE_DIR).exists();
			if (_isCopy || noMerge) {
				copyDirs.add(f);
			}
			else {
				aggregateDirs.add(f);
			}
		}
		System.out.println("Copy: "+copyDirs);
		System.out.println("Aggregate: "+aggregateDirs);
		if (copyDirs.size() > 0) {
			processCopy(copyDirs);
		}
		if (aggregateDirs.size() > 0) {
			processAggregate(aggregateDirs);
		}

		_cssOut.close();
		if (_cacheFileOut != null) {
			_cacheFileOut.close();
		}

		if (ovalue != null) {
			System.setProperty(PROPERTY, ovalue);
		}
	}

	private void processAggregate(Collection<File> allInputDirs)
			throws IOException, ImageMergeException {
		Map<String, List<File>> dirmap = new HashMap<String, List<File>>();

		// collect like directories
		for (File dir : allInputDirs) {
			String dirname = dir.getName();
			List<File> dirlist = dirmap.get(dirname);
			if (dirlist == null) {
				dirlist = new LinkedList<File>();
				dirmap.put(dirname, dirlist);
			}
			dirlist.add(dir);
		}

		// process directories
		for (String imageFileName : dirmap.keySet()) {
			Collection<File> inputDirs = dirmap.get(imageFileName);
			processAggregate(inputDirs, imageFileName);
		}

	} // processAggregate(Collection)

	private void processAggregate(Collection<File> inputDirs, String imageFileName)
			throws IOException, ImageMergeException {
		File aggFile;
		DecodedFullColorImage orig[];
		String[] inputFilenames = getFilesOfType(inputDirs, "gif");

		int fileCount = inputFilenames.length;
		if (fileCount > 0) {
			// put into bins by layoutStyle
			Object[] sortedFiles = sortByLayoutStyle(inputFilenames, "gif");
			// aggregate static, horizontal and vertical images
			for (int layoutStyle = 0; layoutStyle < 3; layoutStyle++) {
				inputFilenames = (String[]) sortedFiles[layoutStyle];
				if (inputFilenames == null) continue;
				System.out.println(ImageMerge.LAYOUT_MESSAGES[layoutStyle] + "'" + imageFileName + "'");

				aggFile = new File(_outputDirName, imageFileName + ImageMerge.LAYOUT_EXTENSIONS[layoutStyle] + ".gif");
				aggFile.delete();
				processGIFs(aggFile, inputFilenames, imageFileName, layoutStyle);
			}

			// copy background images, since they need to tile
			inputFilenames = (String[]) sortedFiles[3];
			if (inputFilenames != null) {
				System.out.println(ImageMerge.LAYOUT_MESSAGES[3] + "'" + imageFileName + "'");
				copyImageFiles(inputFilenames, _outputDirName, "gif", true);
			}
		}

		inputFilenames = getFilesOfType(inputDirs, "png");
		fileCount = inputFilenames.length;
		if (fileCount > 0) {
			// sort by layoutStyle
			Object[] sortedFiles = sortByLayoutStyle(inputFilenames, "png");
			// aggregate static, horizontal and vertical images
			for (int layoutStyle = 0; layoutStyle < 3; layoutStyle++) {
				inputFilenames = (String[]) sortedFiles[layoutStyle];
				if (inputFilenames == null) continue;
				System.out.println(ImageMerge.LAYOUT_MESSAGES[layoutStyle] + "'" + imageFileName + "'");

				aggFile = new File(_outputDirName, imageFileName + ImageMerge.LAYOUT_EXTENSIONS[layoutStyle] + ".png");
				aggFile.delete();
				orig = new DecodedFullColorImage[fileCount];
				loadAndProcess(aggFile, inputFilenames, "png", orig, fileCount, imageFileName, layoutStyle);
			}

			// copy background images, since they need to tile
			inputFilenames = (String[]) sortedFiles[3];
			if (inputFilenames != null) {
				System.out.println(ImageMerge.LAYOUT_MESSAGES[3] + "'" + imageFileName + "'");
				copyImageFiles(inputFilenames, _outputDirName, "png", true);
			}
		}

		inputFilenames = getFilesOfType(inputDirs, "jpg");
		fileCount = inputFilenames.length;
		if (fileCount > 0) {
			Object[] sortedFiles = sortByLayoutStyle(inputFilenames, "jpg");
			for (int layoutStyle = 0; layoutStyle < 3; layoutStyle++) {
				inputFilenames = (String[]) sortedFiles[layoutStyle];
				if (inputFilenames == null) continue;
				System.out.println(ImageMerge.LAYOUT_MESSAGES[layoutStyle] + "'" + imageFileName + "'");

				aggFile = new File(_outputDirName, imageFileName + ImageMerge.LAYOUT_EXTENSIONS[layoutStyle] + ".jpg");
				aggFile.delete();
				orig = new DecodedFullColorImage[fileCount];
				loadAndProcess(aggFile, inputFilenames, "jpg", orig, fileCount, imageFileName, layoutStyle);
			}
			// copy background images, since they need to tile
			inputFilenames = (String[]) sortedFiles[3];
			if (inputFilenames != null) {
				System.out.println(ImageMerge.LAYOUT_MESSAGES[3] + "'" + imageFileName + "'");
				copyImageFiles(inputFilenames, _outputDirName, "jpg", true);
			}
		}
		// Just copy over .ico files.
		copyImageFiles(getFilesOfType(inputDirs, "ico"), _outputDirName, "ico", true);
	}

	private static void copyFile(File in,
								 File out)
			throws IOException {
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		byte[] buf = new byte[8192];
		int i;
		while ((i = fis.read(buf)) != -1)
			fos.write(buf, 0, i);
		fis.close();
		fos.close();
	}


	private void processCopy(Vector<File> inputDirs)
			throws IOException, ImageMergeException {
		copyImageFiles(getFilesOfType(inputDirs, "gif"), _outputDirName, "gif", true);
		copyImageFiles(getFilesOfType(inputDirs, "jpg"), _outputDirName, "jpg", true);
		copyImageFiles(getFilesOfType(inputDirs, "png"), _outputDirName, "png", true);
		copyImageFiles(getFilesOfType(inputDirs, "ico"), _outputDirName, "ico", true);
	}


	private void copyImageFiles(String filenames[],
								String outputDirname,
								String suffix,
								boolean createInParentDir
	)
			throws IOException, ImageMergeException {

		System.out.println("Copying " + suffix + " files...");
		String lastFile = "";
		for (String curFile : filenames) {
			// copy it to the destination directory
			// REVISIT: optimize this by passing in File objects...
			File parentDir = new File(curFile).getParentFile();
			String parentDirname = parentDir.getName();

			// create output directory
			// REVISIT: optimize this by creating the output dirs ahead of time...
			File outputDir;
			String outFilename = curFile.substring(curFile.lastIndexOf(File.separator) + 1);
			String combinedFilename;

			if (createInParentDir) {
				outputDir = new File(outputDirname + File.separator + parentDirname);
				combinedFilename = parentDirname + "/" + outFilename;
			} else {
				outputDir = new File(outputDirname);
				combinedFilename = outFilename;
			}

			if (!outputDir.mkdirs() && !outputDir.exists()) {
				throw new ImageMergeException("unable to create output directory");
			}

			// Skip decode for .ico files
			if (!suffix.equalsIgnoreCase("ico")) {
				// MOW: figure out if it's a stretchy image by the filename
				int layoutStyle = getLayoutStyleFromFilename(curFile, suffix);

				// load the image. GIF's get slightly special treatment.
				DecodedImage curImage;
				if (suffix.equalsIgnoreCase("gif")) {
					curImage = new DecodedGifImage(curFile,
							_cssPath, layoutStyle);
				} else {
					curImage = new DecodedFullColorImage(
							curFile, suffix, _cssPath, layoutStyle);
				}

				String debugMsg = "Copying image " + curFile
						+ (layoutStyle == ImageMerge.HORIZ_LAYOUT ? " as a horizontal border"
						: layoutStyle == ImageMerge.VERT_LAYOUT ? " as a vertical border"
						: layoutStyle == ImageMerge.TILE_LAYOUT ? " as a tiling background image"
						: "");
				System.out.println(debugMsg);
				curImage.load();
				curImage.setCombinedColumn(0);
				curImage.setCombinedRow(0);
				// add to the CSS output
				_cssOut.println(curImage.getCssString(curImage.getWidth(),
						curImage.getHeight(), combinedFilename, _incDisableCss));
			}

			//TODO: No need to copy files in the new build, but may need to later...
			//copyFile(new File(curFile), new File(outputDir, outFilename));

			// add to the pre-cache div
			String thisFile = _cssPath + combinedFilename;
			if (_cacheFileOut != null && !lastFile.equals(thisFile)) {
				_cacheFileOut.println("<img alt=\"\" src='" + thisFile + "?v=@jsVersion@'>");
				lastFile = thisFile;
			}
		}
		System.out.println("Copied " + filenames.length + " " + suffix + " images.");
	}


	private void loadAndProcess(File inputDir,
								String inputFilenames[],
								String type,
								DecodedFullColorImage originals[],
								int fileCount,
								String imageFileName,
								int layoutStyle
	)
			throws java.io.IOException {
		// load images
		for (int i = 0; i < fileCount; i++) {
			DecodedFullColorImage curImage = new DecodedFullColorImage(inputFilenames[i], type, _cssPath, layoutStyle);
			System.out.println("Loading image " + inputFilenames[i]);
			curImage.load();
			originals[i] = curImage;
		}

		System.out.println("Found " + fileCount + " " + type + " images.");

		// process the images
		processFullColorImages(inputDir, originals, fileCount, imageFileName, layoutStyle);
	}


	private static String[] getFilesOfType(Collection<File> inputDirs,
										   final String extension) {
		ArrayList<String> fileNameList = new ArrayList<String>();

		for (File dir : inputDirs) {
			String[] fileNames = dir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					int index = name.lastIndexOf("." + extension);
					if (index == -1 || (index + extension.length() + 1 != name.length())) {
						return false;
					} else {
						return (name.substring(index + 1).compareToIgnoreCase(extension) == 0);
					}
				}
			});

			if (fileNames == null)
				continue;

			String path = dir.getPath();
			for (String fileName : fileNames) {
				fileNameList.add(path + File.separator + fileName);
			}
		}
		Collections.sort(fileNameList);
		return fileNameList.toArray(new String[0]);
	}


	private static Object[] sortByLayoutStyle(String fileList[], String suffix) {
		Object[] typeList = new Object[4];
		ArrayList staticList = new ArrayList();
		ArrayList vertList = new ArrayList();
		ArrayList horizList = new ArrayList();
		ArrayList tileList = new ArrayList();

		typeList[ImageMerge.STATIC_LAYOUT] = staticList;
		typeList[ImageMerge.VERT_LAYOUT] = vertList;
		typeList[ImageMerge.HORIZ_LAYOUT] = horizList;
		typeList[ImageMerge.TILE_LAYOUT] = tileList;

		for (String file : fileList) {
			int layoutStyle = getLayoutStyleFromFilename(file, suffix);
			((ArrayList) typeList[layoutStyle]).add(file);
		}

		for (int i = 0; i < 4; i++) {
			ArrayList list = ((ArrayList) typeList[i]);
			if (list.size() == 0) {
				typeList[i] = null;
			} else {

				typeList[i] = list.toArray(new String[0]);
			}
		}
		return typeList;
	}


	/*
		 Given a filename (and file type suffix for convenience),
		 figure out what layoutStyle we should use for the image.

		 We do this based on the last few characters right before the suffix,
		 matching the following:

			 * HORIZ_LAYOUT =	".repeatx.[gif|png|jpg]"
			 * VERT_LAYOUT =	".repeaty.[gif|png|jpg]"
			 * TILE_LAYOUT =	".repeat.[gif|png|jpg]"
			 * all others are STATIC_LAYOUT

	 */
	private static int getLayoutStyleFromFilename(String curFile,
												  String suffix)
	{
		if (curFile.endsWith(LAYOUT_EXTENSIONS[HORIZ_LAYOUT] + "." + suffix)) {
			return HORIZ_LAYOUT;
		}

		if (curFile.endsWith(LAYOUT_EXTENSIONS[VERT_LAYOUT] + "." + suffix)) {
			return VERT_LAYOUT;
		}

		if (curFile.endsWith(LAYOUT_EXTENSIONS[TILE_LAYOUT] + "." + suffix)) {
			return TILE_LAYOUT;
		}

		return STATIC_LAYOUT;
	}


	private static int getMaxHeight(DecodedImage images[],
									int numImages) {
		int maxHeight = 0;

		for (int i = 0; i < numImages; i++) {
			// height is max of all seen widths
			if (images[i].getHeight() > maxHeight)
				maxHeight = images[i].getHeight();
		}
		return maxHeight;
	}


	private static int getMaxWidth(DecodedImage images[],
								   int numImages) {
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
										  int numImages) throws IOException {
		writeCSSAndGetOutputFile(extension, combinedWidth, combinedHeight, combinedFileName,
								 images, numImages, new LinkedList<Integer>());
	}

	private void writeCSSAndGetOutputFile(String extension,
										  int combinedWidth,
										  int combinedHeight,
										  String combinedFileName,
										  DecodedImage images[],
										  int numImages,
										  LinkedList<Integer> unmerged)
	throws java.io.IOException
	{
		// write out a CSS description of the combined image
		String lastFile = "";
		System.out.println("Writing CSS to "+_outputDirName);
		for (int i = 0; i < numImages; i++) {
			DecodedImage img = images[i];
			boolean bad = false;

			if (unmerged.contains(i)) {
				System.out.println("*** Unmerged file: "+img.getFilename());
				bad = true;
			}
			_cssOut.println(img.getCssString(combinedWidth, combinedHeight, combinedFileName, _incDisableCss, bad));
			String thisFile = _cssPath + combinedFileName;
			if (_cacheFileOut != null && !lastFile.equals(thisFile)) {
				_cacheFileOut.println("<img alt=\"\" src='" + thisFile + "?v=@jsVersion@'>");
				lastFile = thisFile;
			}
		}
	}

	private void processFullColorImages(File aggFile,
										DecodedFullColorImage originals[],
										int fileCount,
										String imageFileName,
										int layoutStyle)
			throws java.io.IOException {
		if (fileCount == 0)
			return;

		String type = originals[0].getSuffix();

		// dims[0] - width, dims[1] - height
		int[] dims = new int[2];
		placeImages(dims, originals, fileCount, layoutStyle);

		int combinedWidth = dims[0];
		int combinedHeight = dims[1];

		System.out.println("Combining " + fileCount + " images into a " + combinedWidth + "x" +
				combinedHeight + " image...");

		// create the combined image and write other images into it
		BufferedImage buffImg = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < fileCount; i++)
			// add this image's bits to the combined image
			addFullColorImageBits(buffImg, originals[i]);

		// write out the combined image

		writeCSSAndGetOutputFile(type, combinedWidth, combinedHeight, aggFile.getName(), originals, fileCount);
		Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix(type);
		ImageWriter writer = iter.next();
		writer.setOutput(new FileImageOutputStream(aggFile));
		writer.write(buffImg);
		writer.dispose();
	}


	/*
	 * Add the bits from the originalImg into the outputImg.  The originalImg
	 * knows where it should go in the combined image.
	 */
	public static void addFullColorImageBits(BufferedImage outputImg,
											 DecodedImage originalImg) {
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
										   int fileCount) {
		int flag;
		do {
			/* do...while loop to sort the array */
			flag = 0;
			for (int z = 0; z < (fileCount - 1); z++) {
				if (originals[z].getHeight() < originals[z + 1].getHeight()) {
					swap(originals, z);
					flag = 1;
				}
			}
		} while (flag != 0);
	}


	private void placeImages(int[] dims,
							 DecodedImage[] originals,
							 int fileCount,
							 int layoutStyle
	) {
		if (layoutStyle == STATIC_LAYOUT) {
			// scan to see the size characteristics of the input images
			dims[0] = getMaxWidth(originals, fileCount);
			dims[1] = placeImagesAuto(originals, fileCount, dims[0]);
		} else if (layoutStyle == VERT_LAYOUT) {
			dims[0] = placeVerticalImages(originals, fileCount);
			dims[1] = getMaxHeight(originals, fileCount);
		} else { // _HORIZ_LAYOUT
			dims[0] = getMaxWidth(originals, fileCount);
			dims[1] = placeHorizontalImages(originals, fileCount);
		}

		System.out.println(" Combining " + fileCount + " images into a " + dims[0] + "x" +
				dims[1] + " image...");
	}


	/**
	 * Arranges the images in a grid and sets their x,y coords for the combined image.

	 * @return The final height of the combined image.
	 */
	private static int placeImagesAuto(DecodedImage images[],
									   int numImages,
									   int combinedWidth) {
		// Ordered from tallest -> shortest
		sortImagesByHeight(images, numImages);

		int currentHeight = images[0].getHeight();   // one more than the bottom-most row of pixels in the
													 //	current image row.
		int currentTop = 0;						  // the top-most row of pixels in the current image row
		int currentColumn = 0;					   // the current column in the current image row
		for (int i = 0; i < numImages;) {
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

	private static int placeVerticalImages(DecodedImage images[],
										   int numImages) {
		int currentLeft = 0;
		for (int i = 0; i < numImages; i++) {
			images[i].setCombinedRow(0);
			images[i].setCombinedColumn(currentLeft);
			currentLeft += images[i].getWidth();
		}
		return currentLeft;
	}

	private static int placeHorizontalImages(DecodedImage images[],
											 int numImages) {
		int currentTop = 0;
		for (int i = 0; i < numImages; i++) {
			images[i].setCombinedRow(currentTop);
			images[i].setCombinedColumn(0);
			currentTop += images[i].getHeight();
		}
		return currentTop;
	}

	private void processGIFs(File aggFile,
							 String[] originals,
							 String imageFileName,
							 int layoutStyle)
	throws IOException, ImageMergeException
	{
		int fileCount = originals.length;

		if (fileCount == 0)
			return;

		// Keep a list of non-uniform file indexes.  These will be kept
		// separate.
		LinkedList<Integer> unmerged = new LinkedList<Integer>();

		DecodedGifImage origGIF[] = new DecodedGifImage[fileCount];

		// color (not index) of GIF transparency color
		boolean transIsSet = false;
		int transparencyColor = -1;

		// load the GIF images and check that the transparency is the same or not present
		for (int i = 0; i < fileCount; i++) {
			DecodedGifImage curImage = new DecodedGifImage(originals[i], _cssPath, layoutStyle);
			System.out.println("Loading image " + originals[i]);
			curImage.load();

			if (curImage.usesTransparency()) {
				if (!transIsSet) {
					// hasn't been set yet, so set it
					transIsSet = true;
					transparencyColor = curImage.getTransparencyColor();
				} else if (transparencyColor != curImage.getTransparencyColor()) {
					/*
					// this image uses transparency and not the color we support
					throw new ImageMergeException("Cannot handle images with different transparency");
					*/
					System.out.println("	Cannot handle images with different transparency");
					unmerged.add(i);
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
		placeImages(dims, origGIF, fileCount, layoutStyle);

		int combinedWidth = dims[0];
		int combinedHeight = dims[1];

		byte[][] combinedImageBits = new byte[combinedHeight][combinedWidth];
		for (int i = 0; i < fileCount; i++) {
			if (unmerged.contains(i)) {
				continue;
			}
			int holdColorTableCount = colorTableCount;
			try {
				// add image's colors to the combined color table
				colorTableCount = origGIF[i].addImageColors(colorTable, colorTableCount);
			} catch (Exception e) {
				/*
				throw new ImageMergeException("Caught exception while adding colors from image \""+originals[i]+"\" to colormap", e);
				*/
				// If the image uses a weird color palatte, just mark it and move on.
				System.out.println("Caught exception while adding colors from image \""+originals[i]+"\" to colormap");
				unmerged.add(i);
				continue;
			}

			try {
				// add image's bits to the combined image
				addImageBits(combinedImageBits, origGIF[i], colorTable, colorTableCount);
			} catch (Exception e) {
				//throw new ImageMergeException("Caught exception while adding image data from image \""+originals[i]+"\" to combined image", e);
				System.out.println("Caught exception while adding image data from image \""+originals[i]+"\" to combined image");
				colorTableCount = holdColorTableCount;
				unmerged.add(i);
				continue;
			}
		}

		// the Gif89Encoder requires the bits in a 1-D array
		byte combinedImage[] = new byte[combinedWidth * combinedHeight];
		for (int r = 0; r < combinedHeight; r++) {
			System.arraycopy(combinedImageBits[r], 0, combinedImage, r * combinedWidth, combinedWidth);
		}
		Gif89Encoder encoder = new Gif89Encoder(colorTable, combinedWidth, combinedHeight, combinedImage);
		encoder.setTransparentIndex(0);

		/*
		 * tell the GIF encoder to write out the GIF image.  if the input dir is
		 * /a/b/c and the output dir is /d, want to name the output files /d/c.css
		 * and /d/c.gif.
		 */
		writeCSSAndGetOutputFile("gif", combinedWidth, combinedHeight, aggFile.getName(), origGIF, fileCount, unmerged);
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
	public static void addImageBits(byte combinedImageBits[][],
									DecodedImage decodedImg,
									Color colorTable[],
									int colorTableCount)
	throws ImageMergeException {
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
			throws ImageMergeException {
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


	public void copyToDir(File src, File dst)
	throws IOException {
		//System.out.println("\t\t" + src+" to "+dst);

		FileChannel srcChannel = new FileInputStream(src).getChannel();
		FileChannel dstChannel = new FileOutputStream(dst).getChannel();
		dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
		srcChannel.close();
		dstChannel.close();
	}


}