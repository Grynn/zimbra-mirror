package com.zimbra.cs.account.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AccessManager;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.Domain;

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
}
