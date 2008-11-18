package com.zimbra.cs.account.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AccessManager;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.AccessManager.ViaGrant;
import com.zimbra.cs.account.accesscontrol.Right;

public class OfflineAccessManager extends AccessManager {

	@Override
	public boolean canAccessAccount(AuthToken at, Account target,
			boolean asAdmin) throws ServiceException {
		return true;
	}

	@Override
	public boolean canAccessAccount(AuthToken at, Account target)
			throws ServiceException {
		return true;
	}

	@Override
	public boolean canAccessAccount(Account credentials, Account target,
			boolean asAdmin) throws ServiceException {
		return true;
	}

	@Override
	public boolean canAccessAccount(Account credentials, Account target)
			throws ServiceException {
		return true;
	}

	@Override
	public boolean canAccessDomain(AuthToken at, String domainName)
			throws ServiceException {
		return true;
	}

	@Override
	public boolean canAccessDomain(AuthToken at, Domain domain)
			throws ServiceException {
		return true;
	}
	
	@Override
	public  boolean canAccessCos(AuthToken at, String cosId)
			throws ServiceException {
		return true;
	}
	
	@Override
	public boolean canAccessEmail(AuthToken at, String email)
			throws ServiceException {
		return true;
	}

	@Override
	public boolean canModifyMailQuota(AuthToken at, Account targetAccount,
			long mailQuota) throws ServiceException {
		return true;
	}

	@Override
	public boolean isDomainAdminOnly(AuthToken at) {
		return false;
	}
	
	@Override
	public boolean canDo(AuthToken grantee, Entry target, Right rightNeeded, boolean asAdmin, boolean defaultGrant) {
	    return defaultGrant;
	}
    
	@Override
	public boolean canDo(Account grantee, Entry target, Right rightNeeded, boolean asAdmin, boolean defaultGrant) {
	    return defaultGrant;
	}
	
	@Override
	public boolean canDo(String grantee, Entry target, Right rightNeeded, boolean asAdmin, boolean defaultGrant) {
	    return defaultGrant;
	}
	
	@Override
    public AllowedAttrs canGetAttrs(Account grantee, Entry target, Map<String, Object> attrs) {
        return ALLOW_ALL_ATTRS();
    }
    
	@Override
    public AllowedAttrs canGetAttrs(AuthToken grantee, Entry target, Map<String, Object> attrs) {
        return ALLOW_ALL_ATTRS();
    }
	
	@Override
    public AllowedAttrs canSetAttrs(Account grantee, Entry target, Map<String, Object> attrs) {
        return ALLOW_ALL_ATTRS();
    }
	
	@Override
    public AllowedAttrs canSetAttrs(AuthToken grantee, Entry target, Map<String, Object> attrs) {
        return ALLOW_ALL_ATTRS();
    }
	

}
