package com.zimbra.cs.client.soap;
import com.zimbra.cs.versioncheck.VersionUpdate;
import java.util.List;
import java.util.ArrayList;
/**
 * @author Greg Solovyev
 */
public class LmcVersionCheckResponse extends LmcSoapResponse {
	private List <VersionUpdate> updates;
	private boolean status;
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public LmcVersionCheckResponse() {
		updates = new ArrayList();
	}
	
	public void addUpdate(VersionUpdate upd) {
		updates.add(upd);
	}
	
	public List <VersionUpdate> getUpdates() {
		return updates;
	}
}
