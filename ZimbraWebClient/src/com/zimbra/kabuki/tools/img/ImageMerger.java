/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2010 Zimbra, Inc.
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class ImageMerger {

    //
    // Constants
    //

    private static final String O_INPUT = "i";
    private static final String O_OUTPUT = "o";
    private static final String O_CSS_PATH = "p";
    private static final String O_CSS_FILENAME = "s";
    private static final String O_JS_FILENAME = "j";
    private static final String O_CACHE_FILENAME = "f";
    private static final String O_COPY = "c";
    private static final String O_NO_APPEND = "a";
    private static final String O_VERBOSE = "v";
    private static final String O_DISABLED_IMAGES = "d";

    private static final Options OPTIONS = new Options();

    // extension file filters
    private static final FileFilter F_GIF = new ExtensionFileFilter(".gif");
    private static final FileFilter F_JPG = new ExtensionFileFilter(".jpg", ".jpeg");
    private static final FileFilter F_PNG = new ExtensionFileFilter(".png");

    // layout file filters
    private static final FileFilter F_HORIZONTAL = new SubExtensionFileFilter(ImageLayout.HORIZONTAL.toExtension());
    private static final FileFilter F_VERTICAL = new SubExtensionFileFilter(ImageLayout.VERTICAL.toExtension());
    private static final FileFilter F_TILE = new SubExtensionFileFilter(ImageLayout.TILE.toExtension());
    private static final FileFilter F_NONE = new AndFileFilter(
        new NotFileFilter(F_HORIZONTAL), new NotFileFilter(F_VERTICAL), new NotFileFilter(F_TILE)
    );

    // image factories
    private static final ImageFactory IF_GIF = new GifImageFactory();
    private static final ImageFactory IF_JPG = new FullColorImageFactory();
    private static final ImageFactory IF_PNG = IF_JPG;

    // static initialization
    static {
        addOption(
            O_INPUT, "input", true,
            "directories to load all images from. If there are multiple directories " +
            "and we are aggregating images, then the output file will be named by " +
            "the directory name of the first input directory", true
        );
        addOption(O_OUTPUT, "output", true, "name of directory to put resultant files in", true);
        addOption(O_CSS_PATH, "css-path", true, "path for background-image:url in CSS file", true);
        addOption(O_CSS_FILENAME, "css-file", true, "css file name", true);
        addOption(O_CACHE_FILENAME, "cache-file", true, "cache file name", false);
        addOption(O_JS_FILENAME, "js-file", true, "JavaScript file name", false);
        addOption(O_COPY, "copy", false, "present if in copy (not merge) mode", false);
        addOption(O_NO_APPEND, "no-append", false, "don't append to output file", false);
        addOption(O_VERBOSE, "verbose", false, "verbose output", false);
        addOption(O_DISABLED_IMAGES, "disable-image", false, "(Deprecated) Create disabled image CSS", false);
    }

    private static void addOption(String shortParam, String longParam,
                                  boolean takesArg, String description,
                                  boolean required) {
        Option option = new Option(shortParam, longParam, takesArg, description);
        option.setRequired(required);
        OPTIONS.addOption(option);
    }

    //
    // Data
    //

    // properties

    private File outputDir;
    private String cssPath;
	private String spacerImagesPath;
    private String cssFilename;
    private String jsFilename;
    private String cacheFilename;
    private boolean copyFiles = false;
    private boolean appendOutput = true;
    private boolean verbose = false;

    // state

    private PrintWriter cssOut;
    private PrintWriter jsOut;
    private PrintWriter cacheOut;

    //
    // Public methods
    //

    public void process(List<File> dirs) throws IOException {
        final String P_HEADLESS = "java.awt.headless";
        String headlessValue = null;
        try {
            // avoid graphics window
            headlessValue = System.getProperty(P_HEADLESS);
            System.setProperty(P_HEADLESS, "true");

            // create output streams
            boolean jsNeedsHeader = !(new File(outputDir, jsFilename).exists());
            cssOut = open(cssFilename);
            jsOut = open(jsFilename);
            cacheOut = open(cacheFilename);

            // generate output
            if (jsNeedsHeader) {
                println(jsOut, "if (!window.AjxImgData) AjxImgData = {};");
            }
            for (File dir : dirs) {
                // ignore directory entirely
                if (new File(dir, "_ignore.flag").exists()) {
                    System.out.println("ignoring "+dir);
                    continue;
                }

                // only copy files
                if (copyFiles || new File(dir, "_nomerge.flag").exists()) {
                    System.out.println("copying "+dir);
                    processCopy(dir);
                    continue;
                }

                // merge
                if (verbose) System.out.println("processing "+dir);
                processMerge(dir);
            }
        }
        finally {
            // close streams
            close(cssOut);
            close(jsOut);
            close(cacheOut);

            // restore previous graphics value
            if (headlessValue != null) {
                System.setProperty(P_HEADLESS, headlessValue);
            }
        }
    }

    // properties

    public void setOutputDirectory(File dir) {
        outputDir = dir;
    }
    public void setCssPath(String path) {
        cssPath = path.endsWith("/") ? path.substring(0, path.length()-1) : path;
    }
	public void setSpacerImagesPath(String path) {
		spacerImagesPath = path;
	}
    public void setCssFilename(String name) {
        cssFilename = name;
    }
    public void setCacheFilename(String name) {
        cacheFilename = name;
    }
    public void setJsFilename(String name) {
        jsFilename = name;
    }
    public void setCopyFiles(boolean copy) {
        copyFiles = copy;
    }
    public void setAppendOutput(boolean append) {
        appendOutput = append;
    }
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    //
    // Private methods
    //

    // processing

    private void processMerge(File dir) throws IOException {
        processMerge(dir, dir.listFiles(F_GIF), IF_GIF, "1x1-trans.gif");
        processMerge(dir, dir.listFiles(F_JPG), IF_JPG, null);
        processMerge(dir, dir.listFiles(F_PNG), IF_PNG, "1x1-trans.png");
    }

    private void processMerge(File dir, File[] files, ImageFactory factory, String spacerFileName) throws IOException {
        if (files.length == 0) return;
        processMerge(dir, listFiles(files, F_NONE), factory, ImageLayout.NONE, spacerFileName);
        processMerge(dir, listFiles(files, F_HORIZONTAL), factory, ImageLayout.HORIZONTAL, null);
        processMerge(dir, listFiles(files, F_VERTICAL), factory, ImageLayout.VERTICAL, null);
        processCopy(dir, listFiles(files, F_TILE), factory);
    }

    private void processMerge(File dir, File[] files, ImageFactory factory,
                              ImageLayout layout, String spacerFileName) throws IOException {
        if (files.length == 0) return;
        if (verbose) System.out.println("merging files with layout "+layout.toString().toLowerCase());

        // load images
        AggregateImage aggregate = factory.createAggregateImage(layout);
        List<ImageEntry> all = new LinkedList<ImageEntry>();
        for (File file : files) {
            DecodedImage image = factory.loadImage(file);
            if (image == null) {
                System.err.println("error: unable to load image "+file);
                continue;
            }

            ImageEntry entry = new ImageEntry(dir, file, image, layout);
            printlnCache(entry);
            all.add(entry);
        }

		ImageEntry spacerEntry = null;
		if (spacerFileName != null) {
			File spacerFile = new File(spacerImagesPath + spacerFileName);
			DecodedImage spacerImage = factory.loadImage(spacerFile);
			spacerEntry = new ImageEntry(dir, spacerFile, spacerImage, layout, true);
		}

        // process images
        for (int filecount = 1; all.size() > 0; filecount++) {
            aggregate.reset();

            // aggregate sub-images
            Iterator<ImageEntry> allEntries = all.iterator();
            while (allEntries.hasNext()) {
                ImageEntry entry = allEntries.next();

                // leave for next pass if unacceptable
                if (!aggregate.acceptSubImage(entry)) {
                    continue;
                }
				else if (spacerEntry != null && allEntries.hasNext()) {
					aggregate.acceptSubImage(spacerEntry);
				}

                // original image is now processed
                allEntries.remove();
            }

            // stop if we've done a full pass and nothing was processed
            List<ImageEntry> subEntries = aggregate.entries();
            if (subEntries.size() == 0) break;

            // only one entry in aggregate, treat as single
            if (subEntries.size() == 1) {
                ImageEntry entry = subEntries.get(0);
                if (verbose) System.out.println("generating output for "+entry.image.getName());
                printlnJs(entry);
                printlnCss(entry);
            }

            // generate output for aggregate
            else {
                String ext = files[0].getName().replaceAll("^.*\\.", ".");
                String filename = dir.getName()+(filecount>1?""+filecount:"")+layout.toExtension()+ext;
                File file = new File(outputDir, filename);
                System.out.println("generating "+file);
                aggregate.saveImage(file);
                for (ImageEntry entry : subEntries) {
					if (entry.isSpacer) { // no need to create JS or CSS for spacer
						continue;
					}
                    if (verbose) System.out.println("generating output for "+entry.image.getName());
                    entry.filename = filename;
                    printlnJs(entry);
                    printlnCss(entry);
                }
            }

        }

        // generate output for anything left
        for (ImageEntry entry : all) {
            if (verbose) System.out.println("generating output for "+entry.image.getName());
            printlnJs(entry);
            printlnCss(entry);
        }

    } // processMerge(File,File[],ImageFactory,ImageLayout)

    private void printlnCss(ImageEntry entry) {
        if (cssOut == null) return;

        // print normal info
        printlnCss(entry.filename, entry.image, entry.x, entry.y, entry.layout);

        // print IE overlay info
        String name = entry.image.getName();
        if (name.endsWith("Overlay")) {
            // NOTE: Keep in sync with output of AjxImg.js.
            print(cssOut, ".IEImage ");
            printlnCss(entry.iefilename, entry.image, 0, 0, ImageLayout.NONE);
        }
    }

    private void printlnCss(String filename, DecodedImage image, int x, int y, ImageLayout layout) {
        String selector = toSelector(image.getName());
        String url = cssPath+"/"+filename.replace(File.separatorChar,'/')+"?v=@jsVersion@";
        print(cssOut, "%s {", selector);

        // conditional properties for PNGs
        boolean isPng = filename.toLowerCase().endsWith(".png");
        if (isPng) {
            println(cssOut);
            println(cssOut, "#IFDEF MSIE_LOWER_THAN_7");
            println(cssOut, "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='%s',sizingMethod='crop');", url);
            println(cssOut, "background-repeat:%s;", layout.toCss());
            println(cssOut, "position:relative;");
            println(cssOut, "top:%dpx;left:%dpx;", negative(y), negative(x));
            println(cssOut, "#ELSE");
            println(cssOut, "background:url('%s') %dpx %dpx %s;", url, negative(x), negative(y), layout.toCss());
            println(cssOut, "#ENDIF");
        }

        // background image properties for non-PNGs
        else {
            print(cssOut, "background:url('%s') %dpx %dpx %s;", url, negative(x), negative(y), layout.toCss());
        }

        // common properties
        boolean isNone = layout.equals(ImageLayout.NONE);
        if (isNone) {
            print(cssOut, "width:%dpx !important;", image.getWidth());
            print(cssOut, "height:%dpx !important;", image.getHeight());
        }
        print(cssOut, "overflow:hidden;");

        println(cssOut, "}");
    }

    private static int negative(int value) {
        return value > 0 ? -value : value;
    }

    private String toSelector(String s) {
        String[] parts = s.split("-");
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) str.append(' ');
            str.append('.');
            str.append(parts[i]);
        }
        return str.toString();
    }

    private void printlnJs(ImageEntry entry) {
        if (jsOut == null) return;
        // TODO: should we output the info for all of the images???
        String name = entry.image.getName();
        if (name.endsWith("Overlay") || name.endsWith("Mask")) {
            println(
                jsOut,
                "AjxImgData[\"%s\"]={t:%d,l:%d,w:%d,h:%d,f:\"%s/%s\",ief:\"%s/%s\"};",
                name, -entry.y, -entry.x,
                entry.image.getWidth(), entry.image.getHeight(),
                cssPath, entry.filename.replace(File.separatorChar,'/'),
                cssPath, entry.iefilename.replace(File.separatorChar,'/')
            );
        }
    }

    private void printlnCache(ImageEntry entry) {
        println(
            cacheOut,
            "<img alt=\"\" src='%s/%s?v=@jsVersion@'>",
            cssPath, entry.filename.replace(File.separatorChar,'/')
        );
    }

    private void processCopy(File dir) throws IOException {
        processCopy(dir, dir.listFiles(F_GIF), IF_GIF);
        processCopy(dir, dir.listFiles(F_JPG), IF_JPG);
        processCopy(dir, dir.listFiles(F_PNG), IF_PNG);
    }
    private void processCopy(File dir, File[] files, ImageFactory factory) throws IOException {
        if (files.length == 0) return;

        for (File file : files) {
            DecodedImage image = factory.loadImage(file, true);
            if (image == null) {
                System.err.println("error: unable to load image "+file);
                continue;
            }
            ImageEntry entry = new ImageEntry(dir, file, image, ImageLayout.fromFile(file));
            printlnJs(entry);
            printlnCss(entry);
        }
    }

    // utilities: i/o

    private PrintWriter open(String filename) {
        if (filename == null) return null;
        return open(new File(outputDir, filename));
    }

    private PrintWriter open(File file) {
        try { return new PrintWriter(new FileOutputStream(file, appendOutput)); }
        catch (Exception e) { return null; }
    }

    private static void print(PrintWriter out, String format, Object... args) {
        if (out != null) out.printf(format, args);
    }

    private static void println(PrintWriter out) {
        print(out, "\n");
    }

    private static void println(PrintWriter out, String format, Object... args) {
        print(out, format, args);
        println(out);
    }

    private static void close(PrintWriter out) {
        try { out.close(); }
        catch (Exception e) { /* ignore */ }
    }

    // utilities: messages

    private static void printWithPrefix(String prefix, String format, Object... args) {
        System.err.printf(prefix+": "+format+"\n", args);
    }

    private static void printWarning(String format, Object... args) {
        printWithPrefix("warning", format, args);
    }

    private static void printErrorAndExit(String format, Object... args) {
        printWithPrefix("error", format, args);
        System.exit(1);
    }

    // utilities: other

    private static List<File> toList(File[] array) {
        List<File> list = new LinkedList<File>();
        for (File file : array) list.add(file);
        return list;
    }

    private static File[] listFiles(File[] all, FileFilter filter) {
        List<File> list = new LinkedList<File>();
        for (File file : all) {
            if (filter.accept(file)) list.add(file);
        }
        return list.toArray(new File[]{});
    }

    private static void assertAndExit(boolean condition, String format, Object... args) {
        if (!condition) printErrorAndExit(format, args);
    }

    //
    // MAIN
    //

    public static void main(String[] argv) throws Exception {
        // parse command-line
        CommandLineParser parser = new GnuParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(OPTIONS, argv);
        }
        catch (ParseException pe) {
            System.err.println(pe);
            printUsageAndExit();
        }
        if (cl == null) {
            printUsageAndExit();
        }

        // handle options
        ImageMerger merger = new ImageMerger();
        List<File> inputDirs = new LinkedList<File>();
        if (cl.hasOption(O_INPUT)) {
            for (String dirname : cl.getOptionValue(O_INPUT).split("[;,]")) {
                File dir = new File(dirname);
                assertAndExit(dir.exists(), "directory %s doesn't exist", dir.toString());
                assertAndExit(dir.isDirectory(), "%s is not a directory", dir.toString());
                inputDirs.add(dir);
            }
        }
        if (cl.hasOption(O_OUTPUT)) {
            File dir = new File(cl.getOptionValue(O_OUTPUT));
            assertAndExit(dir.exists(), "directory %s doesn't exist", dir.toString());
            assertAndExit(dir.isDirectory(), "%s is not a directory", dir.toString());
            merger.setOutputDirectory(dir);
        }
        if (cl.hasOption(O_CSS_PATH) && cl.hasOption(O_CSS_FILENAME)) {
            merger.setCssPath(cl.getOptionValue(O_CSS_PATH));
            merger.setCssFilename(cl.getOptionValue(O_CSS_FILENAME));
            String cacheFilename = cl.getOptionValue(O_CACHE_FILENAME);
            if (cacheFilename != null) {
                merger.setCacheFilename(cacheFilename);
            }
        }
        if (cl.hasOption(O_JS_FILENAME)) {
            merger.setJsFilename(cl.getOptionValue(O_JS_FILENAME));
        }
        if (cl.hasOption(O_COPY)) {
            merger.setCopyFiles(true);
        }
        if (cl.hasOption(O_NO_APPEND)) {
            merger.setAppendOutput(false);
        }
        if (cl.hasOption(O_VERBOSE)) {
            merger.setVerbose(true);
        }
        if (cl.hasOption(O_DISABLED_IMAGES)) {
            printWarning("option -%s is deprecated", O_DISABLED_IMAGES);
        }

        // process
        assertAndExit(inputDirs.size() > 0, "must specify input directories");
        merger.process(inputDirs);
    }

    private static void printUsageAndExit() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main [options]", OPTIONS);
        System.exit(1);
    }

    //
    // Classes
    //

    static class ImageEntry {
        // Data
        public String filename;
        public String iefilename;
        public DecodedImage image;
        public int x;
        public int y;
        public ImageLayout layout;
		public boolean isSpacer;
        // Constructors
        public ImageEntry(File dir, File file, DecodedImage image, ImageLayout layout, boolean isSpacer) {
            this.filename = dir.getName()+File.separator+file.getName();
            this.iefilename = this.filename;
            this.image = image;
            this.layout = layout;
			this.isSpacer = isSpacer;
        }
		public ImageEntry(File dir, File file, DecodedImage image, ImageLayout layout) {
			this(dir, file, image, layout, false);
		}

        // Object methods
        @Override
        public String toString() {
            return image.getName();
        }
    } // class ImageEntry

    abstract static class ImageFactory {
        // Public methods
        public abstract DecodedImage loadImage(File file) throws IOException;
        public abstract DecodedImage loadImage(File file, boolean allowMultipleFrames) throws IOException;
        public abstract AggregateImage createAggregateImage(ImageLayout layout);
    }

    static class GifImageFactory extends ImageFactory {
        // ImageFactory methods
        @Override
        public DecodedImage loadImage(File file) throws IOException {
            return loadImage(file, false);
        }
        @Override
        public DecodedImage loadImage(File file, boolean allowMultipleFrames) throws IOException {
            try {
                DecodedGifImage image = new DecodedGifImage(file.getAbsolutePath());
                image.load(allowMultipleFrames);
                return image;
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                return null;
            }
        }
        @Override
        public AggregateImage createAggregateImage(ImageLayout layout) {
            return new AggregateGifImage(layout);
        }
    }

    static class FullColorImageFactory extends ImageFactory {
        // ImageFactory methods
        @Override
        public DecodedImage loadImage(File file) throws IOException {
            return loadImage(file, false);
        }
        @Override
        public DecodedImage loadImage(File file, boolean allowMultipleFrames) throws IOException {
            try {
                DecodedFullColorImage image = new DecodedFullColorImage(file.getAbsolutePath());
                image.load();
                return image;
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                return null;
            }
        }
        @Override
        public AggregateImage createAggregateImage(ImageLayout layout) {
            return new AggregateFullColorImage(layout);
        }
    }

    abstract static class AggregateImage {

        //
        // Data
        //

        protected ImageLayout layout;
        protected Dimension size;
        protected List<ImageEntry> entries;

        //
        // Constructors
        //

        /**
         * @param layout The layout of this aggregate image.
         */
        public AggregateImage(ImageLayout layout) {
            this.layout = layout;
        }

        //
        // Public methods
        //

        /**
         * Clears state so that the aggregate image can be re-used for
         * additional images with the same layout.
         */
        public void reset() {
            size = new Dimension();
            entries = new LinkedList<ImageEntry>();
        };

        /**
         * <strong>Note:</strong>
         * Sub-classes are <em>required</em> to call <code>addSubImage</code> if
         * the image is accepted. This implementation of <code>acceptSubImage</code>
         * is here simply to provide common logic for accepting a sub-image.
         *
         * @param entry The image entry to accept or reject.
         * @return True if the image is accepted (and thus added to the image entries).
         */
        public boolean acceptSubImage(ImageEntry entry) {
            if (layout.equals(ImageLayout.HORIZONTAL)) {
                return size.height == 0 || entry.image.getHeight() == size.height;
            }
            if (layout.equals(ImageLayout.VERTICAL)) {
                return size.width == 0 || entry.image.getWidth() == size.width;
            }
            if (layout.equals(ImageLayout.TILE)) return false;
            return true;
        }

        /**
         * <strong>Note:</strong>
         * Sub-classes <em>must</em> implement this method to save the
         * specific image file type.
         *
         * @param file The destination file for output.
         *
         * @throws IOException Thrown if image cannot be saved for any reason.
         */
        public abstract void saveImage(File file) throws IOException;

        public List<ImageEntry> entries() {
            return entries;
        }

        //
        // Protected methods
        //

        /**
         * Adds a sub-image to the total image at the next available slot
         * according to the aggregate image's layout.
         *
         * @param entry The sub-image entry to add.
         */
        protected void addSubImage(ImageEntry entry) {
            // TODO: For layout=NONE, devise a more compact way of
            // TODO: placing images instead of just using a vertical
            // TODO: layout.

            // add image
            ImageLayout layout = entry.layout;
            boolean isHorizontal = layout.equals(ImageLayout.HORIZONTAL);
            boolean isVertical   = !isHorizontal;

            entry.x = isHorizontal ? size.width  : 0;
            entry.y = isVertical   ? size.height : 0;
            entries.add(entry);

            // adjust total image size
            DecodedImage image = entry.image;
            int w = image.getWidth();
            int h = image.getHeight();

            if (isVertical   && size.width  < w) size.width  = w;
            if (isHorizontal && size.height < h) size.height = h;

            int dx = isHorizontal ? w : 0;
            int dy = isVertical   ? h : 0;
            size.width  += dx;
            size.height += dy;
        }

    } // class AggregateImage

    static class AggregateGifImage extends AggregateImage {

        // Data
        private Set<Integer> colors;

        // Constructors
        public AggregateGifImage(ImageLayout layout) {
            super(layout);
        }

        // AggregateImage methods
        @Override
        public void reset() {
            super.reset();
            colors = new HashSet<Integer>(256);
        }

        @Override
        public boolean acceptSubImage(ImageEntry entry) {
            if (!super.acceptSubImage(entry)) return false;

            DecodedGifImage gif = (DecodedGifImage)entry.image;

            // add this image's colors to color table
            // NOTE: We have to *copy* the color table because we mark ones
            // NOTE: that are in-common and don't want to change and image's
            // NOTE: original color table.
            int[] argbs = copy(gif.getUniqueColorTable());
            for (int i = 0; i < argbs.length; i++) {
                int argb = argbs[i];
                if ((argb & 0x0FF000000) == 0 || colors.contains(argb)) {
                    argbs[i] = -1;
                    continue;
                }
                colors.add(argb);
            }

            // skip image if too many colors
            // NOTE: Checking to see if the number of colors goes over
            // NOTE: 255/256 actually *still* fails on encoding because
            // NOTE: of "> 256 colors" error. So we chop off a few
            // NOTE: colors and that seems to work fine.
            if (colors.size() > 254) {
                for (int rgb : argbs) {
                    if (rgb != -1) colors.remove(rgb);
                }
                return false;
            }

            // accept image
            addSubImage(entry);
            return true;
        }

        static String setAsHexString(Set<Integer> numbers) {
            StringBuilder str = new StringBuilder("[ ");
            for (int number : numbers) {
                str.append("0x");
                str.append(Integer.toHexString(number).toUpperCase());
                str.append(' ');
            }
            str.append("]");
            return str.toString();
        }

        @Override
        public void saveImage(File file) throws IOException {
            OutputStream out = null;
            try {
                // build color table
                Color[] colorArray = new Color[1 + colors.size()];
                colorArray[0] = makeUniqueTransparentColor();
                Map<Integer,Integer> colorMap = new HashMap<Integer,Integer>();
                Iterator<Integer> iterator = colors.iterator();
                for (int i = 1; iterator.hasNext(); i++) {
                    int argb = iterator.next();
                    colorArray[i] = new Color(argb);
                    colorMap.put(argb, i);
                }

                // generate image
                byte[] pixels = new byte[size.width * size.height];
                for (ImageEntry entry : entries()) {
                    BufferedImage subImage = entry.image.getBufferedImage();
                    int width = entry.image.getWidth();
                    int height = entry.image.getHeight();
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int argb = subImage.getRGB(x, y);
                            int index = colorMap.get(argb) != null ? colorMap.get(argb) : 0;
                            pixels[(entry.y + y) * size.width + entry.x + x] = (byte)index;
                        }
                    }
                    subImage.flush();
                }

                // create encoder
                Gif89Encoder encoder = new Gif89Encoder(colorArray, size.width, size.height, pixels);
                encoder.setTransparentIndex(0);

                // save image file
                out = new FileOutputStream(file);
                encoder.encode(out);
            }
            finally {
                try {
                    out.close();
                }
                catch (Exception e) {
                    // ignore
                }
            }
        }

        // Protected methods
        protected  Color makeUniqueTransparentColor() {
            int[] rgb = { 255, 255, 255 };
            int index = 0;
            do {
                int argb = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
                if (!this.colors.contains(argb)) return new Color(argb, true);
                if (--rgb[index] == 0) index++;
            } while (index < rgb.length);
            return null;
        }

        protected static int[] copy(int[] source) {
            if (source == null || source.length == 0) return source;
            int[] dest = new int[source.length];
            System.arraycopy(source, 0, dest, 0, source.length);
            return dest;
        }


    } // class AggregateGifImage

    static class AggregateFullColorImage extends AggregateImage {

        // Constructors
        public AggregateFullColorImage(ImageLayout layout) {
            super(layout);
        }

        // AggregateImage methods
        @Override
        public boolean acceptSubImage(ImageEntry entry) {
            if (!super.acceptSubImage(entry)) return false;
            addSubImage(entry);
            return true;
        }

        @Override
        public void saveImage(File file) throws IOException {
            String type = file.getName().replaceAll("^.*\\.","");
            Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix(type);
            ImageWriter writer = iter.next();
            BufferedImage image = null;
            try {
                writer.setOutput(new FileImageOutputStream(file));
                writer.write(image = generateImage());
            }
            finally {
                try {
                    image.flush();
                }
                catch (Exception e) {
                    // ignore
                }
                try {
                    writer.dispose();
                }
                catch (Exception e) {
                    // ignore
                }
            }
        }

        // Protected methods

        /**
         * @return The aggregated image comprised of the sub-images.
         */
        protected BufferedImage generateImage() {
            BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = image.getGraphics();
            for (ImageEntry entry : entries) {
                BufferedImage subImage = entry.image.getBufferedImage();
                graphics.drawImage(subImage, entry.x, entry.y, null);
                subImage.flush();
            }
            graphics.dispose();
            return image;
        }

    } // class AggregateFullColorImage

    // file filters

    static class ExtensionFileFilter implements FileFilter {
        private String[] exts;
        public ExtensionFileFilter(String... exts) {
            this.exts = exts;
        }
	    @Override
        public boolean accept(File file) {
            String name = file.getName().toLowerCase();
            for (String ext : exts) {
                if (name.endsWith(ext)) return true;
            }
		    return false;
	    }
    } // class ExtensionFileFilter

    static class SubExtensionFileFilter implements FileFilter {
        private String subext;
        public SubExtensionFileFilter(String subext) {
            this.subext = subext;
        }
        @Override
        public boolean accept(File file) {
            String name = file.getName().toLowerCase();
            int index = name.lastIndexOf('.');
            if (index != -1) name = name.substring(0, index);
            return name.endsWith(subext);
        }
    } // class SubExtensionFileFilter

    static class NotFileFilter implements FileFilter {
        private FileFilter filter;
        public NotFileFilter(FileFilter filter) {
            this.filter = filter;
        }
        @Override
        public boolean accept(File file) {
            return !filter.accept(file);
        }
    } // class NotFileFilter

    static class AndFileFilter implements FileFilter {
        private FileFilter[] filters;
        public AndFileFilter(FileFilter... filters) {
            this.filters = filters;
        }
        @Override
        public boolean accept(File file) {
            for (FileFilter filter : filters) {
                if (!filter.accept(file)) return false;
            }
            return true;
        }
    } // class AndFileFilter

} // class ImageMerger