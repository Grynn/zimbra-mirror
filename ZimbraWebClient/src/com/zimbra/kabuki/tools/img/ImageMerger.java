package com.zimbra.kabuki.tools.img;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import org.apache.commons.cli.*;
import net.jmge.gif.Gif89Encoder;

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
    private static final FileFilter F_HORIZONTAL = new SubExtensionFileFilter(Layout.HORIZONTAL.toExtension());
    private static final FileFilter F_VERTICAL = new SubExtensionFileFilter(Layout.VERTICAL.toExtension());
    private static final FileFilter F_TILE = new SubExtensionFileFilter(Layout.TILE.toExtension());
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
                    System.out.println("ignoring: "+dir);
                    continue;
                }

                // only copy files
                if (copyFiles || new File(dir, "_nomerge.flag").exists()) {
                    System.out.println("copying: "+dir);
                    processCopy(dir);
                    continue;
                }

                // merge
                System.out.println("processing: "+dir);
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
        processMerge(dir, dir.listFiles(F_GIF), IF_GIF);
        processMerge(dir, dir.listFiles(F_JPG), IF_JPG);
        processMerge(dir, dir.listFiles(F_PNG), IF_PNG);
    }

    private void processMerge(File dir, File[] files, ImageFactory factory) throws IOException {
        if (files.length == 0) return;
        processMerge(dir, listFiles(files, F_NONE), factory, Layout.NONE);
        processMerge(dir, listFiles(files, F_HORIZONTAL), factory, Layout.HORIZONTAL);
        processMerge(dir, listFiles(files, F_VERTICAL), factory, Layout.VERTICAL);
        processCopy(dir, dir.listFiles(F_TILE), factory);
    }

    private void processMerge(File dir, File[] files, ImageFactory factory,
                              Layout layout) throws IOException {
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
                System.out.println("generating file: "+file);
                aggregate.saveImage(file);
                for (ImageEntry entry : subEntries) {
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

    } // processMerge(File,File[],ImageFactory,Layout)

    private void printlnCss(ImageEntry entry) {
        if (cssOut == null) return;
        String selector = toSelector(entry.image.getName());
        String url = cssPath+"/"+entry.filename+"?v=@jsVersion@";
        print(cssOut, "%s {", selector);
        boolean isPng = entry.filename.toLowerCase().endsWith(".png");
        if (isPng) {
            println(cssOut);
            println(cssOut, "#IFDEF MSIE");
            println(cssOut, "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='%s',sizingMethod='image');", url);
            println(cssOut, "#ELSE");
            println(cssOut, "background:url('%s') %dpx %dpx %s;", url, negative(entry.x), negative(entry.y), entry.layout.toCss());
            println(cssOut, "#END");
        }
        else {
            print(cssOut, "background:url('%s') %dpx %dpx %s;", url, negative(entry.x), negative(entry.y), entry.layout.toCss());
            print(cssOut, "overflow:hidden;");
        }
        if (!entry.layout.equals(Layout.TILE)) {
            if (!entry.layout.equals(Layout.HORIZONTAL)) {
                print(cssOut, "width:%dpx !important;", entry.image.getWidth());
            }
            if (!entry.layout.equals(Layout.VERTICAL)) {
                print(cssOut, "height:%dpx !important;", entry.image.getHeight());
            }
        }
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
                "AjxImgData[\"%s\"]={t:%d,l:%d,w:%d,h:%d,f:\"%s/%s\"};",
                name, -entry.y, -entry.x,
                entry.image.getWidth(), entry.image.getHeight(),
                cssPath, entry.filename
            );
        }
    }

    private void printlnCache(ImageEntry entry) {
        println(cacheOut, "<img alt=\"\" src='%s/%s?v=@jsVersion@'>", cssPath, entry.filename);
    }

    private void processCopy(File dir) throws IOException {
        processCopy(dir, dir.listFiles(F_GIF), IF_GIF);
        processCopy(dir, dir.listFiles(F_JPG), IF_JPG);
        processCopy(dir, dir.listFiles(F_PNG), IF_PNG);
    }
    private void processCopy(File dir, File[] files, ImageFactory factory) throws IOException {
        if (files.length == 0) return;

        for (File file : files) {
            DecodedImage image = factory.loadImage(file);
            if (image == null) {
                System.err.println("error: unable to load image "+file);
                continue;
            }
            ImageEntry entry = new ImageEntry(dir, file, image, Layout.fromFile(file));
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
        return (File[])list.toArray(new File[]{});
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
    // Enums
    //

    static enum Layout {
        // Values
        NONE("no-repeat", ""), TILE("repeat", ".repeat"),
        HORIZONTAL("repeat-x", ".repeatx"), VERTICAL("repeat-y", ".repeaty");
        // Data
        private String css;
        private String ext;
        // Constructors
        Layout(String css, String ext) {
            this.css = css;
            this.ext = ext;
        }
        // Public methods
        public String toCss() {
            return css;
        }
        public String toExtension() {
            return ext;
        }
        public static Layout fromFile(File file) {
            String name = file.getName().toLowerCase();
            for (Layout layout : Layout.values()) {
                if (layout.equals(Layout.NONE)) continue;
                if (name.contains(layout.toExtension())) {
                    return layout;
                }
            }
            return Layout.NONE;
        }
    }

    //
    // Classes
    //

    static class ImageEntry {
        // Data
        public String filename;
        public DecodedImage image;
        public int x;
        public int y;
        public Layout layout;
        // Constructors
        public ImageEntry(File dir, File file, DecodedImage image, Layout layout) {
            this.filename = dir.getName()+"/"+file.getName();
            this.image = image;
            this.layout = layout;
        }
    } // class ImageEntry

    abstract static class ImageFactory {
        // Public methods
        public abstract DecodedImage loadImage(File file) throws IOException;
        public abstract AggregateImage createAggregateImage(Layout layout);
    }

    static class GifImageFactory extends ImageFactory {
        // ImageFactory methods
        public DecodedImage loadImage(File file) throws IOException {
            try {
                DecodedGifImage image = new DecodedGifImage(file.getAbsolutePath(), null, 0);
                image.load();
                return image;
            }
            catch (Exception e) {
                return null;
            }
        }
        public AggregateImage createAggregateImage(Layout layout) {
            return new AggregateGifImage(layout);
        }
    }

    static class FullColorImageFactory extends ImageFactory {
        // ImageFactory methods
        public DecodedImage loadImage(File file) throws IOException {
            try {
                String name = file.getName();
                int index = name.lastIndexOf('.');
                String suffix = index != -1 ? name.substring(index + 1) : "";
                DecodedFullColorImage image = new DecodedFullColorImage(file.getAbsolutePath(), suffix, null, 0);
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
        public AggregateImage createAggregateImage(Layout layout) {
            return new AggregateFullColorImage(layout);
        }
    }

    abstract static class AggregateImage {

        //
        // Data
        //
        
        protected Layout layout;
        protected Dimension size;
        protected List<ImageEntry> entries;

        //
        // Constructors
        //

        /**
         * @param layout The layout of this aggregate image.
         */
        public AggregateImage(Layout layout) {
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
            if (layout.equals(Layout.HORIZONTAL)) {
                return size.height == 0 || entry.image.getHeight() == size.height;
            }
            if (layout.equals(Layout.VERTICAL)) {
                return size.width == 0 || entry.image.getWidth() == size.width;
            }
            if (layout.equals(Layout.TILE)) return false;
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
            Layout layout = entry.layout;
            boolean isHorizontal = layout.equals(Layout.HORIZONTAL);
            boolean isVertical   = !isHorizontal;

            entry.x = isHorizontal ? size.width  : 0;
            entry.y = isVertical   ? size.height : 0;
            entries.add(entry);

            // adjust total image size
            DecodedImage image = entry.image;
            int w = image.getWidth();
            int h = image.getHeight();

            if (size.width  < w) size.width  = w;
            if (size.height < h) size.height = h;

            int dx = isHorizontal ? w : 0;
            int dy = isVertical   ? h : 0;
            size.width  += dx;
            size.height += dy;
        }

        /**
         * @return The aggregated image comprised of the sub-images. 
         */
        protected BufferedImage generateImage() {
            int count = entries.size();
            BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = image.getGraphics();
            for (ImageEntry entry : entries) {
                graphics.drawImage(entry.image.getBufferedImage(), entry.x, entry.y, null);
            }
            return image;
        }

    } // class AggregateImage

    static class AggregateGifImage extends AggregateImage {

        // Data
        private Set<Integer> colors;

        // Constructors
        public AggregateGifImage(Layout layout) {
            super(layout);
        }

        // AggregateImage methods
        public void reset() {
            super.reset();
            colors = new HashSet<Integer>(256);
        }

        public boolean acceptSubImage(ImageEntry entry) {
            if (!super.acceptSubImage(entry)) return false;

            DecodedGifImage gif = (DecodedGifImage)entry.image;

            // add this image's colors to color table
            int[] rgbs = gif.getUniqueColorTable();
            for (int i = 1; i < rgbs.length; i++) {
                int rgb = rgbs[i];
                if (colors.contains(rgb)) {
                    rgbs[i] = -1;
                    continue;
                }
                colors.add(rgb);
            }

            // skip image if too many colors
            // NOTE: Checking to see if the number of colors goes over
            // NOTE: 255/256 actually *still* fails on encoding because
            // NOTE: of "> 256 colors" error. So we chop off a few
            // NOTE: colors and that seems to work fine.
            if (colors.size() > 254) {
                for (int rgb : rgbs) {
                    if (rgb != -1) colors.remove(rgb);
                }
                return false;
            }

            // accept image
            addSubImage(entry);
            return true;
        }

        public void saveImage(File file) throws IOException {
            OutputStream out = new FileOutputStream(file);
            Gif89Encoder encoder = new Gif89Encoder(generateImage());
            encoder.encode(out);
            out.close();
        }

    } // class AggregateGifImage

    static class AggregateFullColorImage extends AggregateImage {

        // Constructors
        public AggregateFullColorImage(Layout layout) {
            super(layout);
        }

        // AggregateImage methods
        public boolean acceptSubImage(ImageEntry entry) {
            if (!super.acceptSubImage(entry)) return false;
            addSubImage(entry);
            return true;
        }

        public void saveImage(File file) throws IOException {
            String type = file.getName().replaceAll("^.*\\.","");
            Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix(type);
            ImageWriter writer = iter.next();
            writer.setOutput(new FileImageOutputStream(file));
            writer.write(generateImage());
            writer.dispose();
        }

    } // class AggregateFullColorImage

    // file filters

    static class ExtensionFileFilter implements FileFilter {
        private String[] exts;
        public ExtensionFileFilter(String... exts) {
            this.exts = exts;
        }
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
        public boolean accept(File file) {
            return !filter.accept(file);
        }
    } // class NotFileFilter

    static class AndFileFilter implements FileFilter {
        private FileFilter[] filters;
        public AndFileFilter(FileFilter... filters) {
            this.filters = filters;
        }
        public boolean accept(File file) {
            for (FileFilter filter : filters) {
                if (!filter.accept(file)) return false;
            }
            return true;
        }
    } // class AndFileFilter

} // class ImageMerger