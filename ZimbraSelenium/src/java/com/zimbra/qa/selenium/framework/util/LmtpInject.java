package com.zimbra.qa.selenium.framework.util;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.lmtp.LmtpClient;
import com.zimbra.common.lmtp.LmtpProtocolException;
import com.zimbra.qa.selenium.framework.core.ExecuteHarnessMain;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;

/**
 * @author Matt Rhoades
 *
 */
public class LmtpInject {
	protected static Logger logger = LogManager.getLogger(LmtpInject.class);

	/**
	 * Inject a mime file to an email addresses
	 * @param recipient an email addresses
	 * @param mime the mime file or directory of files to inject
	 * @throws HarnessException
	 */
	public static void injectFile(String recipient, File mime) throws HarnessException {

		// conver the recipient to an array
		injectFile(
				Arrays.asList(recipient.split(",")),
				mime
		);

	}

	/**
	 * Inject a mime file to a list of email addresses
	 * @param recipients an array of email addresses
	 * @param mime the mime file or directory of files to inject
	 * @throws HarnessException
	 */
	public static void injectFile(List<String> recipients, File mime) throws HarnessException {

		// Use default sender
		injectFile(
				recipients, 
				"foo@example.com", 
				mime);

	}

	/**
	 * Inject a mime file to a list of email addresses
	 * @param recipients an array of email addresses
	 * @param sender the sender of the message
	 * @param mime the mime file or directory of files to inject
	 * @throws HarnessException
	 */
	public static void injectFile(List<String> recipients, String sender, File mime) throws HarnessException  {

		try {
			
			try {

				injectFolder(recipients, sender, mime);

			} finally {

				Stafpostqueue sp = new Stafpostqueue();
				sp.waitForPostqueue();

			}
			
		} catch (IOException e) {
			throw new HarnessException("Unable to read mime file "+ mime.getAbsolutePath(), e);
		} catch (LmtpProtocolException e) {
			throw new HarnessException("Unable to inject mime file "+ mime.getAbsolutePath(), e);
		}

	}

	protected static void injectFolder(List<String> recipients, String sender, File mime) throws IOException, LmtpProtocolException {

		if ( mime.isFile() ) { 

			// Inject a single file
			inject(recipients, sender, mime);

		} else if ( mime.isDirectory() ) {

			for (File f : mime.listFiles()) {

				injectFolder(recipients, sender, f);

			}

		} else {

			// Unknown File type
			logger.warn("MIME file was not file or directory.  Skipping. " + mime.getAbsolutePath());

		}

	}
	
	
	protected static void inject(List<String> recipients, String sender, File mime) throws IOException, LmtpProtocolException {
		logger.info("LMTP: to: "+ recipients.toString());
		logger.info("LMTP: from: "+ sender);
		logger.info("LMTP: filename: "+ mime.getAbsolutePath());

		long length = mime.length();

		logger.info( length > 2000 ? "LMTP: large mime" : "LMTP:\n" + new String(ByteUtil.getContent(mime)));

		LogManager.getLogger(ExecuteHarnessMain.TraceLoggerName).trace(
				"Inject using LMTP: " +
				" to:"+ recipients.toString() + 
				" from:"+ sender +
				" filename:"+ mime.getAbsolutePath()
		);


		LmtpClient lmtp = null;
		try {

			lmtp = new LmtpClient( 
					ZimbraSeleniumProperties.getStringProperty("server.host"),
					7025);

			lmtp.sendMessage(new FileInputStream(mime), recipients.toArray(new String[recipients.size()]), sender, "Selenium", length);

		} finally {

			// TODO: do we need to close the FileInputStream?

			if (lmtp != null ) {
				lmtp.close();
				lmtp = null;
			}

		}

	}

}
