package com.zimbra.cs.client.soap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.cs.service.versioncheck.VersionCheckService;

public class LmcVersionCheckRequest extends LmcSoapRequest {

	private String mAction;
    protected Element getRequestXML() {
        Element request = DocumentHelper.createElement(VersionCheckService.VC_REQUEST);
        if(mAction == null) {
        	this.setAction(VersionCheckService.VERSION_CHECK_CHECK);
        }
        addAttrNotNull(request, AdminConstants.E_ACTION, mAction);
        return request;
    }

    protected LmcSoapResponse parseResponseXML(Element responseXML) {
        return new LmcVersionCheckResponse();
    }

	public String getAction() {
		return mAction;
	}

	public void setAction(String action) {
		mAction = action;
	}
}
