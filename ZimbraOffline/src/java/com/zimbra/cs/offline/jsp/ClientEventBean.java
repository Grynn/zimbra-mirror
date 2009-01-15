package com.zimbra.cs.offline.jsp;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapHttpTransport;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.cs.offline.common.OfflineConstants;

public class ClientEventBean {
	
	protected void onLogin() {
        String uri = "http://localhost:" + LC.zimbra_admin_service_port.value() + "/service/soap/";
		try {
			SoapHttpTransport transport = new SoapHttpTransport(uri);
			transport.setTimeout(5000);
			transport.setRetryCount(1);
			transport.setRequestProtocol(SoapProtocol.Soap12);
			transport.setResponseProtocol(SoapProtocol.Soap12);

			Element request = new Element.XMLElement(OfflineConstants.CLIENT_EVENT_NOTIFY_REQUEST);
			request.addAttribute(OfflineConstants.A_Event, OfflineConstants.EVENT_UI_LOAD_BEGIN);
			transport.invokeWithoutSession(request.detach());
		} catch (Exception x) {
			System.out.println("failed sending ui_load_event");
			x.printStackTrace(System.out);
		}
	}
	
	public static void onLogin(ClientEventBean bean) {
		bean.onLogin();
	}
}
