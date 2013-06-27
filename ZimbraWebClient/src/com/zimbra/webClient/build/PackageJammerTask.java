/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010, 2012 VMware, Inc.
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
    private static final Pattern RE_REQUIRE_OBJ = Pattern.compile("^AjxPackage\\.require\\((\\s*\\{\\s*name\\s*:\")?([^'\"]+)['\"](.*?)\\);?");

    private static final String OUTPUT_JS = "js";
    private static final String OUTPUT_HTML = "html";
	private static final String OUTPUT_ALL = "all";
    private static final String OUTPUT_APPCACHE = "appcache";

    //
    // Data
    //

    // attributes
    private File destFile;
	private File jsFile;
	private File htmlFile;
    private File acFile;
	private List<Source> sources = new LinkedList<Source>();
	private File dependsFile;
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

	private boolean isJs = true;
	private boolean isHtml = false;
	private boolean isAll = false;
    private boolean isAppCache = false;

	//
    // Public methods
    //

    // attributes

    public void setDestFile(File file) {
        this.destFile = file;
    }

	public void setJsDestFile(File file) {
		this.jsFile = file;
	}

	public void setHtmlDestFile(File file) {
		this.htmlFile = file;
	}

    public void setAppCacheDestFile(File file) {
        this.acFile = file;
    }

	public void setJsDir(File dir) {
		Source source = new Source();
		source.setDir(dir);

		this.sources.clear();
		this.sources.add(source);
	}

	public void setDependsFile(File file) {
		this.dependsFile = file;
	}

	public void setOutput(String output) {
		this.output = output;
		this.isAll = OUTPUT_ALL.equals(output);
		this.isHtml = this.isAll || OUTPUT_HTML.equals(output);
		this.isJs = this.isAll || OUTPUT_JS.equals(output) || !this.isHtml;
        this.isAppCache = this.isAll || OUTPUT_APPCACHE.equals(output);
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

	public Source createSrc() {
		Source source = new Source();
		this.sources.add(source);
		return source;
	}

    //
    // Task methods
    //

    public void execute() throws BuildException {
        this.depth = "";
        this.defines = new HashMap<String,Boolean>();

		PrintWriter jsOut = null;
		PrintWriter htmlOut = null;
		PrintWriter dependsOut = null;
        PrintWriter appCacheOut = null;
		try {
			if (this.isJs) {
				File file = this.jsFile != null ? this.jsFile : this.destFile;
				log("Jamming to ",file.toString());
				jsOut = new PrintWriter(new FileWriter(file));
			}
			if (this.isHtml) {
				File file = this.htmlFile != null ? this.htmlFile : this.destFile;
				log("Jamming to ",file.toString());
				htmlOut = new PrintWriter(new FileWriter(file));
			}
			if (this.isAppCache) {
				File file = this.acFile != null ? this.acFile : this.destFile;
				log("Creating App cache for ",file.toString());
				appCacheOut = new PrintWriter(new FileWriter(file));
			}

			if (this.dependsFile != null) {
				log("Dependencies saved to "+this.dependsFile);
				dependsOut = new PrintWriter(new FileWriter(this.dependsFile));
			}

            if (this.prefix != null) {
				PrintWriter out = OUTPUT_JS.equals(this.prefix.output) ? jsOut : htmlOut;
				if (out != null) {
					out.println(this.prefix.toString());
				}
            }

            List<String> packages = new LinkedList<String>();
            for (JammerFiles files : this.files) {
                boolean wrap = files.isWrapped();
				boolean isManifest = files.isManifest();
				File dir = files.getDir(this.getProject());
                for (String filename : files.getFiles(this.getProject())) {
                    File file = new File(dir, filename);
                    String pkg = path2package(stripExt(filename).replace(File.separatorChar, '/'));
                    packages.add(pkg);
                    if (this.isHtml && !isManifest) {
						printHTML(htmlOut, pkg, files.getBasePath(), files.getExtension());
                    }
					jamFile(jsOut, htmlOut, file, pkg, packages, wrap, true, dependsOut,appCacheOut);
                }
            }

            if (this.isHtml && packages.size() > 0 && htmlOut != null) {
                htmlOut.println("<script type=\"text/javascript\">");
                for (String pkg : packages) {
                    htmlOut.print("AjxPackage.define(\"");
                    htmlOut.print(pkg);
                    htmlOut.println("\");");
                }
                htmlOut.println("</script>");
            }
            if (this.suffix != null) {
				PrintWriter out = OUTPUT_JS.equals(this.prefix.output) ? jsOut : htmlOut;
				if (out != null) {
					out.println(this.suffix.toString());
				}
			}
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
        finally {
			if (jsOut != null) jsOut.close();
			if (htmlOut != null) htmlOut.close();
			if (appCacheOut != null) appCacheOut.close();
			if (dependsOut != null) dependsOut.close();
		}
    }

    //
    // Private methods
    //

    private void jamFile(PrintWriter jsOut, PrintWriter htmlOut, File ifile,
                         String pkg, List<String> packages,
                         boolean wrap, boolean top, PrintWriter dependsOut, PrintWriter appCacheOut)
    throws IOException {
        if (this.verbose) log("file: ",ifile.toString());
        BufferedReader in = new BufferedReader(new FileReader(ifile));
        boolean isJS = ifile.getName().endsWith(".js");

        // "wrap" source
        if (this.isJs && isJS && pkg != null && wrap && jsOut != null) {
            jsOut.print("if (AjxPackage.define(\"");
            jsOut.print(pkg);
            jsOut.println("\")) {");
        }

		// remember this file
		if (dependsOut != null) {
			dependsOut.println(ifile.getCanonicalPath());
			dependsOut.flush();
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
                    if (this.isHtml && !path.endsWith("__all__")) {
                        printHTML(htmlOut, require, null, null);
                    }

                    if (this.isAppCache && !path.endsWith("__all__")) {
                        printAppCache(appCacheOut, require, null, null);
                    }

                    // implicitly define and jam on!
                    this.defines.put(path, true);
                    File file = this.getFileForPath(path);
                    String odepth = this.verbose ? this.depth : null;
                    if (this.verbose) this.depth += "  ";
                    jamFile(jsOut, htmlOut, file, path2package(require), packages, wrap, false, dependsOut,appCacheOut);
                    if (this.verbose) this.depth = odepth;
                }
                continue;
            }

            // leave line intact
            if (this.isJs && isJS && jsOut != null) {
                jsOut.println(line);
            }
        }

        if (this.isJs && isJS && pkg != null && wrap && jsOut != null) {
            jsOut.println("}");
        }

        in.close();
    }

	private File getFileForPath(String path) {
		String name = path.replace('/',File.separatorChar)+".js";
		File file = null;
		for (Source source : this.sources) {
			String filename = name;
			if (source.prefix != null && name.startsWith(source.prefix+"/")) {
				filename = name.substring(source.prefix.length() + 1);
			}
			file = new File(source.dir, filename);
			if (file.exists()) {
				break;
			}
		}
		return file;
	}

    private void printHTML(PrintWriter out, String pkg, String basePath, String extension) {
		if (out == null) return;

		String path = package2path(pkg);
        out.print("<script type=\"text/javascript\" src=\"");
        out.print(basePath != null ? basePath : this.basepath);
        out.print(path);
        out.print(extension != null ? extension : this.extension);
        out.println("\"></script>");
    }

    private void printAppCache(PrintWriter out, String pkg, String basePath, String extension) {
        if (out == null) return;

        String path = package2path(pkg);
        out.print(basePath != null ? basePath : this.basepath);
        out.print(path);
        out.println(extension != null ? extension : this.extension);
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
        if (m.matches()){
            return m.group(1);
        }

        m = RE_REQUIRE_OBJ.matcher(s);

        if (m.matches()){
            return m.group(2);
        }
        return null;

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
		public String getBasePath();
		public String getExtension();
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
		private String basePath;
		private String extension;

        //
        // Public methods
        //

        public void setWrap(boolean wrap) {
            this.wrap = wrap;
        }
        public boolean isWrapped() {
            return this.wrap;
        }

		public void setManifest(boolean manifest) {
			this.manifest = manifest;
		}
		public boolean isManifest() {
			return this.manifest;
		}

		public void setBasePath(String basePath) {
			this.basePath = basePath;
		}
		public String getBasePath() {
			return this.basePath;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}
		public String getExtension() {
			return this.extension;
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
		private String basePath;
		private String extension;

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
			return this.manifest;
		}

		public void setBasePath(String basePath) {
			this.basePath = basePath;
		}
		public String getBasePath() {
			return this.basePath;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}
		public String getExtension() {
			return this.extension;
		}

    } // class JammerFileList

    public static class Text {
		public String output = PackageJammerTask.OUTPUT_JS;
		private StringBuilder str = new StringBuilder();
		public void setOutput(String output) {
			this.output = output;
		}
		public void addText(String s) {
            str.append(s);
        }
        public String toString() { return str.toString(); }
    }

	public class Source {
		public File dir;
		public String prefix;

		public void setDir(File dir) {
			this.dir = dir;
		}
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
	}

} // class PackageJammerTask
