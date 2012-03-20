package com.zimbra.qa.selenium.results;

import java.io.File;

import org.apache.commons.cli.*;
import org.apache.log4j.*;


public class ResultsMain {
	private static Logger logger = LogManager.getLogger(ResultsMain.class);
	
	public static File root = new File(".");
	
    public static void usage(Options o) {

        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("ResultsMain -h | -b <arg> [ -l <log4j> ]", o, true);
        System.exit(1);

    }

	protected static void parseArgs(String[] args) {

        Option h = new Option("h", "help", false, "print usage");
        Option l = new Option("l", "log4j", true, "log4j.properties file");
        Option b = new Option("b", "base", true, "base folder (i.e. TestNG folder containing testng-results.xml file");

        Options options = new Options();
        options.addOption(h);
        options.addOption(l);
        options.addOption(b);

        try {

            CommandLineParser parser = new GnuParser();

            CommandLine cl = parser.parse(options, args);

            // Option: -l <log4j.properties>
            if (cl.hasOption("l")) {

                String propertiesFile = cl.getOptionValue("l");
                File log4jProperties = new File(propertiesFile);
                if (log4jProperties.exists()) {
                    PropertyConfigurator.configure(propertiesFile);
                    logger.debug("Loaded log4j.properites: " + propertiesFile);
                }

            }

            if (cl.hasOption("h")) {
                usage(options);
            }

            if ( cl.hasOption("b") ) {
            	root = new File(cl.getOptionValue("b"));
            }

        } catch (ParseException pe) {
            System.err.println(pe.toString());
            usage(options);
        }

	}
	
	public static void main(String[] args) throws Exception {
		
		// Configure logging
		BasicConfigurator.configure();
		logger.info("Starting ...");

		// Parse any args
		parseArgs(args);

		// Create the new core
		ResultsCore core = new ResultsCore();
		
		// Execute it
		core.execute(root);
		
		System.out.println(core.getResultString());
		
	}

}
