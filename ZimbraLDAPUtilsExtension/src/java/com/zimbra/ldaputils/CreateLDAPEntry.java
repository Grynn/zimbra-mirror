package com.zimbra.ldaputils;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;
import javax.naming.directory.SchemaViolationException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.AttributeManager;

import com.zimbra.cs.account.ldap.LdapUtil;

import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;

import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.cs.account.NamedEntry;
/**
 * @author Greg Solovyev
 */
public class CreateLDAPEntry extends AdminDocumentHandler {

	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {
       
		ZimbraSoapContext lc = getZimbraSoapContext(context);
	    	    
	    String dn = request.getAttribute(ZimbraLDAPUtilsService.E_DN);
	    Map<String, Object> attrs = AdminService.getAttrs(request, true);
	    
	    
	    NamedEntry ne = createLDAPEntry(dn,  attrs);

        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
                new String[] {"cmd", "CreateLDAPEntry","dn", dn}, attrs));

	    Element response = lc.createElement(ZimbraLDAPUtilsService.CREATE_LDAP_ENTRY_RESPONSE);
	    ZimbraLDAPUtilsService.encodeLDAPEntry(response,ne);
	    

	    return response;
	}
	

    public NamedEntry createLDAPEntry(String dn, Map<String, Object> entryAttrs) throws ServiceException {
        HashMap attrManagerContext = new HashMap();
        AttributeManager.getInstance().preModify(entryAttrs, null, attrManagerContext, true, true);

        DirContext ctxt = null;
        try {
            ctxt = LdapUtil.getDirContext(true);

            Attributes attrs = new BasicAttributes(true);
            LdapUtil.mapToAttrs(entryAttrs, attrs);

            createSubcontext(ctxt, dn, attrs, "createLDAPEntry");

            NamedEntry entry = GetLDAPEntries.getObjectByDN(dn, ctxt);
            AttributeManager.getInstance().postModify(entryAttrs, entry, attrManagerContext, true);
            return entry;

        } catch (NameAlreadyBoundException nabe) {
            throw ZimbraLDAPUtilsServiceException.DN_EXISTS(dn);
        } finally {
            LdapUtil.closeContext(ctxt);
        }
    }
    /** 
     * @see com.zimbra.cs.account.Provisioning#createSubcontext(javax.naming.directory.Dircontext,java.lang.String,javax.naming.directory.Attributes,java.lang.String)
     **/
    
    private void createSubcontext(DirContext ctxt, String dn, Attributes attrs, String method)
    throws NameAlreadyBoundException, ServiceException {
        Context newCtxt = null;
        try {
            newCtxt = ctxt.createSubcontext(dn, attrs);
        } catch (NameAlreadyBoundException e) {            
            throw e;
        } catch (InvalidAttributeIdentifierException e) {
            throw AccountServiceException.INVALID_ATTR_NAME(method+" invalid attr name: "+e.getMessage(), e);
        } catch (InvalidAttributeValueException e) {
            throw AccountServiceException.INVALID_ATTR_VALUE(method+" invalid attr value: "+e.getMessage(), e);
        } catch (InvalidAttributesException e) {
            throw ServiceException.INVALID_REQUEST(method+" invalid set of attributes: "+e.getMessage(), e);
        } catch (InvalidNameException e) {
            throw ServiceException.INVALID_REQUEST(method+" invalid name: "+e.getMessage(), e);
        } catch (SchemaViolationException e) {
            throw ServiceException.INVALID_REQUEST(method+" invalid schema change: "+e.getMessage(), e);            
        } catch (NamingException e) {
            throw ServiceException.FAILURE(method, e);
        } finally {
            LdapUtil.closeContext(newCtxt);
        }
    }
}
