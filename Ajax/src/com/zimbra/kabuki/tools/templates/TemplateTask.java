package com.zimbra.kabuki.tools.templates;

import java.io.*;
import java.util.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;


public class TemplateTask
extends Task {

    //
    // Data
    //

    private File destDir;
    private String prefix = "";
    private boolean define = false;
    private List<FileSet> fileSets = new LinkedList<FileSet>();
	private boolean authoritative = false;


    //
    // Public methods
    //

    public void setDestDir(File dir) {
        this.destDir = dir;
    }

    public void setPrefix(String prefix) {
        if (prefix.length() > 0 && !prefix.matches("\\.$")) {
            prefix = prefix + ".";
        }
        this.prefix = prefix;
    }

    public void setDefine(boolean define) {
        this.define = define;
    }

    public void setAuthoritative(boolean authoritative) {
        this.authoritative = authoritative;
    }

    public void addFileSet(FileSet fileSet) {
        this.fileSets.add(fileSet);
    }

    //
    // Task methods
    //

    public void execute() throws BuildException {
        if (this.destDir != null) {
            System.out.println("Destination: "+this.destDir);
        }        
        Project project = this.getProject();
        for (FileSet fileSet : this.fileSets) {
            File idir = fileSet.getDir(project);
            File odir = this.destDir != null ? this.destDir : idir;
            DirectoryScanner scanner = fileSet.getDirectoryScanner(project);
            String[] filenames = scanner.getIncludedFiles();
	        try {
		        Template.convertFiles(idir, odir, prefix, filenames,
				                      authoritative, define);
	        } catch (IOException e) {
		        System.err.println("error: "+e.getMessage());
	        }
        }
    }

 
/*
>>>> ORIGINAL TemplateTask.java#10
        String[] keys = s.split("\\.");
        for (String key : keys) {
            out.print("[\"");
            printEscaped(out, key);
            out.print("\"]");
        }
        out.println(";");
    }

    private static void printBufferLine(PrintWriter out, String... ss) {
        out.print("\tbuffer[_i++] = ");
        for (String s : ss) {
            out.print(s);
        }
        out.println(";");
    }

    private static String stripExt(String s) {
        return s.replaceAll("\\.[^\\.]+$", "");
    }

    private static String path2package(String s) {
        return s.replace(File.separatorChar, '.');
    }

    private static void printEscaped(PrintWriter out, String s) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c == '"') {
                out.print('\\');
            }
            else if (c == '\n') {
                out.print("\\n");
                continue;
            }
            out.print(c);
        }
    }

    private static boolean upToDate(File ifile, File ofile) {
        if (ifile.exists() && ofile.exists()) {
            return ifile.lastModified() < ofile.lastModified();
        }
        return false;
    }

==== THEIRS TemplateTask.java#11
        Matcher part = RE_PARAM_PART.matcher(s.trim());
        while (part.find()) {
            String name = part.group(1).trim();
            String args = part.group(2);

            out.print("[\"");
            out.print(name);
            out.print("\"]");
            if (args != null) {
                out.print(args);
            }
        }
        out.println(";");
    }

    private static void printBufferLine(PrintWriter out, String... ss) {
        out.print("\tbuffer[_i++] = ");
        for (String s : ss) {
            out.print(s);
        }
        out.println(";");
    }

    private static String stripExt(String s) {
        return s.replaceAll("\\.[^\\.]+$", "");
    }

    private static String path2package(String s) {
        return s.replace(File.separatorChar, '.');
    }

    private static void printEscaped(PrintWriter out, String s) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c == '"') {
                out.print('\\');
            }
            else if (c == '\n') {
                out.print("\\n");
                continue;
            }
            out.print(c);
        }
    }

    private static boolean upToDate(File ifile, File ofile) {
        if (ifile.exists() && ofile.exists()) {
            return ifile.lastModified() < ofile.lastModified();
        }
        return false;
    }

==== YOURS TemplateTask.java
<<<<
*/
} // class TemplateTask
