package framework.util;

import java.io.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class SkipTestsUtil {
	static public String getContents(File aFile) throws Exception {
		HttpClient client = new HttpClient();
		String bugNo = null;
		String line = null;
		StringBuilder contents = new StringBuilder();

		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(aFile));
		System.out.println("Execution Started..");

		while ((line = reader.readLine()) != null) {
			if (line.length() < 100) {
				contents.append(line).append(
						System.getProperty("line.separator"));
				continue;
			}
			if (!line.substring(0, 1).trim().equals("#")) {
				bugNo = line.substring(line.indexOf("BUGS:") + 5,
						line.indexOf(";REMARK")).trim();
				if (!bugNo.equals("na")) {
					GetMethod method = new GetMethod(
							"http://bugzilla.zimbra.com/show_bug.cgi?id="
									+ bugNo + "&ctype=xml");
					try {
						client.executeMethod(method);
						String bugBody = method.getResponseBodyAsString();
						String bugResolution = bugBody.substring(bugBody
								.indexOf("<bug_status>") + 12, bugBody
								.indexOf("</bug_status>"));
						if (bugResolution.equals("VERIFIED")) {
							System.out.println("Commented bug - " + bugNo);
							contents.append("#" + line).append(
									System.getProperty("line.separator"));
							continue;
						}
					} catch (Exception e) {
						System.err.println(e);
					} finally {
						method.releaseConnection();
					}
				}
			} else if (line.indexOf("BUGS:") > 0 && !line.contains("BUGS:na")
					&& !line.contains("BUGS:<bug1,bug2>")
					&& !line.contains("BUGS:1234")) {
				bugNo = line.substring(line.indexOf("BUGS:") + 5,
						line.indexOf(";REMARK")).trim();
				if (!bugNo.equals("na")) {
					GetMethod method = new GetMethod(
							"http://bugzilla.zimbra.com/show_bug.cgi?id="
									+ bugNo + "&ctype=xml");
					try {
						client.executeMethod(method);
						String bugBody = method.getResponseBodyAsString();
						String bugResolution = bugBody.substring(bugBody
								.indexOf("<bug_status>") + 12, bugBody
								.indexOf("</bug_status>"));
						if (bugResolution.equals("NEW")
								|| bugResolution.equals("ASSIGNED")
								|| bugResolution.equals("REOPENED")
								|| bugResolution.equals("RESOLVED")) {
							System.out.println("Removed comment from bug - "
									+ bugNo);
							contents.append(line.replace("#", "")).append(
									System.getProperty("line.separator"));
							continue;
						}
					} catch (Exception e) {
						System.err.println(e);
					} finally {
						method.releaseConnection();
					}
				}
			}
			contents.append(line).append(System.getProperty("line.separator"));
		}
		System.out.println("Execution Completed.");
		return contents.toString();
	}

	static public void setContents(File aFile, String aContents)
			throws FileNotFoundException, IOException {
		Writer output = new BufferedWriter(new FileWriter(aFile));
		try {
			output.write(aContents);
		} finally {
			output.close();
		}
	}

	public static void main(String[] args) throws Exception {
		File dir = new File("conf");
		File file = new File(dir.getAbsolutePath() + "/skipTests.txt");
		setContents(file, getContents(file));
	}
}