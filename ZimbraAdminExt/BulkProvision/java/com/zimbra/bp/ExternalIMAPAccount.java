package com.zimbra.bp;

import com.zimbra.cs.account.Account;

public class ExternalIMAPAccount {
    private String userEmail;
    private String userLogin;
    private String userPassword;
    private Account account;
    
    public ExternalIMAPAccount(String uEmail,String uLogin,String uPassword, Account acct) {
        userEmail = uEmail;
        userLogin = uLogin;
        userPassword = uPassword;
        account = acct;
    }
    
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getUserLogin() {
        return userLogin;
    }
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
    public String getUserPassword() {
        return userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}