package com.zimbra.cs.client.soap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import java.util.Iterator;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.DomUtil;
import com.zimbra.cs.service.versioncheck.VersionCheckService;
import com.zimbra.cs.service.versioncheck.VersionCheck;
import com.zimbra.cs.versioncheck.VersionUpdate;
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

    protected LmcSoapResponse parseResponseXML(Element responseXML) throws ServiceException {
    	LmcVersionCheckResponse response = new LmcVersionCheckResponse();
        if(mAction == VersionCheck.A_VERSION_CHECK_STATUS) {
	    	try {
	        	Element evc = DomUtil.get(responseXML, VersionCheck.E_VERSION_CHECK);
		        response.setStatus(DomUtil.getAttrBoolean(evc, VersionCheck.A_VERSION_CHECK_STATUS));
		        Element eUpdates = DomUtil.get(evc, VersionCheck.E_UPDATES);
		        for(Iterator<Element> iter = eUpdates.elementIterator();iter.hasNext();) {
		        	Element eUpdate = iter.next();
		        	VersionUpdate upd = new VersionUpdate();
		        	upd.setCritical(DomUtil.getAttrBoolean(eUpdate, VersionCheck.A_CRITICAL));
		        	upd.setType(DomUtil.getAttr(eUpdate, VersionCheck.A_UPDATE_TYPE));
		        	upd.setShortversion(DomUtil.getAttr(eUpdate, VersionCheck.A_SHORT_VERSION));
		        	upd.setRelease(DomUtil.getAttr(eUpdate, VersionCheck.A_RELEASE));
		        	upd.setVersion(DomUtil.getAttr(eUpdate, VersionCheck.A_VERSION));
		        	upd.setDescription(DomUtil.getAttr(eUpdate, VersionCheck.A_DESCRIPTION));
		        	upd.setPlatform(DomUtil.getAttr(eUpdate, VersionCheck.A_PLATFORM));
		        	upd.setBuildtype(DomUtil.getAttr(eUpdate, VersionCheck.A_BUILDTYPE));
		        	upd.setUpdateURL(DomUtil.getAttr(eUpdate, VersionCheck.A_UPDATE_URL));
		        	response.addUpdate(upd);
		        }
	    	} catch (ServiceException ex) {
	    		
	    	}
        }
        return response;    	
    }

	public String getAction() {
		return mAction;
	}

	public void setAction(String action) {
		mAction = action;
	}
}
