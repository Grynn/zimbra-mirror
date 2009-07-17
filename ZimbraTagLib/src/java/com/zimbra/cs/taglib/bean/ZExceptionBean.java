/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
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
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.ZimbraNamespace;

import javax.servlet.jsp.JspException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ZExceptionBean {

    private ServiceException mException;
    public static class Argument{
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
            while((e instanceof JspException) && (((JspException) e).getRootCause() != null)) {
                e =  ((JspException) e).getRootCause();
            }
        }
        if ((!(e instanceof ServiceException)) && (e.getCause() instanceof ServiceException)) {
            e = e.getCause();
        }

        if (e instanceof ServiceException) {
            mException = (ServiceException) e;
        } else {
            mException = ZTagLibException.TAG_EXCEPTION(e.getMessage(), e);
        }
    }

    public Exception getException() {
        return mException;
    }

    public String getCode() {
        return mException.getCode();
    }


    public String getId() {
        return mException.getId();
    }
    
    public String getStackStrace() {
       return ExceptionToString.ToString(mException);
    }

    public List<Argument> getArguments(){
        List<Argument> args = new ArrayList<Argument>();
        try{
            if(mException instanceof SoapFaultException){
                SoapFaultException sfe = (SoapFaultException)mException;
                Element d = sfe.getDetail();
                if(d != null){
                    List<Element> l = d.getPathElementList(new String[]{ZimbraNamespace.E_ERROR.getName(), ZimbraNamespace.E_ARGUMENT.getName()});
                    if(l != null){
                        for (Element e: l){
                            args.add(new Argument(e.getAttribute(ZimbraNamespace.A_ARG_NAME,""),e.getAttribute(ZimbraNamespace.A_ARG_TYPE,""),e.getText()));
                        }
                    }
                }
            }
        }catch (Exception e){
            //ignore...
        }
        return args;
    }
}
