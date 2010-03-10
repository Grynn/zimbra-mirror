package com.zimbra.examples.extns.customauth;

import com.zimbra.common.soap.Element;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple authentication mechanism that reads usernames/passwords from a file.
 *
 * @author vmahajan
 */
public class SimpleAuth extends ZimbraCustomAuth {

    private static Map<String, String> userPassMap = new HashMap<String, String>();

    static {
        try {
            Element usersElt = Element.parseXML(new FileInputStream("/opt/zimbra/conf/users.xml"));
            List<Element> userEltList = usersElt.getPathElementList(new String[]{"user"});
            for (Element userElt : userEltList) {
                userPassMap.put(userElt.getAttribute("name"), userElt.getAttribute("password"));
            }
        } catch (Exception e) {
            ZimbraLog.extensions.error(SystemUtil.getStackTrace(e));
        }
    }

    /**
     * Authenticates account.
     *
     * @param acct
     * @param password
     * @param context
     * @param args
     * @throws Exception
     */
    public void authenticate(Account acct, String password, Map<String, Object> context, List<String> args)
            throws Exception {
        String username = acct.getName();
        if (userPassMap.containsKey(username)) {
            if (!userPassMap.get(username).equals(password))
                throw new Exception("Invalid password");
        } else {
            throw new Exception("Invalid user name \"" + username + "\"");
        }

    }
}
