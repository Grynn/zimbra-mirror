package com.zimbra.ldaputils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.AttributeManager;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.NamedEntry.Visitor;
import com.zimbra.cs.account.callback.CallbackContext;
import com.zimbra.cs.account.ldap.LdapProv;
import com.zimbra.cs.ldap.IAttributes;
import com.zimbra.cs.ldap.LdapClient;
import com.zimbra.cs.ldap.LdapServerType;
import com.zimbra.cs.ldap.LdapUsage;
import com.zimbra.cs.ldap.SearchLdapOptions;
import com.zimbra.cs.ldap.ZAttributes;
import com.zimbra.cs.ldap.ZLdapContext;
import com.zimbra.cs.ldap.ZMutableEntry;
import com.zimbra.cs.ldap.ZSearchScope;
import com.zimbra.cs.ldap.IAttributes.CheckBinary;
import com.zimbra.cs.ldap.LdapException.LdapEntryAlreadyExistException;
import com.zimbra.cs.ldap.LdapException.LdapEntryNotFoundException;
import com.zimbra.cs.ldap.LdapException.LdapSizeLimitExceededException;
import com.zimbra.cs.ldap.SearchLdapOptions.SearchLdapVisitor;

abstract class LDAPUtilsHelper {
    
    private static LDAPUtilsHelper SINGLETON = null;
    
    static synchronized LDAPUtilsHelper getInstance() {
        if (SINGLETON == null) {
            SINGLETON = new ZLDAPUtilsHelper();
        }
        
        return SINGLETON;
    }

    abstract NamedEntry createLDAPEntry(String dn, Map<String, Object> entryAttrs) 
    throws ServiceException;
    
    abstract void deleteLDAPEntry(String dn) throws ServiceException;
    
    abstract NamedEntry modifyLDAPEntry(String dn,  Map<String, Object> attrs)
    throws ServiceException;
    
    abstract NamedEntry renameLDAPEntry(String dn,  String newDN)
    throws ServiceException;
    
    abstract void searchObjects(String query,  String base, NamedEntry.Visitor visitor)
    throws ServiceException;
    
    List<NamedEntry> searchObjects(String query, String base, final String sortAttr, final boolean sortAscending)
    throws ServiceException {
        final List<NamedEntry> result = new ArrayList<NamedEntry>();
        
        NamedEntry.Visitor visitor = new NamedEntry.Visitor() {
            public void visit(NamedEntry entry) {
                result.add(entry);
            }
        };
        
        searchObjects(query,  base,  visitor);

        final boolean byName = sortAttr == null || sortAttr.equals("name"); 
        Comparator<NamedEntry> comparator = new Comparator<NamedEntry>() {
            public int compare(NamedEntry oa, NamedEntry ob) {
                NamedEntry a = (NamedEntry) oa;
                NamedEntry b = (NamedEntry) ob;
                int comp = 0;
                if (byName)
                    comp = a.getName().compareToIgnoreCase(b.getName());
                else {
                    String sa = a.getAttr(sortAttr);
                    String sb = b.getAttr(sortAttr);
                    if (sa == null) sa = "";
                    if (sb == null) sb = "";
                    comp = sa.compareToIgnoreCase(sb);
                }
                return sortAscending ? comp : -comp;
            }
        };
        Collections.sort(result, comparator);        
        return result;

    }
    
    private static class ZLDAPUtilsHelper extends LDAPUtilsHelper {
        
        @Override
        NamedEntry createLDAPEntry(String dn, Map<String, Object> entryAttrs)
        throws ServiceException {
            CallbackContext callbackContext = new CallbackContext(CallbackContext.Op.CREATE);
            AttributeManager.getInstance().preModify(entryAttrs, null, callbackContext, true);
            
            ZMutableEntry entry = LdapClient.createMutableEntry();
            entry.mapToAttrs(entryAttrs);
            entry.setDN(dn);
            
            ZLdapContext zlc = null;
            try {
                zlc = LdapClient.getContext(LdapServerType.MASTER, LdapUsage.UNITTEST);
                zlc.createEntry(entry);
                
                NamedEntry namedEntry = getObjectByDN(dn, zlc);
                AttributeManager.getInstance().postModify(entryAttrs, namedEntry, callbackContext);
                return namedEntry;
            } catch (LdapEntryAlreadyExistException nabe) {   
                throw ZimbraLDAPUtilsServiceException.DN_EXISTS(dn);
            } finally {
                LdapClient.closeContext(zlc);
            }
        }

        @Override
        void deleteLDAPEntry(String dn) throws ServiceException {
            ZLdapContext zlc = null;
            try {
                zlc = LdapClient.getContext(LdapServerType.MASTER, LdapUsage.UNITTEST);
                zlc.deleteChildren(dn);
                zlc.deleteEntry(dn);
            } catch (ServiceException e) {
                throw ServiceException.FAILURE("unable to purge dn: "+dn, e);
            } finally {
                LdapClient.closeContext(zlc);
            }
        }

        @Override
        NamedEntry modifyLDAPEntry(String dn, Map<String, Object> attrs) 
        throws ServiceException {
            
            Provisioning prov = Provisioning.getInstance();
            if (!(prov instanceof LdapProv)) {
                throw new UnsupportedOperationException();
            }
            LdapProv ldapProv = (LdapProv) prov;
            
            ZLdapContext zlc = null;
            try {
                zlc = LdapClient.getContext(LdapServerType.MASTER, LdapUsage.MODIFY_ENTRY);
                
                LDAPUtilEntry ne = getObjectByDN(dn, zlc);
                if (ne==null) {
                    throw ServiceException.FAILURE("Cannot find an object for DN "+dn, null);
                }
                
                ldapProv.getHelper().modifyAttrs(zlc, dn, attrs, ne);
                                
                LDAPUtilEntry newNe = getObjectByDN(dn, zlc);
                return newNe;

            } catch (ServiceException e) {
                throw ServiceException.FAILURE("unable to modify attrs: "
                        + e.getMessage(), e);
            } finally {
                LdapClient.closeContext(zlc);
            }

        }

        @Override
        NamedEntry renameLDAPEntry(String dn, String newDN)
                throws ServiceException {
            ZLdapContext zlc = null;
            try {
                zlc = LdapClient.getContext(LdapServerType.MASTER, LdapUsage.UNITTEST);
                zlc.renameEntry(dn, newDN);
                NamedEntry ne = getObjectByDN(newDN, zlc);
                return ne;
            } catch (LdapEntryAlreadyExistException nabe) {
                throw ZimbraLDAPUtilsServiceException.DN_EXISTS(newDN);         
            } catch (ServiceException e) {
                throw ServiceException.FAILURE("unable to rename dn: "+dn+ "to " +newDN, e);
            } finally {
                LdapClient.closeContext(zlc);
            }
        }
        
        private LDAPUtilEntry getObjectByDN(String dn, ZLdapContext initZlc) throws ServiceException {
            ZLdapContext zlc = initZlc;
            try {
                if (zlc == null) {
                    zlc = LdapClient.getContext(LdapUsage.UNITTEST);
                }
                
                ZAttributes attrs = zlc.getAttributes(dn, null);
                LDAPUtilEntry ne = new LDAPUtilEntry(dn, attrs,null);
                return ne;
                
            } catch (LdapEntryNotFoundException e) {
                return null;
            } catch (ServiceException e) {
                throw ServiceException.FAILURE("unable to find dn: "+dn+" message: "+e.getMessage(), e);
            } finally {
                if (initZlc == null) {
                    LdapClient.closeContext(zlc);
                }
            }
        }

        private static class SearchObjectsVisitor extends SearchLdapVisitor {
            private NamedEntry.Visitor visitor;
            
            SearchObjectsVisitor(Visitor visitor) {
                super(false);
                this.visitor = visitor;
            }
            
            @Override
            public void visit(String dn, IAttributes ldapAttrs) {
                try {
                    doVisit(dn, ldapAttrs);
                } catch (ServiceException e) {
                    ZimbraLog.account.warn("entry skipped, encountered error while processing entry at:" + dn);
                }
            }
            
            private void doVisit(String dn, IAttributes ldapAttrs) throws ServiceException {
                List<String> objectclass = ldapAttrs.getMultiAttrStringAsList(Provisioning.A_objectClass, 
                        CheckBinary.NOCHECK);

                // skip admin accounts
                if (dn.endsWith("cn=zimbra")) {
                    return;
                }
                
                ZAttributes attrs = (ZAttributes)ldapAttrs;

                if (objectclass.contains("sambaDomain")) {
                    visitor.visit(new SambaDomain(dn, attrs, null));                        
                } else if (objectclass.contains("posixGroup")) {
                    visitor.visit(new PosixGroup(dn, attrs, null));
                } else if (objectclass.contains("posixAccount")) {
                    visitor.visit(new PosixAccount(dn, attrs, null));
                } else {
                    visitor.visit(new LDAPUtilEntry(dn, attrs, null));
                }
            }
        }
        
        @Override
        void searchObjects(String query, String base, Visitor visitor)
                throws ServiceException {
            
            SearchObjectsVisitor searchObjectsVisitor = new SearchObjectsVisitor(visitor);
            
            SearchLdapOptions searchObjectsOptions = new SearchLdapOptions(base, query, 
                    SearchLdapOptions.RETURN_ALL_ATTRS, SearchLdapOptions.SIZE_UNLIMITED, 
                    null, ZSearchScope.SEARCH_SCOPE_SUBTREE, 
                    searchObjectsVisitor);
                    
            ZLdapContext zlc = null;
            try {
                zlc = LdapClient.getContext(LdapUsage.UNITTEST);
                zlc.searchPaged(searchObjectsOptions);
            } catch (LdapSizeLimitExceededException e) {
                throw AccountServiceException.TOO_MANY_SEARCH_RESULTS("too many search results returned", e);
            } catch (ServiceException e) {
                throw ServiceException.FAILURE("unable to list all objects", e);
            } finally {
                LdapClient.closeContext(zlc);
            }
            
        }
        
    }
}
