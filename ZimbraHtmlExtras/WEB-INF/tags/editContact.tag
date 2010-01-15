<%--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
--%>
<%@ tag body-content="empty" %>
<%@ attribute name="contact" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.bean.ZContactBean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>

<c:set var="noDisplayAs"><fmt:message key="noDisplayAs"/></c:set>
<table width=100% cellspacing=0 cellpadding=0>
    <tr class='contactHeaderRow'>
        <td width=20><center><app:img src="contacts/Contact.gif"/></center></td>
        <td><div
                class='contactHeader'>(New Contact)</div>
        </td>
    </tr>

</table>

<table border="0" cellpadding="0" cellspacing="3" width="100%">
    <tbody>

        <tr>
            <td width="5">&nbsp;</td>
            <td valign="top" width="385">
                 <table width=100% border="0" cellspacing='5'>
                    <tbody>
                        <app:contactEditField label="AB_FIELD_lastName" contact="${contact}" field="lastName"/>
                        <app:contactEditField label="AB_FIELD_firstName" contact="${contact}" field="firstName"/>
                        <app:contactEditField label="AB_FIELD_middleName" contact="${contact}" field="middleName"/>
                        <tr>
                            <td valign='center' class="editContactLabel"><fmt:message key="fileAs"/> :</td>
                            <td>
                                <select name="fileas">
                                    <option selected value="1"><fmt:message key="AB_FILE_AS_lastFirst"/>
                                    <option value="2"><fmt:message key="AB_FILE_AS_firstLast"/>
                                    <option value="3"><fmt:message key="AB_FILE_AS_company"/>
                                    <option value="4"><fmt:message key="AB_FILE_AS_lastFirstCompany"/>
                                    <option value="5"><fmt:message key="AB_FILE_AS_firstLastCompany"/>
                                    <option value="6"><fmt:message key="AB_FILE_AS_companyLastFirst"/>
                                    <option value="7"><fmt:message key="AB_FILE_AS_companyFirstLast"/>
                                </select>
                            </td>
                        </tr>
                    </tbody>
                 </table>
            </td>
            <td valign="top">
                <table width=100% border="0" cellspacing='5'>
                    <tbody>
                        <app:contactEditField label="AB_FIELD_jobTitle" contact="${contact}" field="jobTitle"/>
                        <app:contactEditField label="AB_FIELD_company" contact="${contact}" field="company"/>
                        <tr>
                            <td valign='center' class="editContactLabel"><fmt:message key="addressBook"/> :</td>
                            <td>
                                <select name="folderid">
                                    <zm:forEachFolder var="folder">
                                        <c:if test="${folder.isContactCreateTarget}">
                                            <option <c:if test="${folder.isContacts}">selected </c:if> value="m:${folder.id}" />${zm:repeatString('&nbsp;&nbsp;', folder.depth)}${fn:escapeXml(folder.name)}
                                        </c:if>
                                    </zm:forEachFolder>
                                </select>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>

        <tr><td colspan="4" class="sectionLabel" valign="top"><fmt:message key="email"/></td></tr>
        <tr>
            <td width="5">&nbsp;</td>
            <td valign="top" width="385">
                 <table width=100% border="0" cellspacing='5'>
                    <tbody>
                        <app:contactEditField label="AB_FIELD_email" contact="${contact}" field="email"/>
                        <app:contactEditField label="AB_FIELD_email2" contact="${contact}" field="email2"/>
                        <app:contactEditField label="AB_FIELD_email3" contact="${contact}" field="email3"/>
                    </tbody>
                 </table>
            </td>
            <td valign="top">
                <table width=100% border="0" cellspacing='5'>
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td><br></td>
        </tr>
        <tr><td colspan="4" class="sectionLabel" valign="top"><fmt:message key="work"/></td></tr>
        <tr>
            <td width="5">&nbsp;</td>
            <td valign="top" width="385">
                 <table width=100% border="0" cellspacing='5'>
                    <tbody>
                        <app:contactEditField label="AB_FIELD_workStreet" contact="${contact}" field="workStreet" address="true"/>
                        <app:contactEditField label="AB_FIELD_workCity" contact="${contact}" field="workCity"/>
                        <app:contactEditField label="AB_FIELD_workState" contact="${contact}" field="workState"/>
                        <app:contactEditField label="AB_FIELD_workPostalCode" contact="${contact}" field="workPostalCode"/>
                        <app:contactEditField label="AB_FIELD_workCountry" contact="${contact}" field="workCountry"/>
                        <app:contactEditField label="AB_FIELD_workURL" contact="${contact}" field="workURL"/>
                    </tbody>
                 </table>
            </td>
            <td valign="top">
                <table width=100% border="0" cellspacing='5'>
                    <tbody>
                        <app:contactEditField label="AB_FIELD_workPhone" contact="${contact}" field="workPhone"/>
                        <app:contactEditField label="AB_FIELD_workPhone2" contact="${contact}" field="workPhone2"/>
                        <app:contactEditField label="AB_FIELD_workFax" contact="${contact}" field="workFax"/>
                        <app:contactEditField label="AB_FIELD_assistantPhone" contact="${contact}" field="assistantPhone"/>
                        <app:contactEditField label="AB_FIELD_companyPhone" contact="${contact}" field="companyPhone"/>
                        <app:contactEditField label="AB_FIELD_callbackPhone" contact="${contact}" field="callbackPhone"/>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td><br></td>
        </tr>
        <tr><td colspan="4" class="sectionLabel" valign="top"><fmt:message key="home"/></td></tr>
        <tr>
            <td width="5">&nbsp;</td>
            <td valign="top" width="385">
                 <table width=100% border="0" cellspacing='5'>
                    <tbody>
                        <app:contactEditField label="AB_FIELD_homeStreet" contact="${contact}" field="homeStreet" address="true"/>
                        <app:contactEditField label="AB_FIELD_homeCity" contact="${contact}" field="homeCity"/>
                        <app:contactEditField label="AB_FIELD_homeState" contact="${contact}" field="homeState"/>
                        <app:contactEditField label="AB_FIELD_homePostalCode" contact="${contact}" field="homePostalCode"/>
                        <app:contactEditField label="AB_FIELD_homeCountry" contact="${contact}" field="homeCountry"/>
                        <app:contactEditField label="AB_FIELD_homeURL" contact="${contact}" field="homeURL"/>
                    </tbody>
                 </table>
            </td>
            <td valign="top">
                <table width=100% border="0" cellspacing='5'>
                    <tbody>
                        <app:contactEditField label="AB_FIELD_homePhone" contact="${contact}" field="homePhone"/>
                        <app:contactEditField label="AB_FIELD_homePhone2" contact="${contact}" field="homePhone2"/>
                        <app:contactEditField label="AB_FIELD_homeFax" contact="${contact}" field="homeFax"/>
                        <app:contactEditField label="AB_FIELD_mobilePhone" contact="${contact}" field="mobilePhone"/>
                        <app:contactEditField label="AB_FIELD_pager" contact="${contact}" field="pager"/>
                        <app:contactEditField label="AB_FIELD_carPhone" contact="${contact}" field="carPhone"/>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td><br></td>
        </tr>
        <tr><td colspan="4" class="sectionLabel" valign="top"><fmt:message key="other"/></td></tr>
        <tr>
            <td width="5">&nbsp;</td>
            <td valign="top" width="385">
                 <table width=100% border="0" cellspacing='5'>
                    <tbody>
                        <app:contactEditField label="AB_FIELD_otherStreet" contact="${contact}" field="otherStreet" address="true"/>
                        <app:contactEditField label="AB_FIELD_otherCity" contact="${contact}" field="otherCity"/>
                        <app:contactEditField label="AB_FIELD_otherState" contact="${contact}" field="otherState"/>
                        <app:contactEditField label="AB_FIELD_otherPostalCode" contact="${contact}" field="otherPostalCode"/>
                        <app:contactEditField label="AB_FIELD_otherCountry" contact="${contact}" field="otherCountry"/>
                        <app:contactEditField label="AB_FIELD_otherURL" contact="${contact}" field="otherURL"/>
                    </tbody>
                 </table>
            </td>
            <td valign="top">
                <table width=100% border="0" cellspacing='5'>
                    <tbody>
                        <app:contactEditField label="AB_FIELD_otherPhone" contact="${contact}" field="otherPhone"/>
                        <app:contactEditField label="AB_FIELD_otherFax" contact="${contact}" field="otherFax"/>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td><br></td>
        </tr>
        <tr><td colspan="4" class="sectionLabel" valign="top"><fmt:message key="notes"/></td></tr>
        <tr>
            <td colspan="4">
                <textarea rows="8" cols="60" style="width:90%" name="notes">${contact != null ? contact.notes : ''}</textarea>
            </td>
        </tr>
    </tbody>
</table>
