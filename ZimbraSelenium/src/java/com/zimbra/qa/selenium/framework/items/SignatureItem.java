package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;

public class SignatureItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	/**
	 * The ID for this tag
	 */
	protected String dId;

	/**
	 * The Name for this tag
	 */
	protected String dName;

	// //
	// FINISH: SOAP Data
	// //

	// //
	// START: GUI Data
	// //

	/**
	 * The icon image for this tag
	 */
	public String gIconImage;

	/**
	 * The name for this tag
	 */
	public String gName;
	public String dBodyText;
	public String dBodyHtmlText;

	// //
	// FINISH: GUI Data
	// //

	/**
	 * Create a mail item
	 */
	public SignatureItem() {
	}

	public void setId(String id) {
		dId = id;
	}

	public String getId() {
		return (dId);
	}

	public void setName(String name) {
		dName = name;
	}

	public String getName() {
		return (dName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.items.IItem#CreateSOAP(framework.util.ZimbraAccount)
	 */
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	public static SignatureItem importFromSOAP(Element sig) throws HarnessException {
		
		if ( sig == null )
			throw new HarnessException("Element cannot be null");

		SignatureItem item = null;

		try {

			// Make sure we only have the <tag/> part
			Element t = ZimbraAccount.SoapClient.selectNode(sig,
					"//acct:signature");
			if (t == null)
				throw new HarnessException(
						"Element does not contain an <tag/> element");

			// Create the object
			item = new SignatureItem();

			// Set the ID
			item.setId(t.getAttribute("id", null));
			// Set tag name
			item.setName(t.getAttribute("name", null));
			
			Element contentBodyHtml = ZimbraAccount.SoapClient.selectNode(sig, "//acct:content[@type='text/html']");
			Element contentBodyText = ZimbraAccount.SoapClient.selectNode(sig, "//acct:content[@type='text/plain']");
			if ( contentBodyHtml != null ) {
				item.dBodyHtmlText = contentBodyHtml.getText().trim();
			}else if ( contentBodyText != null ) {
				item.dBodyText = contentBodyText.getText().trim();
			}


			return (item);

		} catch (Exception e) {
			throw new HarnessException("Could not parse GetMsgResponse: "
					+ sig.prettyPrint(), e);
		} finally {
			if (item != null)
				logger.info(item.prettyPrint());
		}

	}

	public static SignatureItem importFromSOAP(ZimbraAccount account,
			String name) throws HarnessException {

		if (account == null)
			throw new HarnessException("account cannot be null");
		if (name == null)
			throw new HarnessException("name cannot be null");
		if (name.trim().length() == 0)
			throw new HarnessException("name cannot be empty: (" + name + ")");

		try {
			account
					.soapSend("<GetSignaturesRequest xmlns='urn:zimbraAccount'/>");
			Element[] results = account
					.soapSelectNodes("//acct:signature[@name='" + name + "']");
			return (importFromSOAP(results[0]));

		} catch (Exception e) {
			throw new HarnessException("Unable to import using SOAP name("
					+ name + ") and account(" + account.EmailAddress + ")", e);
		}
	}

	@Override
	public String prettyPrint() {
		// TODO Auto-generated method stub
		return null;
	}

}
