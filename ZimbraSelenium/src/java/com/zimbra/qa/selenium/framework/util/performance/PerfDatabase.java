package com.zimbra.qa.selenium.framework.util.performance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;


/**


mysql> create table apps ( 
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
 name VARCHAR(256) 
 );
mysql> insert into apps (name) VALUES ('AJAX');
mysql> insert into apps (name) VALUES ('HTML');
mysql> insert into apps (name) VALUES ('MOBILE');
mysql> insert into apps (name) VALUES ('ADMIN');
mysql> insert into apps (name) VALUES ('DESKTOP');
mysql> insert into apps (name) VALUES ('OCTOPUS');

mysql> create table actions ( 
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
 name VARCHAR(256) 
 );
mysql> insert into actions (name) VALUES ('ZmMailApp');

mysql> create table builds ( 
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
 build VARCHAR(256) 
 );

mysql> create table milestones ( 
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
 milestone VARCHAR(256) 
 );
mysql> insert into milestones (milestone) VALUES ('GunsNRoses');
mysql> insert into milestones (milestone) VALUES ('Helix');
mysql> insert into milestones (milestone) VALUES ('IronMaiden');
mysql> insert into milestones (milestone) VALUES ('JudasPriest');


mysql> create table browsers ( 
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
 name VARCHAR(512) 
 );

mysql> create table clients ( 
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
 name VARCHAR(256) 
 );

mysql> create table messages ( 
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
 name VARCHAR(1024) 
 );

mysql> create table perf2 (
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 created TIMESTAMP(8),
 name VARCHAR(35),
 appid INT,
 buildid INT,
 browserid INT,
 clientid INT,
 milestoneid INT,
 start BIGINT,
 launched BIGINT,
 loaded BIGINT,
 delta BIGINT,
 delta_internal BIGINT,
 messageid INT
 );

 **/


public class PerfDatabase {
	private static final Logger logger = LogManager.getLogger(PerfDatabase.class);

	public static class DbPerf {
		public static final String NAME = "name";
		public static final String APPID = "appid";
		public static final String BUILDID = "buildid";
		public static final String BROWSERID = "browserid";
		public static final String CLIENTID = "clientid";
		public static final String MILESTONEID = "milestoneid";
		public static final String START = "start";
		public static final String LAUNCHED = "launched";
		public static final String LOADED = "loaded";
		public static final String DELTA = "delta";
		public static final String DELTA_INTERNAL = "delta_internal";
		public static final String MESSAGEID = "messageid";
	}
	
	/**
	 * Insert a PerfData values into the database
	 * @param data
	 * @throws HarnessException
	 */
	public static void record(PerfData data) throws HarnessException {

		// Make sure the action exists
		PerfDatabase.getInstance().getActionKey(data.Key.toString());

		// A mapping of column names to values
		HashMap<String, String> table = new HashMap<String, String>();

		table.put(DbPerf.NAME, "'" + data.Key.toString() +"'");		// VARCHAR ... enclose in single quotes
		table.put(DbPerf.APPID, "" + PerfDatabase.getInstance().getAppType());
		table.put(DbPerf.BUILDID, "" + PerfDatabase.getInstance().getBuildID());
		table.put(DbPerf.MILESTONEID, "" + PerfDatabase.getInstance().getMilestoneID());
		table.put(DbPerf.BROWSERID, "" + PerfDatabase.getInstance().getBrowserID());
		table.put(DbPerf.CLIENTID, "" + PerfDatabase.getInstance().getClientID());
		table.put(DbPerf.MESSAGEID, "" + PerfDatabase.getInstance().getMessageKey(data.Message));

		table.put(DbPerf.START, "" + data.StartStamp);
		table.put(DbPerf.LAUNCHED, "" + data.LaunchStamp);
		table.put(DbPerf.LOADED, "" + data.FinishStamp);

		table.put(DbPerf.DELTA, "" + "" + (Long.parseLong(data.FinishStamp) - data.StartStamp));

		String deltaInternal = "0";
		if ( data.LaunchStamp != null && !data.LaunchStamp.trim().equals("") ) {
			deltaInternal = "" + (Long.parseLong(data.FinishStamp) - Long.parseLong(data.LaunchStamp));
		}
		table.put(DbPerf.DELTA_INTERNAL, deltaInternal);

		// Insert the map into the database
		PerfDatabase.getInstance().insertPerf(table);

	}

	protected void insertPerf(Map<String, String> data) throws HarnessException {

		// Convert the keys, values into comma separated strings
		String columns = Arrays.asList(data.keySet().toArray()).toString().replace("[", "").replace("]", "");
		String values = Arrays.asList(data.values().toArray()).toString().replace("[", "").replace("]", "");

		String command = String.format("INSERT INTO perf2 (%s) VALUES (%s)", columns, values);
		Statement statement = null;
		
		try {

			statement = DatabaseConnection.getInstance().createStatement();
			logger.info("Statement: "+ command);
			int ret = statement.executeUpdate(command);
			logger.info("Statement: ret="+ ret);


		} catch (SQLException e) {
			throw new HarnessException(command, e);
		} finally {
			DatabaseConnection.closeStatement(statement);
		}


	}

		
	/**
	 * Get the ID corresponding to the app from the perf DB
	 * @return
	 * @throws HarnessException
	 */
	protected int getAppType() throws HarnessException {
		getAppTypeTable();

		String type = ZimbraSeleniumProperties.getAppType().toString().toLowerCase();
		if (!appTable.containsKey(type)) {
			throw new HarnessException("Unknown app type: "+ type +".  Maybe INSERT INTO apps ('name') VALUES ('"+ type +"') ?");
		}

		return (appTable.get(type));
	}

	private static HashMap<String, Integer> appTable = null;
	protected synchronized void getAppTypeTable() throws HarnessException {
		if ( appTable == null ) {
			appTable = new HashMap<String, Integer>();

			String query = "SELECT id, name FROM apps";
			Statement statement = null;
			
			try {

				statement = DatabaseConnection.getInstance().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {

					Integer id = rs.getInt("id");
					String name = rs.getString("name").toLowerCase();

					logger.info("getAppTypeTable(): id="+ id +" name="+ name);

					appTable.put(name, id);

				}

			} catch (SQLException e) {
				throw new HarnessException(query, e);
			} finally {
				DatabaseConnection.closeStatement(statement);
			}


		}
	}

	/**
	 * Get the ID corresponding to the Build from the perf DB
	 * @return
	 * @throws HarnessException
	 */
	protected int getBuildID() throws HarnessException {
		getBuildTable();

		String build = getVersionString();
		if (!buildTable.containsKey(build)) {
			insertBuild(build);
		}

		return (buildTable.get(build));
	}

	private static HashMap<String, Integer> buildTable = null;
	protected synchronized void getBuildTable() throws HarnessException {
		if ( buildTable == null ) {
			buildTable = new HashMap<String, Integer>();

			String query = "SELECT id, build FROM builds";
			Statement statement = null;
			
			try {

				statement = DatabaseConnection.getInstance().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {

					Integer id = rs.getInt("id");
					String build = rs.getString("build");

					logger.info("getBuildTable(): id="+ id +" build="+ build);

					buildTable.put(build, id);

				}

			} catch (SQLException e) {
				throw new HarnessException(query, e);
			} finally {
				DatabaseConnection.closeStatement(statement);
			}


		}
	}
	protected void insertBuild(String build) throws HarnessException {

		if ( buildTable.containsKey(build) ) {
			throw new HarnessException("buildTable already contains "+ build);
		}
		
		String command = String.format("INSERT INTO builds (build) VALUES ('%s')", build);
		Statement statement = null;
		

		try {

			statement = DatabaseConnection.getInstance().createStatement();
			logger.info("Statement: "+ command);
			int ret = statement.executeUpdate(command);
			logger.info("Statement: ret="+ ret);


		} catch (SQLException e) {
			throw new HarnessException(command, e);
		} finally {
			DatabaseConnection.closeStatement(statement);
		}

		// Reset the action table to pick up the new ID
		buildTable = null;
		getBuildTable();

	}


	/**
	 * Get the ID corresponding to the browser from the perf DB
	 * @return
	 * @throws HarnessException
	 */
	protected int getBrowserID() throws HarnessException {
		getBrowserTable();
		
		String browser = ZimbraSeleniumProperties.getStringProperty("CalculatedBrowser", "unknown");

		if (!browserTable.containsKey(browser)) {
			insertBrowser(browser);
		}

		return (browserTable.get(browser));
	}

	private static HashMap<String, Integer> browserTable = null;
	protected synchronized void getBrowserTable() throws HarnessException {
		if ( browserTable == null ) {
			browserTable = new HashMap<String, Integer>();

			String query = "SELECT id, name FROM browsers";
			Statement statement = null;
			
			try {

				statement = DatabaseConnection.getInstance().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {

					Integer id = rs.getInt("id");
					String name = rs.getString("name");

					logger.info("getBrowserTable(): id="+ id +" name="+ name);

					browserTable.put(name, id);

				}

			} catch (SQLException e) {
				throw new HarnessException(query, e);
			} finally {
				DatabaseConnection.closeStatement(statement);
			}


		}
	}
	protected void insertBrowser(String browser) throws HarnessException {

		if ( browserTable.containsKey(browser) ) {
			throw new HarnessException("browserTable already contains "+ browser);
		}
		
		String command = String.format("INSERT INTO browsers (name) VALUES ('%s')", browser);
		Statement statement = null;
		

		try {

			statement = DatabaseConnection.getInstance().createStatement();
			logger.info("Statement: "+ command);
			int ret = statement.executeUpdate(command);
			logger.info("Statement: ret="+ ret);


		} catch (SQLException e) {
			throw new HarnessException(command, e);
		} finally {
			DatabaseConnection.closeStatement(statement);
		}

		// Reset the action table to pick up the new ID
		browserTable = null;
		getBrowserTable();

	}

	/**
	 * Get the ID corresponding to the client OS from the perf DB
	 * @return
	 * @throws HarnessException
	 */
	protected int getClientID() throws HarnessException {
		getClientTable();
		
		String os = OperatingSystem.getOSType().toString();

		if (!clientTable.containsKey(os)) {
			insertClient(os);
		}

		return (clientTable.get(os));
	}

	private static HashMap<String, Integer> clientTable = null;
	protected synchronized void getClientTable() throws HarnessException {
		if ( clientTable == null ) {
			clientTable = new HashMap<String, Integer>();

			String query = "SELECT id, name FROM clients";
			Statement statement = null;
			
			try {

				statement = DatabaseConnection.getInstance().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {

					Integer id = rs.getInt("id");
					String name = rs.getString("name");

					logger.info("getClientTable(): id="+ id +" name="+ name);

					clientTable.put(name, id);

				}

			} catch (SQLException e) {
				throw new HarnessException(query, e);
			} finally {
				DatabaseConnection.closeStatement(statement);
			}


		}
	}
	protected void insertClient(String os) throws HarnessException {

		if ( clientTable.containsKey(os) ) {
			throw new HarnessException("clientTable already contains "+ os);
		}
			
		String command = String.format("INSERT INTO clients (name) VALUES ('%s')", os);
		Statement statement = null;

		try {

			statement = DatabaseConnection.getInstance().createStatement();
			logger.info("Statement: "+ command);
			int ret = statement.executeUpdate(command);
			logger.info("Statement: ret="+ ret);


		} catch (SQLException e) {
			throw new HarnessException(command, e);
		} finally {
			DatabaseConnection.closeStatement(statement);
		}

		// Reset the action table to pick up the new ID
		clientTable = null;
		getClientTable();

	}

	/**
	 * Get the ID corresponding to the key from the perf DB
	 * @return
	 * @throws HarnessException
	 */
	protected int getActionKey(String action) throws HarnessException {
		getActionTable();

		if (!actionTable.containsKey(action)) {
			insertAction(action);
		}

		return (actionTable.get(action));
	}

	private static HashMap<String, Integer> actionTable = null;
	protected synchronized void getActionTable() throws HarnessException {
		if ( actionTable == null ) {
			actionTable = new HashMap<String, Integer>();

			String query = "SELECT id, name FROM actions";
			Statement statement = null;
			
			try {


				statement = DatabaseConnection.getInstance().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {

					Integer id = rs.getInt("id");
					String name = rs.getString("name");

					logger.info("getActionTable(): id="+ id +" name="+ name);

					actionTable.put(name, id);

				}

			} catch (SQLException e) {
				throw new HarnessException(query, e);
			} finally {
				DatabaseConnection.closeStatement(statement);
			}


		}
	}
	protected void insertAction(String action) throws HarnessException {

		if ( actionTable.containsKey(action) ) {
			throw new HarnessException("actionTable already contains "+ action);
		}
		
		String command = String.format("INSERT INTO actions (name) VALUES ('%s')", action);
		Statement statement = null;
		

		try {

			statement = DatabaseConnection.getInstance().createStatement();
			logger.info("Statement: "+ command);
			int ret = statement.executeUpdate(command);
			logger.info("Statement: ret="+ ret);


		} catch (SQLException e) {
			throw new HarnessException(command, e);
		} finally {
			DatabaseConnection.closeStatement(statement);
		}

		// Reset the action table to pick up the new ID
		actionTable = null;
		getActionTable();

	}

	/**
	 * Get the Milestone (string) corresponding to the version number from the perf DB
	 * @return
	 * @throws HarnessException
	 */
	protected int getMilestoneID() throws HarnessException {
		getMilestoneTable();

		if ( getVersionString().startsWith("6") ) {
			return (milestoneTable.get("GunsNRoses"));
		}
		if ( getVersionString().startsWith("7") ) {
			return (milestoneTable.get("Helix"));
		}
		if ( getVersionString().startsWith("8") ) {
			return (milestoneTable.get("IronMaiden"));
		}
		if ( getVersionString().startsWith("9") ) {
			return (milestoneTable.get("JudasPriest"));
		}

		throw new HarnessException("Unable to determine Milestone from version string: "+ getVersionString());

	}
	
	private static HashMap<String, Integer> milestoneTable = null;
	protected synchronized void getMilestoneTable() throws HarnessException {
		if ( milestoneTable == null ) {
			milestoneTable = new HashMap<String, Integer>();

			String query = "SELECT id, milestone FROM milestones";
			Statement statement = null;
			
			try {

				statement = DatabaseConnection.getInstance().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {

					Integer id = rs.getInt("id");
					String milestone = rs.getString("milestone");

					logger.info("getActionTable(): id="+ id +" milestone="+ milestone);

					milestoneTable.put(milestone, id);

				}

			} catch (SQLException e) {
				throw new HarnessException(query, e);
			} finally {
				DatabaseConnection.closeStatement(statement);
			}


		}
	}


	protected String versionString = null;
	protected String getVersionString() throws HarnessException {
		if ( versionString == null ) {
			ZimbraAdminAccount.GlobalAdmin().soapSend("<GetVersionInfoRequest xmlns='urn:zimbraAdmin'/>");
			Element getVersionInfo = ZimbraAdminAccount.GlobalAdmin().soapSelectNode("//admin:GetVersionInfoResponse//admin:info", 1);
			versionString = String.format("%s.%s.%s",
					getVersionInfo.getAttribute("majorversion", "X"),
					getVersionInfo.getAttribute("minorversion", "X"),
					getVersionInfo.getAttribute("microversion", "X"));
		}
		return (versionString);
	}

	/**
	 * Get the ID corresponding to the key from the perf DB
	 * @return
	 * @throws HarnessException
	 */
	protected int getMessageKey(String description) throws HarnessException {
		getMessageTable();

		if (!messageTable.containsKey(description)) {
			insertMessage(description);
		}

		return (messageTable.get(description));
	}

	private static HashMap<String, Integer> messageTable = null;
	protected synchronized void getMessageTable() throws HarnessException {
		if ( messageTable == null ) {
			messageTable = new HashMap<String, Integer>();

			String query = "SELECT id, name FROM messages";
			Statement statement = null;
			
			try {

				statement = DatabaseConnection.getInstance().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {

					Integer id = rs.getInt("id");
					String name = rs.getString("name");

					logger.info("getMessageTable(): id="+ id +" name="+ name);

					messageTable.put(name, id);

				}

			} catch (SQLException e) {
				throw new HarnessException(query, e);
			} finally {
				DatabaseConnection.closeStatement(statement);
			}


		}
	}
	protected void insertMessage(String description) throws HarnessException {

		if ( messageTable.containsKey(description) ) {
			throw new HarnessException("messageTable already contains "+ description);
		}
		
		String command = String.format("INSERT INTO messages (name) VALUES ('%s')", description);
		Statement statement = null;

		try {

			statement = DatabaseConnection.getInstance().createStatement();
			logger.info("Statement: "+ command);
			int ret = statement.executeUpdate(command);
			logger.info("Statement: ret="+ ret);


		} catch (SQLException e) {
			throw new HarnessException(command, e);
		} finally {
			DatabaseConnection.closeStatement(statement);
		}

		// Reset the description table to pick up the new ID
		messageTable = null;
		getMessageTable();

	}
	
	public static PerfDatabase getInstance() {
		if (Instance == null) {
			synchronized(PerfDatabase.class) {
				if ( Instance == null) {
					Instance = new PerfDatabase();
				}
			}
		}
		return (Instance);
	}


	private volatile static PerfDatabase Instance;

	protected PerfDatabase() {	
		logger.info("New "+ this.getClass().getCanonicalName());			
	}

	protected static class DatabaseConnection {
		private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);


		public static final String dbDefaultURL = "jdbc:mysql://10.20.140.198/perf";
		public static final String dbDefaultUsername = "perf";
		public static final String dbDefaultUserpass = "perf";


		public Connection getConnection() throws SQLException {

			if ( conn == null ) {

				conn = DriverManager.getConnection(dbURL, dbUsername, dbUserpass);
				logger.info("Database connection established: "+ dbURL);

			}

			return (conn);
		}

		public Statement createStatement() throws SQLException {

			return (getConnection().createStatement());

		}
		
		protected static void closeStatement(Statement s) {
			if ( s == null ) {
				return;
			}
			try {
				s.close();
			} catch (SQLException e) {
				logger.error("Exception when closing statement", e);
			}
		}

		public static DatabaseConnection getInstance() throws HarnessException {
			if (Instance == null) {
				synchronized(DatabaseConnection.class) {
					if ( Instance == null) {
						Instance = new DatabaseConnection();
					}
				}
			}
			return (Instance);
		}


		private volatile static DatabaseConnection Instance;
		private Connection conn = null;
		private String dbURL = null;
		private String dbUsername = null;
		private String dbUserpass = null;

		protected DatabaseConnection() throws HarnessException {	
			logger.info("New "+ this.getClass().getCanonicalName());			

			try {

				Class.forName("com.mysql.jdbc.Driver");
				
				dbURL = ZimbraSeleniumProperties.getStringProperty("performance.metrics.db.url", dbDefaultURL);
				dbUsername = ZimbraSeleniumProperties.getStringProperty("performance.metrics.db.username", dbDefaultUsername);
				dbUserpass = ZimbraSeleniumProperties.getStringProperty("performance.metrics.db.userpass", dbDefaultUserpass);

			} catch (ClassNotFoundException e) {
				throw new HarnessException(e);
			}


		}


	}


}
