package framework.util;

import java.io.*;

public class MakeResultUTF8 {
 
    public static void main(String[] args) throws Exception {
	String resultDir = args[0];
	makeUTF8(resultDir, "");
    }

    public static void makeUTF8(String resultDir) throws Exception {
	makeUTF8(resultDir, "");
    }

    public static void makeUTF8(String resultDir, String skippedTestHTML)
	    throws Exception {

	File file = new File(resultDir + "/emailable-report.html");
	File tempFile = new File(resultDir + "/emailable-reportUTF8.html");
	BufferedReader br = new BufferedReader(new BufferedReader(
		new InputStreamReader(new FileInputStream(file), "UTF8")));
	BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
		new FileOutputStream(tempFile), "UTF8"));
	new BufferedWriter(new FileWriter(tempFile));
	String str = null;
	int index = 0;
	boolean isFirstTable = true;
	while ((str = br.readLine()) != null) {
	    wr.write(str);
	    wr.newLine();
	    // add skipped tests table
	    if ((str.indexOf("</table>") >= 0) && isFirstTable) {
		wr.write(skippedTestHTML);
		wr.newLine();
		isFirstTable = false;
	    }

	    index++;
	    if (index == 2) {// insert blank line in the second line
		wr
			.write(" <meta http-equiv=\"Content-Type\" content=\"text/html\"; Charset=\"UTF-8\"  /> ");
		wr.newLine();
	    }
	}

	br.close();
	wr.close();
    }

}
