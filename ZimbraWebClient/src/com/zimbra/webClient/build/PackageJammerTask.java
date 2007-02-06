/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is: Zimbra Collaboration Suite Web Client
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.webClient.build;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class PackageJammerTask
extends Task {

    //
    // Constants
    //

    private static final Pattern RE_DEFINE = Pattern.compile("^AjxPackage\\.define\\(['\"]([^'\"]+)['\"]\\);?");
    private static final Pattern RE_UNDEFINE = Pattern.compile("^AjxPackage\\.undefine\\(['\"]([^'\"]+)['\"]\\);?");
    private static final Pattern RE_REQUIRE = Pattern.compile("^AjxPackage\\.require\\(['\"]([^'\"]+)['\"](.*?)\\);?");

    private static final String OUTPUT_JS = "js";
    private static final String OUTPUT_HTML = "html";

    //
    // Data
    //

    // attributes
    private File destFile;
    private File jsDir;
    private String output = OUTPUT_JS;
    private String basepath = "";
    private String extension = ".js";
    private boolean verbose = false;

    // children
    private Text prefix;
    private Text suffix;
    private List<JammerFiles> files = new LinkedList<JammerFiles>();

    // internal state
    private String depth;
    private Map<String,Boolean> defines;

    //
    // Public methods
    //

    // attributes

    public void setDestFile(File file) {
        this.destFile = file;
    }

    public void setJsDir(File dir) {
        this.jsDir = dir;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setBasePath(String basepath) {
        this.basepath = basepath;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    // children

    public Text createPrefix() {
        return this.prefix = new Text();
    }
    public Text createSuffix() {
        return this.suffix = new Text();
    }

    public FileList createFileList() {
        JammerFileList fileList = new JammerFileList();
        this.files.add(fileList);
        return fileList;
    }
    public FileSet createFileSet() {
        JammerFileSet fileSet = new JammerFileSet();
        this.files.add(fileSet);
        return fileSet;
    }

    //
    // Task methods
    //

    public void execute() throws BuildException {
        this.depth = "";
        this.defines = new HashMap<String,Boolean>();

        PrintWriter out = null;
        try {
            log("Jamming to ",this.destFile.toString());
            out = new PrintWriter(new FileWriter(this.destFile));

            boolean genHTML = this.output.equals(OUTPUT_HTML);
            if (this.prefix != null) {
                out.println(this.prefix.toString());
            }

            List<String> packages = new LinkedList<String>();
            for (JammerFiles files : this.files) {
                boolean wrap = files.isWrapped();
                boolean manifest = files.isManifest();
                File dir = files.getDir(this.getProject());
                for (String filename : files.getFiles(this.getProject())) {
                    File file = new File(dir, filename);
                    String pkg = path2package(stripExt(filename).replace(File.separatorChar, '/'));
                    packages.add(pkg);
                    if (genHTML && !manifest) {
                        printHTML(out, pkg);
                    }
                    else {
                        jamFile(out, file, pkg, packages, wrap, true);
                    }
                }
            }

            if (genHTML && packages.size() > 0) {
                out.println("<script type=\"text/javascript\">");
                for (String pkg : packages) {
                    out.print("AjxPackage.define(\"");
                    out.print(pkg);
                    out.println("\");");
                }
                out.println("</script>");
            }
            if (this.suffix != null) {
                out.println(this.suffix.toString());
            }
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }

    //
    // Private methods
    //

    private void jamFile(PrintWriter out, File ifile,
                         String pkg, List<String> packages,
                         boolean wrap, boolean top)
    throws IOException {
        if (this.verbose) log("file: ",ifile.toString());
        BufferedReader in = new BufferedReader(new FileReader(ifile));
        boolean genJS = this.output.equals(OUTPUT_JS);
        boolean genHTML = this.output.equals(OUTPUT_HTML);
        boolean isJS = ifile.getName().endsWith(".js");

        // "wrap" source
        if (genJS && isJS && pkg != null && wrap) {
            out.print("if (AjxPackage.define(\"");
            out.print(pkg);
            out.println("\")) {");
        }

        // read file
        String line;
        while ((line = in.readLine()) != null) {
            // define package
            String define = matchDefine(line);
            if (define != null) {
                if (this.verbose) log("define ", define);
                this.defines.put(package2path(define), true);
                continue;
            }

            // undefine package
            String undefine = matchUndefine(line);
            if (undefine != null) {
                if (this.verbose) log("undefine ", undefine);
                this.defines.remove(package2path(undefine));
                continue;
            }

            // require package
            String require = matchRequire(line);
            if (require != null) {
                if (this.verbose) log("require ", require);
                String path = package2path(require);
                if (this.defines.get(path) == null) {
                    packages.add(require);

                    // output script tag
                    if (genHTML && !path.endsWith("__all__")) {
                        printHTML(out, require);
                    }

                    // implicitly define and jam on! 
                    this.defines.put(path, true);
                    File file = new File(this.jsDir, path.replace('/',File.separatorChar)+".js");
                    String odepth = this.verbose ? this.depth : null;
                    if (this.verbose) this.depth += "  ";
                    jamFile(out, file, path2package(require), packages, wrap, false);
                    if (this.verbose) this.depth = odepth;
                }
                continue;
            }

            // leave line intact
            if (genJS && isJS) {
                out.println(line);
            }
        }

        if (genJS && isJS && pkg != null && wrap) {
            out.println("}");
        }

        in.close();
    }

    private void printHTML(PrintWriter out, String pkg) {
        String path = package2path(pkg);
        out.print("<script type=\"text/javascript\" src=\"");
        out.print(this.basepath);
        out.print(path);
        out.print(this.extension);
        out.println("\"></script>");
    }

    private String matchDefine(String s) {
        Matcher m = RE_DEFINE.matcher(s);
        return m.matches() ? m.group(1) : null;
    }

    private String matchUndefine(String s) {
        Matcher m = RE_UNDEFINE.matcher(s);
        return m.matches() ? m.group(1) : null;
    }

    private String matchRequire(String s) {
        Matcher m = RE_REQUIRE.matcher(s);
        return m.matches() ? m.group(1) : null;
    }

    private void log(String... ss) {
        System.out.print(this.depth);
        for (String s : ss) {
            System.out.print(s);
        }
        System.out.println();
    }

    //
    // Private functions
    //

    private static String package2path(String pkg) {
        return pkg.replace('.', '/').replaceAll("\\*$", "__all__");
    }

    private static String path2package(String path) {
        return path.replace('/', '.').replaceAll("\\*$", "__all__");
    }

    private static String stripExt(String fname) {
        return fname.replaceAll("\\..+$", "");
    }

    //
    // Classes
    //

    private static interface JammerFiles {
        public boolean isWrapped();
        public boolean isManifest();
        public File getDir(Project project);
        public String[] getFiles(Project project);
    }

    public static class JammerFileList
    extends FileList
    implements JammerFiles {

        //
        // Data
        //

        private boolean wrap = true;
        private boolean manifest = true;

        //
        // Public methods
        //

        public void setWrap(boolean wrap) {
            this.wrap = wrap;
        }

        public boolean isWrapped() {
            return wrap;
        }

        public void setManifest(boolean manifest) {
            this.manifest = manifest;
        }

        public boolean isManifest() {
            return manifest;
        }

    } // class JammerFileList

    public static class JammerFileSet
    extends FileSet
    implements JammerFiles {

        //
        // Data
        //

        private boolean wrap = true;
        private boolean manifest = true;

        //
        // Public methods
        //

        public String[] getFiles(Project project) {
            return this.getDirectoryScanner(project).getIncludedFiles();
        }

        public void setWrap(boolean wrap) {
            this.wrap = wrap;
        }

        public boolean isWrapped() {
            return wrap;
        }

        public void setManifest(boolean manifest) {
            this.manifest = manifest;
        }

        public boolean isManifest() {
            return manifest;
        }

    } // class JammerFileList

    public static class Text {
        private StringBuilder str = new StringBuilder();
        public void addText(String s) {
            str.append(s);
        }
        public String toString() { return str.toString(); }
    }

} // class PackageJammerTask
