package projects.zcs.bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class CreateTestSuites {
	//if we want a particular set of browsers or locales to be run first.. add them here.
	private static String priorityLocales = "ru,ko,fr";
	private static String priorityBrowsers = "";
	
	// Enter clients to be used, they can be qafe1,qafe2,qa33-vista-qtp1 or
	// qa33-vista-qtp2 PS: should *not* be fully qualified names, since
	// its used for queuing purposes on tms
	private static String[] clientsToUse = {"qafe1", "qafe2",
			"qa33-vista-qtp1", "qa33-vista-qtp2", "browserperf1",
			"browserperf2"};
	// private static String[] clientsToUse = { "browserperf2" };

	// Enter Servers to be used, qa60,qa62,qa54 or qa65, but should match # of
	// clients(since currently 1server-manyClients isnt implemented)
	// Servers should be fully qualified name
	private static String[] serversToUse = {"qa62.lab.zimbra.com"};

	private static String branch = "FRANKLIN";

	private static ArrayList<ServerMachine> allServers = new ArrayList<ServerMachine>();

	private static ArrayList<ClientMachine> requiredClients = new ArrayList<ClientMachine>();
	private static ArrayList<ClientMachine> allClients = new ArrayList<ClientMachine>();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// qafe1 IE7, FF3, SF3
		// qafe2 IE6, FF2, SF3
		// qa33-vista-qtp1 IE8, FF3, SF3
		// qa33-vista-qtp2 IE7, FF2, SF3

		// add all clients

		allClients.add(new ClientMachine("qafe1", "XP", "IE7,FF3,SF3"));
		allClients.add(new ClientMachine("qafe2", "XP", "IE6,FF2")); // remove
																		// SF3
																		// to
																		// balance
																		// out
		allClients.add(new ClientMachine("qa33-vista-qtp1", "VISTA", "IE8"));// to
																				// balance
																				// out
																				// ,
																				// remove
																				// FF3
																				// and
																				// SF3
		allClients.add(new ClientMachine("qa33-vista-qtp2", "VISTA",
				"IE7,FF2,SF3"));
		allClients.add(new ClientMachine("browserperf1", "XP", "IE6,FF2")); // to
																			// balance
																			// out
																			// ,
																			// remove
																			// SF3
		allClients.add(new ClientMachine("browserperf2", "XP", "IE7,FF3,SF3"));

		// add all servers
		allServers.add(new ServerMachine("qa62.lab.zimbra.com", "RHEL4"));
		allServers.add(new ServerMachine("qa60.lab.zimbra.com", "UBUNTU6"));
		allServers.add(new ServerMachine("qa54.lab.zimbra.com", "UBUNTU8_64"));
		allServers.add(new ServerMachine("qa65.lab.zimbra.com", "UBUNTU6_64"));

		// load only required clients
		loadOnlyRequiredClients();

		// load combinations file
		File combinations = new File("conf/baseBrowserLocalecombinations.csv");
		try {
			BufferedReader br = new BufferedReader(new BufferedReader(
					new InputStreamReader(new FileInputStream(combinations),
							"UTF8")));

			String str = null;
			ArrayList<String> prioritySuites = new ArrayList<String>();
			ArrayList<String> normalSuites = new ArrayList<String>();
			while ((str = br.readLine()) != null) {
				String[] temp = str.split(",");
				String locale = temp[0];
				String browser = temp[1];
				String os = temp[2];
				if (str.indexOf("locale") >= 0)// skip the first line
					continue;
				String job= getJobIfMatched(locale, browser, os);
				if(!job.equals("")) {
					if(priorityLocales.indexOf(locale) >=0 || priorityBrowsers.indexOf(browser) >=0 )
						prioritySuites.add(job);//add to priority list
					else
						normalSuites.add(job);
				}

			}
			
			printJobs(prioritySuites);//first print the priority suites
			printJobs(normalSuites);//then print normal suites
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void printJobs(ArrayList<String> jobs) throws Exception {
		for (String  job : jobs) {
			System.out.println(job);
		}
	}

	public static String getJobIfMatched(String locale, String browser, String os) throws Exception {

		boolean foundBrowser = false;
		ArrayList<String> matchedClients = new ArrayList<String>();
		for (ClientMachine rc : requiredClients) {
			if (rc.browsers.contains(browser)) {
				matchedClients.add(rc.machineName);
				foundBrowser = true;

			}

		}
		if (foundBrowser) {
			Random generator = new Random();
			int num = generator.nextInt(matchedClients.size());
			String clientName = matchedClients.get(num);
			String serverName = serversToUse[generator
					.nextInt(serversToUse.length)];
			ServerMachine sMachine = getServer(serverName);
			String browserName = normalizeBrowserName(browser);
			String printStr = "/opt/qa/tools/kickoffTest.rb " + clientName
					+ " " + branch + " " + sMachine.osType
					+ " 'Selng&cmachine=" + clientName + "&s_locale=" + locale
					+ "&s_browser=" + browserName + "&s_server=" + serverName
					+ "' N $1 >> /opt/qa/tools/" + serverName + ".txt 2>&1";

			return printStr;
		}
		return "";
	}

	public static ServerMachine getServer(String serverName) throws Exception {
		for (ServerMachine rs : allServers) {
			if (rs.machineName.contains(serverName)) {
				return rs;
			}

		}
		throw new Exception("Server with serverName '" + serverName
				+ "' not found");
	}

	public static String normalizeBrowserName(String browser) {
		if (browser.indexOf("IE") >= 0)
			return "IE";
		else if (browser.indexOf("FF2") >= 0)
			return "FF";
		else if (browser.indexOf("SF3") >= 0)
			return "SF";
		else
			return browser;
	}

	public static void loadOnlyRequiredClients() {
		for (int i = 0; i < clientsToUse.length; i++) {
			for (ClientMachine c : allClients) {
				if (c.machineName.equals(clientsToUse[i]))
					requiredClients.add(c);

			}

		}
	}

}
