/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ExceptionToString;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.ZimbraNamespace;
import com.zimbra.cs.account.AccountServiceException;

import javax.servlet.jsp.JspException;
import java.util.List;
import java.util.ArrayList;

public class ZExceptionBean {

    private ServiceException exception;

    public static class Argument {
        private String name, type, val;

        Argument(String name,String type,String val){
            this.type = type;
            this.name = name;
            this.val = val;
        }

        public String getName(){
            return this.name;
        }

        public String getType(){
            return this.type;
        }

        public String getVal(){
            return this.val;
        }
    }

    public ZExceptionBean(Throwable e) {
        if (e instanceof JspException) {
            while ((e instanceof JspException) && (((JspException) e).getRootCause() != null)) {
                e =  ((JspException) e).getRootCause();
            }
        }
        if ((!(e instanceof ServiceException)) && (e.getCause() instanceof ServiceException)) {
            e = e.getCause();
        }
        if( e instanceof SoapFaultException)  {
            if(AccountServiceException.AUTH_FAILED.equals(((SoapFaultException)e).getCode())) {
                ZimbraLog.webclient.debug(e.getMessage(), e);
            } else {
                ZimbraLog.webclient.error(e.getMessage(), e);
            }
        }
        else{
            ZimbraLog.webclient.error(e.getMessage(), e);
        }

        if (e instanceof ServiceException) {
            exception = (ServiceException) e;
        } else {
            exception = ZTagLibException.TAG_EXCEPTION(e.getMessage(), e);
        }
    }

    public Exception getException() {
        return exception;
    }

    public String getCode() {
        return exception.getCode();
    }

    public String getId() {
        return exception.getId();
    }

    public String getStackStrace() {
       return ExceptionToString.ToString(exception);
    }

    public List<Argument> getArguments(){
        List<Argument> args = new ArrayList<Argument>();
        try {
            if (exception instanceof SoapFaultException) {
                SoapFaultException sfe = (SoapFaultException) exception;
                Element d = sfe.getDetail();
                if (d != null) {
                    List<Element> list = d.getPathElementList(
                            new String[] {ZimbraNamespace.E_ERROR.getName(), ZimbraNamespace.E_ARGUMENT.getName()});
                    if (list != null) {
                        for (Element el : list) {
                            args.add(new Argument(el.getAttribute(ZimbraNamespace.A_ARG_NAME,""),
                                    el.getAttribute(ZimbraNamespace.A_ARG_TYPE, ""), el.getText()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            //ignore...
        }
        return args;
    }
}
