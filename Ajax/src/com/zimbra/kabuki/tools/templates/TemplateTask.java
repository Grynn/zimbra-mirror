package com.zimbra.kabuki.tools.templates;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class TemplateTask
extends Task {

    //
    // Constants
    //

    private static final String S_PARAM = "\\$\\{(.+?)\\}";
    private static final String S_INLINE = "<\\$=(.+?)\\$>";
    private static final String S_CODE = "<\\$(.+?)\\$>";
    private static final String S_ALL = S_PARAM + "|" + S_INLINE + "|" + S_CODE;
    private static final String S_TEMPLATE = "<template(.*?)>(.*?)</template>";
    private static final String S_ATTR = "\\s*(\\S+)\\s*=\\s*('[^']*'|\"[^\"]*\")";
    private static final String S_WS_LINESEP = "\\s*\\n+\\s*";
    private static final String S_GT_LINESEP_LT = ">" + S_WS_LINESEP + "<";

    private static final Pattern RE_REPLACE = Pattern.compile(S_ALL, Pattern.DOTALL);
    private static final Pattern RE_TEMPLATE = Pattern.compile(S_TEMPLATE, Pattern.DOTALL);
    private static final Pattern RE_ATTR = Pattern.compile(S_ATTR, Pattern.DOTALL);

    private static final String A_XML_SPACE = "xml:space";
    private static final String V_XML_SPACE_PRESERVE = "preserve";

    //
    // Data
    //

    private File destDir;
    private String prefix = "";
    private boolean define = false;
    private boolean authoritative = false;
    private List<FileSet> fileSets = new LinkedList<FileSet>();

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
            for (String filename : filenames) {
                String path = stripExt(filename);
                String pkg = prefix + path2package(path);
                File ifile = new File(idir, filename);
                File ofile = new File(odir, path+".js");
                if (upToDate(ifile, ofile)) {
                    continue;
                }
                System.out.println("Compiling "+ifile);
                if (odir != idir) {
                    File pdir = ofile.getParentFile();
                    pdir.mkdirs();
                }
                try {
                    convert(ifile, ofile, pkg);
                }
                catch (IOException e) {
                    System.err.println("error: "+e.getMessage());
                }
            }
        }
    }

    //
    // Private methods
    //

    private void convert(File ifile, File ofile, String pkg) throws IOException {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new FileReader(ifile));
            out = new PrintWriter(new FileWriter(ofile));

            String lines = readLines(in);
            Matcher matcher = RE_TEMPLATE.matcher(lines);
            if (matcher.find()) {
                boolean first = true;
                do {
                    Map<String,String> attrs = parseAttrs(matcher.group(1));
                    String body = matcher.group(2);
                    String stripWsAttr = attrs.get(A_XML_SPACE);
                    if (stripWsAttr == null || !stripWsAttr.equals(V_XML_SPACE_PRESERVE)) {
                        body = body.replaceAll(S_GT_LINESEP_LT, "><").trim();
                    }
                    String packageId = pkg;
                    String templateId = attrs.get("id");
                    // NOTE: Template ids can be specified absolutely (i.e.
                    //       overriding the default package) if the id starts
                    //       with a forward slash (/), or if the id contains
                    //       a hash mark (#). This allows a template file to
                    //       override both types of template files (i.e. a
                    //       single template per file or multiple templates
                    //       per file).
                    if (templateId != null && (templateId.indexOf('#') != -1 || templateId.startsWith("/"))) {
                        if (templateId.indexOf('#') == -1) templateId += "#";
                        packageId = templateId.replaceAll("#.*$", "").replaceAll("^/","").replace('/','.');
                        templateId = templateId.replaceAll("^.*#", "");
                    }
                    String id = templateId != null && !templateId.equals("") ? packageId+"#"+templateId : packageId;
                    if (first && this.define) {
                        out.print("AjxPackage.define(\"");
                        out.print(packageId);
                        out.println("\");");
                    }
                    convertLines(out, id, body, attrs);
                    if (first) {
                        first = false;
                        out.print("AjxTemplate.register(\"");
                        out.print(packageId);
                        out.print("\", ");
                        out.print("AjxTemplate.getTemplate(\"");
                        out.print(id);
                        out.print("\"), ");
                        out.print("AjxTemplate.getParams(\"");
                        out.print(id);
                        out.println("\"));");
                    }
                    out.println();
                } while (matcher.find());
            }
            else {
                convertLines(out, pkg, lines, null);
            }
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception e) {
                    // ignore
                }
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private void convertLines(PrintWriter out, String pkg, String lines, Map<String,String> attrs) {
        out.print("AjxTemplate.register(\"");
        out.print(pkg);
        out.println("\", ");
        out.println("function(name, params, data, buffer) {");
        out.println("\tvar _hasBuffer = Boolean(buffer);");
        out.println("\tdata = (typeof data == \"string\" ? { id: data } : data) || {};");
        out.println("\tbuffer = buffer || [];");
        out.println("\tvar _i = buffer.length;");
        out.println();

        Matcher matcher = RE_REPLACE.matcher(lines);
        if (matcher.find()) {
            int offset = 0;
            do {
                int index = matcher.start();
                if (offset < index) {
                    printStringLines(out, lines.substring(offset, index));
                }
                String param = matcher.group(1);
                String inline = matcher.group(2);
                if (param != null) {
                    printDataLine(out, param);
                }
                else if (inline != null) {
                    printBufferLine(out, inline);
                }
                else {
                    printLine(out, "\t", matcher.group(3).replaceAll("\n", "\n\t"), "\n");
                }
                offset = matcher.end();
            } while (matcher.find());
            if (offset < lines.length()) {
                printStringLines(out, lines.substring(offset));
            }
        }
        else {
            printStringLines(out, lines);
        }
        out.println();

        out.println("\treturn _hasBuffer ? buffer.length : buffer.join(\"\");");
        out.println("},");
        if (attrs != null && attrs.size() > 0) {
            out.println("{");
            Iterator<String> iter = attrs.keySet().iterator();
            while (iter.hasNext()) {
                String aname = iter.next();
                String avalue = attrs.get(aname);
                out.print("\t\"");
                printEscaped(out, aname);
                out.print("\": \"");
                printEscaped(out, avalue);
                out.print("\"");
                if (iter.hasNext()) {
                    out.print(",");
                }
                out.println();
            }
            out.print("}");
        }
        else {
            out.print("null");
        }
        out.print(", ");
        out.print(this.authoritative);
        out.println(");");
    }

    //
    // Private functions
    //

    private static Map<String,String> parseAttrs(String s) {
        Map<String,String> attrs = new HashMap<String,String>();
        Matcher matcher = RE_ATTR.matcher(s);
        while (matcher.find()) {
            String aname = matcher.group(1);
            String avalue = matcher.group(2).replaceAll("^['\"]|['\"]$", "");
            attrs.put(aname, avalue);
        }
        return attrs;
    }

    private static String readLines(BufferedReader in) throws IOException {
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            str.append(line);
            str.append('\n');
        }
        return str.toString();
    }

    private static void printLine(PrintWriter out, String... ss) {
        for (String s : ss) {
            out.print(s);
        }
    }

    private static void printStringLines(PrintWriter out, String... ss) {
        for (String s : ss) {
            String[] lines = s.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                printStringLine(out, line, i < lines.length - 1 ? "\n" : "");
            }
        }
    }
    private static void printStringLine(PrintWriter out, String... ss) {
        out.print("\tbuffer[_i++] = \"");
        for (String s : ss) {
            printEscaped(out, s);
        }
        out.println("\";");
    }

    private static void printDataLine(PrintWriter out, String s) {
        out.print("\tbuffer[_i++] = data");
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

} // class TemplateTask